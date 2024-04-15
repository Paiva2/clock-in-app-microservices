package com.root.authservice.controllers;

import com.root.authservice.clients.EmployeeClientRest;
import com.root.authservice.dto.in.auth.RegisterUserDto;
import com.root.authservice.dto.out.auth.ProfileResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/employee")
public class EmployeeServiceController {
    private final EmployeeClientRest employeeClientRest;

    public EmployeeServiceController(EmployeeClientRest employeeClientRest) {
        this.employeeClientRest = employeeClientRest;
    }

    @PostMapping("/register/hr")
    public ResponseEntity<Void> registerHr(@RequestBody @Valid RegisterUserDto dto) {
        this.employeeClientRest.requestRegisterHr(dto);

        return ResponseEntity.status(201).build();
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody @Valid RegisterUserDto dto) {
        this.employeeClientRest.requestRegisterEmployee(dto);

        return ResponseEntity.status(201).build();
    }

    @GetMapping("/profile")
    public ResponseEntity<ProfileResponseDTO> profile(Authentication auth) {
        UUID requesterId = UUID.fromString(auth.getName());

        ProfileResponseDTO profileResponseDTO = this.employeeClientRest.requestProfile(requesterId);

        return ResponseEntity.status(200).body(profileResponseDTO);
    }
}
