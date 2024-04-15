package com.root.employeeservice.controllers;

import com.google.common.collect.Sets;
import com.root.crossdbservice.entities.UserEntity;
import com.root.employeeservice.dtos.in.RegisterUserDto;
import com.root.employeeservice.dtos.out.ProfileResponseDTO;
import com.root.employeeservice.services.EmployeeService;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1")
public class EmployeeController {
    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @PostMapping("/register/hr")
    public void registerHr(@RequestBody @Valid RegisterUserDto dto) {
        this.employeeService.registerHumanResource(dto.toEntity());
    }

    @PostMapping("/register/employee")
    public void register(@RequestBody @Valid RegisterUserDto dto) {
        this.employeeService.registerBasic(dto.toEntity());
    }

    @GetMapping("/profile")
    public ProfileResponseDTO profile(@RequestParam(value = "userId") UUID userId) {
        UserEntity user = this.employeeService.requesterProfile(userId);

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

        return profileResponseDTO;
    }
}
