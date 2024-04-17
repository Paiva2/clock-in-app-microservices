package com.root.employeeservice.controllers;

import com.google.common.collect.Sets;
import com.root.crossdbservice.entities.RoleEntity;
import com.root.crossdbservice.entities.UserEntity;
import com.root.crossdbservice.entities.UserManager;
import com.root.crossdbservice.entities.UserRole;
import com.root.employeeservice.dtos.in.RegisterUserDto;
import com.root.employeeservice.dtos.in.SuperiorAttachRequestDTO;
import com.root.employeeservice.dtos.in.UpdateEmployeeProfileDTO;
import com.root.employeeservice.dtos.out.*;
import com.root.employeeservice.enums.OrderBy;
import com.root.employeeservice.services.EmployeeService;
import org.springframework.data.domain.Page;
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

    @GetMapping("/list")
    public List<ProfileResponseDTO> listAllUsersByRoleNotPageable(@RequestParam("role") RoleEntity.Role role) {
        List<UserEntity> usersFiltered = this.employeeService.listEmployeesByRoleUnpaginable(
                role
        );

        List<ProfileResponseDTO> profiles = usersFiltered.stream().map(user ->
                new ProfileResponseDTO(
                        user.getId(),
                        user.getName(),
                        user.getEmail(),
                        user.getPosition(),
                        Sets.newHashSet(user.getUserRoles().stream()
                                .map(userRole -> userRole.getRole().getRoleName().getRoleValue())
                                .collect(Collectors.toList())
                        ),
                        user.getCreatedAt())
        ).collect(Collectors.toList());

        return profiles;
    }

    @GetMapping("/list-all")
    public EmployeeFullListResponseDTO listAllUsers(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "perPage", defaultValue = "5") int perPage,
            @RequestParam(value = "order", defaultValue = "desc") OrderBy orderBy,
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "position", required = false) String position,
            @RequestParam(value = "disabled", required = false, defaultValue = "false") boolean disabled
    ) {
        Page<UserEntity> listOfUsers = this.employeeService.listAllEmployees(
                page,
                perPage,
                orderBy,
                name,
                email,
                position,
                disabled
        );

        List<EmployeeResponseDTO> employeeResponseDtos = listOfUsers.stream().map(user ->
                new EmployeeResponseDTO(
                        user.getId(),
                        user.getName(),
                        user.getEmail(),
                        user.getPosition(),
                        this.userRolesResponseDto(user.getUserRoles()),
                        user.getUserManager().stream().map(userManager ->
                                this.managerResponseDto(userManager.getManager())
                        ).collect(Collectors.toList()),
                        user.getDisabled(),
                        user.getDisabledAt()
                )
        ).collect(Collectors.toList());

        EmployeeFullListResponseDTO employeeFullListResponseDto = new EmployeeFullListResponseDTO(
                listOfUsers.getNumber() + 1,
                listOfUsers.getSize(),
                listOfUsers.getTotalElements(),
                employeeResponseDtos
        );

        return employeeFullListResponseDto;
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
                user.getPosition(),
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
            @PathVariable(name = "employeeId") UUID employeeId,
            @RequestParam("requesterId") UUID requesterId
    ) {
        UserEntity employeeDisabled = this.employeeService.switchEmployeeStatus(requesterId, employeeId, true);

        Map<String, String> responseBody = new LinkedHashMap<String, String>() {{
            put("message", "Employee disabled successfully.");
            put("employeeId", employeeDisabled.getId().toString());
        }};

        return responseBody;
    }

    @PutMapping("/active/{employeeId}")
    public Map<String, String> reActiveEmployee(
            @PathVariable(name = "employeeId") UUID employeeId,
            @RequestParam("requesterId") UUID requesterId
    ) {
        UserEntity employeeActivated = this.employeeService.switchEmployeeStatus(requesterId, employeeId, false);

        Map<String, String> responseBody = new LinkedHashMap<String, String>() {{
            put("message", "Employee activated successfully.");
            put("employeeId", employeeActivated.getId().toString());
        }};

        return responseBody;
    }

    @PutMapping("/update/{employeeId}")
    public ProfileUpdateResponseDTO updateEmployeeProfile(
            @PathVariable("employeeId") UUID employeeId,
            @RequestBody @Valid UpdateEmployeeProfileDTO dto
    ) {
        UserEntity employeeUpdated =
                this.employeeService.updateEmployeeProfile(
                        UUID.fromString(dto.getRequesterId()), dto.toEntity(employeeId)
                );

        ProfileUpdateResponseDTO profileUpdateResponseDto = new ProfileUpdateResponseDTO(
                employeeUpdated.getId(),
                employeeUpdated.getName(),
                employeeUpdated.getEmail(),
                employeeUpdated.getPosition(),
                employeeUpdated.getUpdatedAt()
        );

        return profileUpdateResponseDto;
    }

    private ManagerResponseDTO managerResponseDto(UserEntity manager) {
        return new ManagerResponseDTO(
                manager.getId(),
                manager.getName(),
                manager.getEmail(),
                manager.getPosition()
        );
    }

    private Set<String> userRolesResponseDto(Set<UserRole> userRoles) {
        return userRoles.stream().map(role ->
                role.getRole().getRoleName().getRoleValue()
        ).collect(Collectors.toSet());
    }
}
