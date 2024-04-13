package com.root.authservice.services;

import com.root.authservice.exceptions.BadRequestException;
import com.root.authservice.exceptions.ConflictException;
import com.root.authservice.repositories.UserRepository;
import com.root.crossdbservice.UserEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

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

        this.userRepository.save(newUser);
    }
}
