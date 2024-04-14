package com.root.authservice.services;

import com.root.authservice.exceptions.BadRequestException;
import com.root.authservice.exceptions.ConflictException;
import com.root.authservice.exceptions.ForbiddenException;
import com.root.authservice.exceptions.NotFoundException;
import com.root.crossdbservice.entities.RoleEntity;
import com.root.crossdbservice.entities.UserEntity;
import com.root.crossdbservice.entities.UserRole;
import com.root.crossdbservice.repositories.RoleRepository;
import com.root.crossdbservice.repositories.UserRepository;
import com.root.crossdbservice.repositories.UserRoleRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserRoleRepository userRoleRepository;
    private final RoleRepository roleRepository;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, UserRoleRepository userRoleRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userRoleRepository = userRoleRepository;
        this.roleRepository = roleRepository;
    }

    @Transactional
    public void registerBasic(UserEntity newUser) {
        if (newUser == null) {
            throw new BadRequestException("newUser can't be null");
        }

        if (newUser.getPassword().length() < 6) {
            throw new BadRequestException("password must have at least 6 characters");
        }

        Optional<UserEntity> doesUserAlreadyExists = this.userRepository.findByEmail(newUser.getEmail());

        if (doesUserAlreadyExists.isPresent()) {
            throw new ConflictException("E-mail already registered.");
        }

        newUser.setPassword(this.passwordEncoder.encode(newUser.getPassword()));

        UserEntity user = this.userRepository.save(newUser);

        RoleEntity defaultRole = this.roleRepository.findByRole(RoleEntity.Role.USER)
                .orElseThrow(() -> new NotFoundException("Role User not found."));

        UserRole userRole = new UserRole();
        userRole.setRole(defaultRole);
        userRole.setUser(user);

        this.userRoleRepository.save(userRole);
    }

    public UserEntity authorizeRequester(UserEntity user) {
        if (user == null) {
            throw new BadRequestException("User can't be empty");
        }

        if (user.getEmail() == null) {
            throw new BadRequestException("User e-mail can't be empty");
        }

        UserEntity findUser = this.userRepository.findByEmail(user.getEmail())
                .orElseThrow(() -> new NotFoundException("User not found"));

        boolean doesPasswordsMatches =
                this.passwordEncoder.matches(user.getPassword(), findUser.getPassword());

        if (!doesPasswordsMatches) {
            throw new ForbiddenException("Wrong credentials.");
        }

        return findUser;
    }

    public UserEntity requesterProfile(UUID userId) {
        if (userId == null) {
            throw new BadRequestException("userId can't be empty");
        }

        UserEntity findUser = this.userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        return findUser;
    }

    public void registerHumanResource(UserEntity newHr) {
        if (newHr == null) {
            throw new BadRequestException("newHr can't be empty");
        }

        if (newHr.getPassword().length() < 6) {
            throw new BadRequestException("Password must have at least 6 characters");
        }

        Optional<UserEntity> doesHrAlreadyExists = this.userRepository.findByEmail(newHr.getEmail());

        if (doesHrAlreadyExists.isPresent()) {
            throw new ConflictException("E-mail already being used.");
        }

        newHr.setPassword(this.passwordEncoder.encode(newHr.getPassword()));
        newHr.setPosition("Human Resources");

        RoleEntity getHrRole = this.roleRepository.findByRole(RoleEntity.Role.HUMAN_RESOURCES)
                .orElseThrow(() -> new NotFoundException("Human Resource role not found"));

        UserEntity saveUser = this.userRepository.save(newHr);

        UserRole newUserRole = new UserRole();
        newUserRole.setRole(getHrRole);
        newUserRole.setUser(saveUser);

        this.userRoleRepository.save(newUserRole);
    }
}
