package com.root.authservice.controllers;

import com.google.common.collect.Sets;
import com.root.authservice.dto.in.auth.RegisterUserDto;
import com.root.authservice.dto.in.auth.LoginRequesterDTO;
import com.root.authservice.dto.out.auth.AuthorizationInfosDTO;
import com.root.authservice.dto.out.auth.ProfileResponseDTO;
import com.root.authservice.services.AuthService;
import com.root.authservice.utils.JwtHandler;
import com.root.crossdbservice.entities.UserEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthService authService;
    private final JwtHandler jwtHandler;

    public AuthController(AuthService authService, JwtHandler jwtHandler) {
        this.authService = authService;
        this.jwtHandler = jwtHandler;
    }

    @PostMapping("/register/hr")
    public ResponseEntity<Void> registerHr(@RequestBody @Valid RegisterUserDto dto) {
        this.authService.registerHumanResource(dto.toEntity());

        return ResponseEntity.status(201).build();
    }

    @PostMapping("/register/employee")
    public ResponseEntity<Void> register(@RequestBody @Valid RegisterUserDto dto) {
        this.authService.registerBasic(dto.toEntity());

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

    @GetMapping("/profile")
    public ResponseEntity<ProfileResponseDTO> profile(Authentication auth) {
        UUID requesterId = UUID.fromString(auth.getName());
        UserEntity user = this.authService.requesterProfile(requesterId);

        List<String> userRoles = user.getUserRoles()
                .stream().map(userRole -> userRole.getRole().getRoleName().getRoleValue())
                .collect(Collectors.toList());

        ProfileResponseDTO profileResponseDTO = new ProfileResponseDTO(
                user.getId(),
                user.getName(),
                user.getEmail(),
                Sets.newHashSet(userRoles),
                user.getCreatedAt()
        );

        return ResponseEntity.status(200).body(profileResponseDTO);
    }
}
