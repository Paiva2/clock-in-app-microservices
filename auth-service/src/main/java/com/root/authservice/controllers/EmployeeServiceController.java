package com.root.authservice.controllers;

import com.root.authservice.clients.EmployeeClientRest;
import com.root.authservice.dto.in.auth.RegisterUserDto;
import com.root.authservice.dto.in.auth.SuperiorAttachRequestDTO;
import com.root.authservice.dto.out.auth.ProfileResponseDTO;
import com.root.crossdbservice.entities.RoleEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;
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

    @PostMapping("/register/manager")
    public ResponseEntity<Void> registerManager(@RequestBody @Valid RegisterUserDto dto) {
        this.employeeClientRest.requestRegisterManager(dto);

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

    @GetMapping("/list")
    public ResponseEntity<List<ProfileResponseDTO>> listAllByRoleUnpageable(@RequestParam("role") RoleEntity.Role role) {
        List<ProfileResponseDTO> filtered = this.employeeClientRest.requestListAllByRoleNotPageable(
                role
        );

        return ResponseEntity.status(200).body(filtered);
    }

    @PostMapping("/attach-superior")
    public ResponseEntity<Map<String, String>> attachSuperior(@RequestBody @Valid SuperiorAttachRequestDTO dto) {
        Map<String, String> clientResponse = this.employeeClientRest.attachSuperiorToEmployee(dto);

        return ResponseEntity.status(201).body(clientResponse);
    }
}
