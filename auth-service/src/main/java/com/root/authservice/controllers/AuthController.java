package com.root.authservice.controllers;

import com.root.authservice.dto.RegisterUserDto;
import com.root.authservice.services.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @RequestMapping("/register")
    @PostMapping
    public ResponseEntity<Void> register(@RequestBody @Valid RegisterUserDto dto) {
        this.authService.registerRequester(dto.toEntity());

        return ResponseEntity.status(201).build();
    }

    @GetMapping("/testing")
    public String test(){
        return "test";
    }
}
