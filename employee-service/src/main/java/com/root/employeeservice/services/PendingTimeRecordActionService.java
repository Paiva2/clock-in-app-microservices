package com.root.employeeservice.services;

import com.root.crossdbservice.entities.PendingTimeRecordAction;
import com.root.crossdbservice.entities.UserEntity;
import com.root.crossdbservice.repositories.PendingTimeRecordActionRepository;
import com.root.crossdbservice.repositories.UserRepository;
import com.root.employeeservice.enums.OrderBy;
import com.root.employeeservice.exceptions.BadRequestException;
import com.root.employeeservice.exceptions.NotFoundException;
import com.root.employeeservice.specifications.PendingTimeRecordActionSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

@Service
public class PendingTimeRecordActionService {
    private final UserRepository userRepository;
    private final PendingTimeRecordActionRepository pendingTimeRecordActionRepository;
    private final PendingTimeRecordActionSpecification pendingTimeRecordActionSpecification;

    public PendingTimeRecordActionService(
            PendingTimeRecordActionRepository pendingTimeRecordActionRepository,
            UserRepository userRepository,
            PendingTimeRecordActionSpecification pendingTimeRecordActionSpecification
    ) {
        this.pendingTimeRecordActionRepository = pendingTimeRecordActionRepository;
        this.userRepository = userRepository;
        this.pendingTimeRecordActionSpecification = pendingTimeRecordActionSpecification;
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
