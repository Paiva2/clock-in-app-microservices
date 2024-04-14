package com.root.authservice.services;

import com.root.authservice.exceptions.BadRequestException;
import com.root.authservice.exceptions.ConflictException;
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
    public void registerRequester(UserEntity newUser) {
        if(newUser == null){
            throw new BadRequestException("newUser can't be null");
        }

        if(newUser.getPassword().length() < 6){
            throw new BadRequestException("password must have at least 6 characters");
        }

        Optional<UserEntity> doesUserAlreadyExists = this.userRepository.findByEmail(newUser.getEmail());

        if(doesUserAlreadyExists.isPresent()){
            throw new ConflictException("E-mail already registered.");
        }

        newUser.setPassword(this.passwordEncoder.encode(newUser.getPassword()));

        UserEntity user =  this.userRepository.save(newUser);

        RoleEntity defaultRole = this.roleRepository.findByRole(RoleEntity.Role.USER)
                .orElseThrow(() -> new NotFoundException("Role User not found."));

        UserRole userRole = new UserRole();
        userRole.setRole(defaultRole);
        userRole.setUser(user);

        this.userRoleRepository.save(userRole);
    }
}
