package com.root.employeeservice.controllers;

import com.google.common.collect.Sets;
import com.root.crossdbservice.entities.RoleEntity;
import com.root.crossdbservice.entities.UserEntity;
import com.root.crossdbservice.entities.UserManager;
import com.root.employeeservice.dtos.in.RegisterUserDto;
import com.root.employeeservice.dtos.in.SuperiorAttachRequestDTO;
import com.root.employeeservice.dtos.out.ProfileResponseDTO;
import com.root.employeeservice.services.EmployeeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;
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

    @PostMapping("/register/manager")
    public void registerManager(@RequestBody @Valid RegisterUserDto dto) {
        this.employeeService.registerManager(dto.toEntity());
    }

    @GetMapping("/list/all")
    public List<ProfileResponseDTO> listAllUsersByRoleNotPageable(@RequestParam("role") RoleEntity.Role role) {
        List<UserEntity> usersFiltered = this.employeeService.listEmployeesByRoleUnpaginable(
                role
        );

        List<ProfileResponseDTO> profiles = usersFiltered.stream().map(user ->
                new ProfileResponseDTO(
                        user.getId(),
                        user.getName(),
                        user.getEmail(),
                        Sets.newHashSet(user.getUserRoles().stream()
                                .map(userRole -> userRole.getRole().getRoleName().getRoleValue())
                                .collect(Collectors.toList())
                        ),
                        user.getCreatedAt())
        ).collect(Collectors.toList());

        return profiles;
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

    @PostMapping("/attach-superior")
    public Map<String, String> attachNewSuperiorToEmployee(@RequestBody @Valid SuperiorAttachRequestDTO dto) {
        UserManager managerAttached = this.employeeService.attachSuperiorToEmployee(
                UUID.fromString(dto.getEmployeeId()), UUID.fromString(dto.getSuperiorId())
        );

        Map<String, String> response = new LinkedHashMap<String, String>() {{
            put("employee", managerAttached.getUser().getName());
            put("manager_attached", managerAttached.getManager().getName());
        }};

        return response;
    }

    @DeleteMapping("/disable/{employeeId}")
    public Map<String, String> disableActiveEmployee(
            @PathVariable(name = "employeeId") UUID employeeId
    ) {
        UserEntity employeeDisabled = this.employeeService.disableEmployee(employeeId);

        Map<String, String> responseBody = new LinkedHashMap<String, String>() {{
            put("message", "Employee disabled successfully.");
            put("employeeId", employeeDisabled.getId().toString());
        }};

        return responseBody;
    }
}
