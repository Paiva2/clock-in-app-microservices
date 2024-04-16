package com.root.employeeservice.services;

import com.google.common.collect.Sets;
import com.root.crossdbservice.entities.RoleEntity;
import com.root.crossdbservice.entities.UserEntity;
import com.root.crossdbservice.entities.UserManager;
import com.root.crossdbservice.entities.UserRole;
import com.root.crossdbservice.repositories.RoleRepository;
import com.root.crossdbservice.repositories.UserManagerRepository;
import com.root.crossdbservice.repositories.UserRepository;
import com.root.crossdbservice.repositories.UserRoleRepository;
import com.root.employeeservice.exceptions.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import javax.transaction.Transactional;
import java.util.*;

@Service
public class EmployeeService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final UserRoleRepository userRoleRepository;
    private final RoleRepository roleRepository;
    private final UserManagerRepository userManagerRepository;

    protected final List<String> rolesWithPermissionToBeSuperior = Arrays.asList(
            RoleEntity.Role.ADMIN.getRoleValue(),
            RoleEntity.Role.MANAGER.getRoleValue(),
            RoleEntity.Role.HUMAN_RESOURCES.getRoleValue()
    );

    public EmployeeService(UserRepository userRepository, UserRoleRepository userRoleRepository, RoleRepository roleRepository, UserManagerRepository userManagerRepository) {
        this.userRepository = userRepository;
        this.userManagerRepository = userManagerRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
        this.userRoleRepository = userRoleRepository;
        this.roleRepository = roleRepository;
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

        user.setPassword(this.passwordEncoder.encode(user.getPassword()));
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
