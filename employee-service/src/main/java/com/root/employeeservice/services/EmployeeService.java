package com.root.employeeservice.services;

import com.root.crossdbservice.entities.RoleEntity;
import com.root.crossdbservice.entities.UserEntity;
import com.root.crossdbservice.entities.UserManager;
import com.root.crossdbservice.entities.UserRole;
import com.root.crossdbservice.repositories.RoleRepository;
import com.root.crossdbservice.repositories.UserManagerRepository;
import com.root.crossdbservice.repositories.UserRepository;
import com.root.crossdbservice.repositories.UserRoleRepository;
import com.root.employeeservice.enums.OrderBy;
import com.root.employeeservice.exceptions.*;
import com.root.employeeservice.specifications.EmployeeSpecification;
import com.root.employeeservice.strategy.concrete.ClonePropertiesBeanUtilsStrategy;
import com.root.employeeservice.strategy.concrete.EncryptBcryptStrategy;
import com.root.employeeservice.strategy.concrete.MailValidatorRegexStrategy;
import com.root.employeeservice.strategy.context.Cloner;
import com.root.employeeservice.strategy.context.Encryptor;
import com.root.employeeservice.strategy.context.MailValidator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;

@Service
public class EmployeeService {
    protected final List<String> rolesWithPermissionToBeSuperior = Arrays.asList(
            RoleEntity.Role.ADMIN.getRoleValue(),
            RoleEntity.Role.MANAGER.getRoleValue(),
            RoleEntity.Role.HUMAN_RESOURCES.getRoleValue()
    );

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final RoleRepository roleRepository;
    private final UserManagerRepository userManagerRepository;
    private final EmployeeSpecification employeeSpecification;

    private final Cloner cloneProperties;
    private final Encryptor passwordEncoder;
    private final MailValidator mailValidator;

    public EmployeeService(
            UserRepository userRepository,
            UserRoleRepository userRoleRepository,
            RoleRepository roleRepository,
            UserManagerRepository userManagerRepository, EmployeeSpecification employeeSpecification
    ) {
        this.userRepository = userRepository;
        this.userManagerRepository = userManagerRepository;
        this.userRoleRepository = userRoleRepository;
        this.roleRepository = roleRepository;
        this.employeeSpecification = employeeSpecification;
        this.cloneProperties = new Cloner(new ClonePropertiesBeanUtilsStrategy());
        this.passwordEncoder = new Encryptor(new EncryptBcryptStrategy());
        this.mailValidator = new MailValidator(new MailValidatorRegexStrategy());
    }

    public UserEntity requesterProfile(UUID userId) {
        if (userId == null) {
            throw new BadRequestException("userId can't be empty");
        }

        UserEntity findUser = this.userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        if (findUser.getDisabled()) {
            throw new ForbiddenException("User is disabled");
        }

        return findUser;
    }

    public void registerHumanResource(UserEntity newHr) {
        this.registerUser(newHr, RoleEntity.Role.HUMAN_RESOURCES, "Human Resources");
    }

    public void registerBasic(UserEntity newUser) {
        this.registerUser(newUser, RoleEntity.Role.USER, newUser.getPosition());
    }

    public void registerManager(UserEntity newManager) {
        this.registerUser(newManager, RoleEntity.Role.MANAGER, newManager.getPosition());
    }

    public List<UserEntity> listEmployeesByRoleUnpaginable(RoleEntity.Role role) {
        if (role == null) {
            throw new BadRequestException("Role can't be empty");
        }

        List<UserEntity> employees = this.userRepository.findAllByRoleCustom(role.getRoleValue());

        return employees;
    }

    public Page<UserEntity> listAllEmployees(
            int page,
            int perPage,
            OrderBy orderBy,
            String name,
            String email,
            String position,
            boolean disabled
    ) {
        if (page < 1) {
            page = 1;
        }

        if (perPage < 5) {
            perPage = 5;
        } else if (perPage > 50) {
            perPage = 50;
        }

        Pageable pageable = PageRequest.of(
                page - 1,
                perPage,
                Sort.Direction.fromString(orderBy.name()),
                "createdAt"
        );

        Specification<UserEntity> querySpecification = Specification.where(
                        name != null ? this.employeeSpecification.nameLike(name) : null
                ).or(email != null ? this.employeeSpecification.emailLike(email) : null)
                .or(position != null ? this.employeeSpecification.positionLike(position) : null)
                .and(this.employeeSpecification.isDisabled(disabled));

        Page<UserEntity> listFiltered = this.userRepository.findAll(querySpecification, pageable);

        return listFiltered;
    }

    public UserManager attachSuperiorToEmployee(UUID employeeId, UUID superiorId) {
        if (employeeId == null) {
            throw new BadRequestException("employeeId can't be empty");
        }

        if (superiorId == null) {
            throw new BadRequestException("superiorId can't be empty");
        }

        UserEntity getEmployee = this.userRepository.findById(employeeId)
                .orElseThrow(() -> new NotFoundException("Employee not found"));

        UserEntity getSuperior = this.userRepository.findById(superiorId)
                .orElseThrow(() -> new NotFoundException("Manager not found"));

        getSuperior.getUserRoles()
                .stream()
                .filter(role -> {
                            String roleValue = role.getRole().getRoleName().getRoleValue();
                            return this.rolesWithPermissionToBeSuperior.contains(roleValue);
                        }
                ).findFirst().orElseThrow(() -> new UnauthorizedException("Insufficient permissions"));

        Optional<UserManager> doesManagerIsAlreadyAttached = this.userManagerRepository.findByUserAndManager(
                getEmployee.getId(),
                getSuperior.getId()
        );

        if (doesManagerIsAlreadyAttached.isPresent()) {
            throw new ConflictException("Superior is already attached to this employee");
        }

        UserManager userSuperiorAttach = new UserManager();
        userSuperiorAttach.setManager(getSuperior);
        userSuperiorAttach.setUser(getEmployee);

        UserManager performAttachment = this.userManagerRepository.save(userSuperiorAttach);

        return performAttachment;
    }

    public UserEntity switchEmployeeStatus(UUID employeeId, boolean willDisable) {
        if (employeeId == null) {
            throw new BadRequestException("superiorId can't be empty");
        }

        UserEntity getEmployee = this.userRepository.findById(employeeId)
                .orElseThrow(() -> new NotFoundException("Employee not found"));

        if (willDisable && getEmployee.getDisabled()) {
            throw new ConflictException("Employee is already disabled");
        } else if (!willDisable && !getEmployee.getDisabled()) {
            throw new ConflictException("Employee is already active");
        }

        return this.handleDisableEmployee(getEmployee, willDisable);
    }

    private UserEntity handleDisableEmployee(UserEntity getEmployee, boolean willDisable) {
        getEmployee.setDisabled(willDisable);
        getEmployee.setDisabledAt(willDisable ? new Date() : null);

        UserEntity performDisable = this.userRepository.save(getEmployee);

        return performDisable;
    }

    public UserEntity updateEmployeeProfile(UUID requesterId, UserEntity employeeUpdated) {
        if (requesterId == null) {
            throw new BadRequestException("Requester id can't be empty");
        }

        if (employeeUpdated == null) {
            throw new BadRequestException("Employee can't be empty");
        } else if (employeeUpdated.getId() == null) {
            throw new BadRequestException("Employee id can't be empty");
        }

        UserEntity getEmployee = this.userRepository.findById(employeeUpdated.getId())
                .orElseThrow(() -> new NotFoundException("Employee not found"));

        if (!requesterId.equals(getEmployee.getId())) {
            Optional<UserManager> doesRequesterIsEmployeeManager = this.userManagerRepository.findByUserAndManager(
                    getEmployee.getId(),
                    requesterId
            );

            if (!doesRequesterIsEmployeeManager.isPresent()) {
                this.checkPermissionsToUpdateEmployee(requesterId);
            }
        }

        if (employeeUpdated.getPassword() != null) {
            if (employeeUpdated.getPassword().length() < 6) {
                throw new BadRequestException("Password must have at least 6 characters");
            }

            employeeUpdated.setPassword(this.passwordEncoder.encrypt(employeeUpdated.getPassword()));
        }

        if (employeeUpdated.getEmail() != null) {
            if (!this.mailValidator.validate(employeeUpdated.getEmail())) {
                throw new BadRequestException("Invalid e-mail");
            }

            Optional<UserEntity> isEmailAlreadyBeingUsed = this.userRepository.findByEmail(employeeUpdated.getEmail());

            if (isEmailAlreadyBeingUsed.isPresent()) {
                throw new ConflictException("Updated E-mail is already being used");
            }
        }

        this.cloneProperties.cloneNonNullProps(employeeUpdated, getEmployee);

        return this.userRepository.save(getEmployee);
    }

    protected void checkPermissionsToUpdateEmployee(UUID updateRequesterId) {
        UserEntity getRequester = this.userRepository.findById(updateRequesterId)
                .orElseThrow(() -> new NotFoundException("Requester not found"));

        getRequester.getUserRoles().stream().filter(role -> {
            String getRole = role.getRole().getRoleName().getRoleValue();

            return getRole.equals(RoleEntity.Role.ADMIN.getRoleValue()) ||
                    getRole.equals(RoleEntity.Role.HUMAN_RESOURCES.getRoleValue());
        }).findAny().orElseThrow(() -> new ForbiddenException(
                "Only the direct Manager, Human Resources or Admins can perform actions on employee profile.")
        );
    }

    @Transactional
    private void registerUser(UserEntity user, RoleEntity.Role role, String position) {
        if (user == null) {
            throw new BadRequestException("user can't be empty");
        }

        if (user.getPassword().length() < 6) {
            throw new BadRequestException("Password must have at least 6 characters");
        }

        Optional<UserEntity> doesHrAlreadyExists = this.userRepository.findByEmail(user.getEmail());

        if (doesHrAlreadyExists.isPresent()) {
            throw new ConflictException("E-mail already being used.");
        }

        user.setPassword(this.passwordEncoder.encrypt(user.getPassword()));
        user.setPosition(position);
        user.setDisabled(false);

        RoleEntity getHrRole = this.roleRepository.findByRole(role)
                .orElseThrow(() -> new NotFoundException("Provided role not found"));

        UserEntity saveUser = this.userRepository.save(user);

        UserRole newUserRole = new UserRole();
        newUserRole.setRole(getHrRole);
        newUserRole.setUser(saveUser);

        this.userRoleRepository.save(newUserRole);
    }
}
