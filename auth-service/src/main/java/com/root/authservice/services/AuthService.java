package com.root.authservice.services;

import com.root.authservice.exceptions.BadRequestException;
import com.root.authservice.exceptions.ForbiddenException;
import com.root.authservice.exceptions.NotFoundException;
import com.root.crossdbservice.entities.UserEntity;
import com.root.crossdbservice.repositories.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;

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

        if (findUser.getDisabled()) {
            throw new ForbiddenException("User is disabled");
        }

        boolean doesPasswordsMatches =
                this.passwordEncoder.matches(user.getPassword(), findUser.getPassword());

        if (!doesPasswordsMatches) {
            throw new ForbiddenException("Wrong credentials.");
        }

        return findUser;
    }
}
