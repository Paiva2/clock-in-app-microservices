package com.root.employeeservice.services;

import com.root.crossdbservice.entities.RoleEntity;
import com.root.crossdbservice.entities.UserEntity;
import com.root.crossdbservice.entities.UserRole;
import com.root.crossdbservice.repositories.RoleRepository;
import com.root.crossdbservice.repositories.UserRepository;
import com.root.crossdbservice.repositories.UserRoleRepository;
import com.root.employeeservice.exceptions.BadRequestException;
import com.root.employeeservice.exceptions.ConflictException;
import com.root.employeeservice.exceptions.ForbiddenException;
import com.root.employeeservice.exceptions.NotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;

@Service
public class EmployeeService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final UserRoleRepository userRoleRepository;
    private final RoleRepository roleRepository;

    public EmployeeService(UserRepository userRepository, UserRoleRepository userRoleRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
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
