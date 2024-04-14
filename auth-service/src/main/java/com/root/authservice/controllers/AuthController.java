package com.root.authservice.controllers;

import com.root.authservice.dto.RegisterUserDto;
import com.root.authservice.dto.in.auth.LoginRequesterDTO;
import com.root.authservice.dto.out.auth.AuthorizationInfosDTO;
import com.root.authservice.services.AuthService;
import com.root.authservice.utils.JwtHandler;
import com.root.crossdbservice.entities.UserEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthService authService;
    private final JwtHandler jwtHandler;

    public AuthController(AuthService authService, JwtHandler jwtHandler) {
        this.authService = authService;
        this.jwtHandler = jwtHandler;
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody @Valid RegisterUserDto dto) {
        this.authService.registerRequester(dto.toEntity());

        return ResponseEntity.status(201).build();
    }

    @PostMapping("/login")
    public ResponseEntity<AuthorizationInfosDTO> auth(@RequestBody @Valid LoginRequesterDTO dto) {
        UserEntity authUser = this.authService.authorizeRequester(dto.toEntity());

        String generatedJwt = this.jwtHandler.createToken(authUser.getId().toString());

        AuthorizationInfosDTO authorizationInfosDTO = new AuthorizationInfosDTO(
                "Authenticated successfully!",
                generatedJwt
        );

        return ResponseEntity.status(200).body(authorizationInfosDTO);
    }
}
