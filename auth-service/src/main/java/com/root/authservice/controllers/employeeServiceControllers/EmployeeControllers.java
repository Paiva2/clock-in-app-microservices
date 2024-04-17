package com.root.authservice.controllers.employeeServiceControllers;

import com.root.authservice.clients.employee_service_clients.EmployeeClientRest;
import com.root.authservice.dto.in.auth.RegisterUserDto;
import com.root.authservice.dto.in.auth.SuperiorAttachRequestDTO;
import com.root.authservice.dto.in.auth.UpdateEmployeeProfileDTO;
import com.root.authservice.dto.out.auth.EmployeeFullListResponseDTO;
import com.root.authservice.dto.out.auth.ProfileResponseDTO;
import com.root.authservice.dto.out.auth.ProfileUpdateResponseDTO;
import com.root.authservice.enums.OrderBy;
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
public class EmployeeControllers {
    private final EmployeeClientRest employeeClientRest;

    public EmployeeControllers(EmployeeClientRest employeeClientRest) {
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

    @GetMapping("/list-all")
    public ResponseEntity<EmployeeFullListResponseDTO> listAllEmployees(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "perPage", defaultValue = "5") int perPage,
            @RequestParam(value = "order", defaultValue = "desc") String orderBy,
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "position", required = false) String position,
            @RequestParam(value = "disabled", required = false, defaultValue = "false") boolean disabled
    ) {
        EmployeeFullListResponseDTO clientResponse = this.employeeClientRest.listAll(
                page,
                perPage,
                OrderBy.valueOf(orderBy.toUpperCase()),
                email,
                name,
                position,
                disabled
        );

        return ResponseEntity.status(200).body(clientResponse);
    }

    @PostMapping("/attach-superior")
    public ResponseEntity<Map<String, String>> attachSuperior(@RequestBody @Valid SuperiorAttachRequestDTO dto) {
        Map<String, String> clientResponse = this.employeeClientRest.attachSuperiorToEmployee(dto);

        return ResponseEntity.status(201).body(clientResponse);
    }

    @DeleteMapping("/disable/{employeeId}")
    public ResponseEntity<Map<String, String>> disableAnEmployee(
            @PathVariable("employeeId") UUID employeeId,
            Authentication authentication
    ) {
        UUID requesterId = UUID.fromString(authentication.getName());
        Map<String, String> clientResponse = this.employeeClientRest.disableEmployee(employeeId, requesterId);

        return ResponseEntity.status(201).body(clientResponse);
    }

    @PutMapping("/active/{employeeId}")
    public ResponseEntity<Map<String, String>> reActiveAnEmployee(
            @PathVariable("employeeId") UUID employeeId,
            Authentication authentication
    ) {
        UUID requesterId = UUID.fromString(authentication.getName());
        Map<String, String> clientResponse = this.employeeClientRest.reActiveEmployee(employeeId, requesterId);

        return ResponseEntity.status(201).body(clientResponse);
    }

    @PatchMapping("/update/{employeeId}")
    public ResponseEntity<ProfileUpdateResponseDTO> updateEmployee(
            @PathVariable("employeeId") UUID employeeId,
            @RequestBody @Valid UpdateEmployeeProfileDTO dto,
            Authentication authentication
    ) {
        dto.setRequesterId(UUID.fromString(authentication.getName()));

        ProfileUpdateResponseDTO clientResponse = this.employeeClientRest.updateEmployeeProfile(
                employeeId,
                dto
        );

        return ResponseEntity.status(201).body(clientResponse);
    }
}
