package com.root.employeeservice.services;

import com.root.crossdbservice.entities.*;
import com.root.crossdbservice.repositories.PendingTimeRecordActionRepository;
import com.root.crossdbservice.repositories.TimeRecordRepository;
import com.root.crossdbservice.repositories.UserManagerRepository;
import com.root.crossdbservice.repositories.UserRepository;
import com.root.employeeservice.enums.OrderBy;
import com.root.employeeservice.exceptions.*;
import com.root.employeeservice.specifications.PendingTimeRecordActionSpecification;
import com.root.employeeservice.strategy.concrete.ClonePropertiesBeanUtilsStrategy;
import com.root.employeeservice.strategy.concrete.TimeRecordDateValidationStrategy;
import com.root.employeeservice.strategy.context.Cloner;
import com.root.employeeservice.strategy.context.DateValidator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;

@Service
public class PendingTimeRecordActionService {
    private final UserRepository userRepository;
    private final PendingTimeRecordActionRepository pendingTimeRecordActionRepository;
    private final PendingTimeRecordActionSpecification pendingTimeRecordActionSpecification;
    private final UserManagerRepository userManagerRepository;
    private final TimeRecordRepository timeRecordRepository;

    private final Cloner cloner;
    private final DateValidator dateValidator;

    private final List<String> rolesWithPermissionToBeSuperior = Arrays.asList(
            RoleEntity.Role.ADMIN.getRoleValue(),
            RoleEntity.Role.HUMAN_RESOURCES.getRoleValue()
    );

    public PendingTimeRecordActionService(
            PendingTimeRecordActionRepository pendingTimeRecordActionRepository,
            UserRepository userRepository,
            PendingTimeRecordActionSpecification pendingTimeRecordActionSpecification, UserManagerRepository userManagerRepository, TimeRecordRepository timeRecordRepository
    ) {
        this.pendingTimeRecordActionRepository = pendingTimeRecordActionRepository;
        this.userRepository = userRepository;
        this.pendingTimeRecordActionSpecification = pendingTimeRecordActionSpecification;
        this.userManagerRepository = userManagerRepository;
        this.timeRecordRepository = timeRecordRepository;

        this.cloner = new Cloner(new ClonePropertiesBeanUtilsStrategy());
        this.dateValidator = new DateValidator(new TimeRecordDateValidationStrategy());
    }

    public PendingTimeRecordAction updatePendingTimeRecord(
            UUID employeeId,
            PendingTimeRecordAction pendingTimeRecordAction
    ) {
        if (employeeId == null) {
            throw new BadRequestException("Employee id can't be empty");
        }

        if (pendingTimeRecordAction == null) {
            throw new BadRequestException("Pending time record action can't be empty");
        } else if (pendingTimeRecordAction.getId() == null) {
            throw new BadRequestException("Pending time record action id can't be empty");
        }

        boolean isChangingActionToUpdate =
                pendingTimeRecordAction.getActionType().equals(PendingTimeRecordAction.ActionType.UPDATE);

        UserEntity getEmployee = this.userRepository.findById(employeeId)
                .orElseThrow(() -> new NotFoundException("Employee not found"));

        if (getEmployee.getDisabled()) {
            throw new ForbiddenException("Employee is disabled");
        }

        PendingTimeRecordAction getPendingAction = this.pendingTimeRecordActionRepository.findById(
                pendingTimeRecordAction.getId()
        ).orElseThrow(() -> new NotFoundException("Pending Action not found"));

        TimeRecord originalTimeRecord = getPendingAction.getTimeRecord();

        if (!originalTimeRecord.getUser().getId().equals(employeeId)) {
            throw new ForbiddenException("Insufficient permissions");
        }

        if (isChangingActionToUpdate && pendingTimeRecordAction.getTimeUpdated() != null) {
            this.dateValidator.checkIfDatesAreEquals(
                    pendingTimeRecordAction.getTimeUpdated(),
                    originalTimeRecord.getRecordHour()
            );
        }

        if (getPendingAction.isActionDone()) {
            throw new ConflictException("Done actions can't be updated");
        }

        this.cloner.cloneNonNullProps(pendingTimeRecordAction, getPendingAction);

        if (!isChangingActionToUpdate) {
            getPendingAction.setTimeUpdated(null);
        }

        PendingTimeRecordAction updatedAction = this.pendingTimeRecordActionRepository.save(getPendingAction);

        return updatedAction;
    }

    public Page<PendingTimeRecordAction> listOwnPendingActions(
            UUID userId,
            boolean actionDone,
            int page,
            int perPage,
            int month,
            int year,
            int day
    ) {
        if (userId == null) {
            throw new BadRequestException("User id can't be empty");
        }

        if (page < 1) {
            page = 1;
        }

        if (perPage > 50) {
            perPage = 50;
        } else if (perPage < 5) {
            perPage = 5;
        }

        Pageable pageable = PageRequest.of(page - 1, perPage, Sort.Direction.DESC, "createdAt");

        UserEntity getEmployee = this.userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Employee not found"));

        Specification<PendingTimeRecordAction> specification = Specification.where(
                        this.pendingTimeRecordActionSpecification.employeeEq(getEmployee.getId())
                ).and(this.pendingTimeRecordActionSpecification.actionDone(actionDone))
                .and(month > 0 ? this.pendingTimeRecordActionSpecification.monthEq(month) : null)
                .and(year > 0 ? this.pendingTimeRecordActionSpecification.yearEq(year) : null)
                .and(day > 0 ? this.pendingTimeRecordActionSpecification.dayEq(day) : null);

        Page<PendingTimeRecordAction> pageList =
                this.pendingTimeRecordActionRepository.findAll(specification, pageable);

        return pageList;
    }

    @Transactional
    public TimeRecord confirmUpdate(UUID managerId, UUID pendingTimeRecordActionId) {
        if (managerId == null) {
            throw new BadRequestException("Manager id can't be empty");
        }

        if (pendingTimeRecordActionId == null) {
            throw new BadRequestException("Pending time record action can't be empty");
        }

        UserEntity getManager = this.userRepository.findById(managerId)
                .orElseThrow(() -> new NotFoundException("Manager not found"));

        PendingTimeRecordAction getPendingAction =
                this.pendingTimeRecordActionRepository.findById(pendingTimeRecordActionId)
                        .orElseThrow(() -> new NotFoundException("Pending action not found"));

        if (!getPendingAction.getActionType().equals(PendingTimeRecordAction.ActionType.UPDATE)) {
            throw new BadRequestException("Pending Action must be an UPDATE type");
        }

        if (getPendingAction.isActionDone()) {
            throw new ForbiddenException("Action is already done");
        }

        Optional<UserManager> findUserManager = this.userManagerRepository.findByUserAndManager(
                getPendingAction.getTimeRecord().getUser().getId(),
                getManager.getId()
        );

        if (!findUserManager.isPresent()) {
            getManager.getUserRoles().stream().filter(role -> {
                        String managerRole = role.getRole().getRoleName().getRoleValue();
                        return rolesWithPermissionToBeSuperior.contains(managerRole);
                    }
            ).findFirst().orElseThrow(() -> new UnauthorizedException(
                    "Only Admins, Human Resources or User Manager can handle pending User actions"
            ));
        }

        TimeRecord getTimeRecordToUpdate = this.timeRecordRepository.findById(
                getPendingAction.getTimeRecord().getId()
        ).orElseThrow(() -> new NotFoundException("Time Record not found"));

        if (getTimeRecordToUpdate.getDisabled()) {
            throw new ConflictException("Time record is disabled");
        }

        getTimeRecordToUpdate.setRecordHour(getPendingAction.getTimeUpdated());
        TimeRecord updatedTimeRecord = this.timeRecordRepository.save(getTimeRecordToUpdate);

        getPendingAction.setActionDone(true);
        this.pendingTimeRecordActionRepository.save(getPendingAction);

        return updatedTimeRecord;
    }

    @Transactional
    public void confirmDeletion(UUID managerId, UUID pendingTimeRecordActionId) {
        if (managerId == null) {
            throw new BadRequestException("Manager id can't be empty");
        }

        if (pendingTimeRecordActionId == null) {
            throw new BadRequestException("Pending time record action can't be empty");
        }

        PendingTimeRecordAction getTimeRecordAction =
                this.pendingTimeRecordActionRepository.findById(pendingTimeRecordActionId)
                        .orElseThrow(() -> new NotFoundException("Pending time record action not found"));

        if (getTimeRecordAction.isActionDone()) {
            throw new ForbiddenException("Action is already done");
        }

        if (!getTimeRecordAction.getActionType().equals(PendingTimeRecordAction.ActionType.DELETE)) {
            throw new BadRequestException("Pending Action must be an DELETE type");
        }

        UserEntity getManager = this.userRepository.findById(managerId)
                .orElseThrow(() -> new NotFoundException("Manager not found"));

        Optional<UserManager> doesManagerAndUserMatches = this.userManagerRepository.findByUserAndManager(
                getTimeRecordAction.getTimeRecord().getUser().getId(),
                getManager.getId()
        );

        if (!doesManagerAndUserMatches.isPresent()) {
            getManager.getUserRoles().stream().filter(role -> {
                        String managerRole = role.getRole().getRoleName().getRoleValue();
                        return rolesWithPermissionToBeSuperior.contains(managerRole);
                    }
            ).findFirst().orElseThrow(() -> new UnauthorizedException(
                    "Only Admins, Human Resources or User Manager can handle pending User actions"
            ));
        }

        TimeRecord getTimeRecord = this.timeRecordRepository.findById(
                getTimeRecordAction.getTimeRecord().getId()
        ).orElseThrow(() -> new NotFoundException("Time record not found"));

        if (getTimeRecord.getDisabled()) {
            throw new ConflictException("Time record is already disabled");
        }

        getTimeRecord.setDisabled(true);
        this.timeRecordRepository.save(getTimeRecord);

        getTimeRecordAction.setActionDone(true);
        this.pendingTimeRecordActionRepository.save(getTimeRecordAction);
    }

    public Page<PendingTimeRecordAction> listPendingToUpdate(
            UUID managerId,
            int page,
            int perPage,
            String employeeName,
            Date minDate,
            Date maxDate,
            OrderBy orderBy
    ) {
        if (managerId == null) {
            throw new BadRequestException("managerId can't be null");
        }

        if (page < 1) {
            page = 1;
        }

        if (perPage < 5) {
            perPage = 5;
        } else if (perPage > 50) {
            perPage = 50;
        }

        return this.listAll(managerId,
                page,
                perPage,
                employeeName,
                minDate,
                maxDate,
                orderBy,
                PendingTimeRecordAction.ActionType.UPDATE
        );
    }

    public Page<PendingTimeRecordAction> listPendingToDelete(
            UUID managerId,
            int page,
            int perPage,
            String employeeName,
            Date minDate,
            Date maxDate,
            OrderBy orderBy
    ) {
        if (managerId == null) {
            throw new BadRequestException("managerId can't be null");
        }

        if (page < 1) {
            page = 1;
        }

        if (perPage < 5) {
            perPage = 5;
        } else if (perPage > 50) {
            perPage = 50;
        }

        return this.listAll(managerId,
                page,
                perPage,
                employeeName,
                minDate,
                maxDate,
                orderBy,
                PendingTimeRecordAction.ActionType.DELETE
        );
    }

    private Page<PendingTimeRecordAction> listAll(
            UUID managerId,
            int page,
            int perPage,
            String employeeName,
            Date minDate,
            Date maxDate,
            OrderBy orderBy,
            PendingTimeRecordAction.ActionType actionType
    ) {
        UserEntity getManager = this.userRepository.findById(managerId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        PageRequest pageable =
                PageRequest.of(
                        page - 1,
                        perPage,
                        Sort.Direction.valueOf(orderBy.getOrderByValue().toUpperCase()),
                        "createdAt"
                );

        PendingTimeRecordActionSpecification timeRecordSpec = this.pendingTimeRecordActionSpecification;

        Specification<PendingTimeRecordAction> specification
                = Specification.where(timeRecordSpec.managerEq(getManager.getId())
                        .and(timeRecordSpec.actionEqual(actionType))
                        .and(timeRecordSpec.actionDone(false))
                )
                .and(employeeName != null ? timeRecordSpec.employeeNameLike(employeeName) : null)
                .and(maxDate != null ? timeRecordSpec.maxDate(maxDate) : null)
                .and(minDate != null ? timeRecordSpec.minDate(minDate) : null);

        return this.pendingTimeRecordActionRepository.findAll(specification, pageable);
    }
}
