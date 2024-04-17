package com.root.authservice.clients.employee_service_clients;

import com.root.authservice.dto.in.auth.RegisterUserDto;
import com.root.authservice.dto.in.auth.SuperiorAttachRequestDTO;
import com.root.authservice.dto.in.auth.UpdateEmployeeProfileDTO;
import com.root.authservice.dto.out.auth.EmployeeFullListResponseDTO;
import com.root.authservice.dto.out.auth.ProfileResponseDTO;
import com.root.authservice.dto.out.auth.ProfileUpdateResponseDTO;
import com.root.authservice.enums.OrderBy;
import com.root.crossdbservice.entities.RoleEntity;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@FeignClient(
        name = "employee-rest-calls",
        url = "http://localhost:9003/api/v1"
)
@Component
public interface EmployeeClientRest {
    @PostMapping(value = "/register/hr")
    void requestRegisterHr(@RequestBody RegisterUserDto requestBody);

    @PostMapping(value = "/register/employee")
    void requestRegisterEmployee(@RequestBody RegisterUserDto requestBody);

    @PostMapping(value = "/register/manager")
    void requestRegisterManager(@RequestBody RegisterUserDto requestBody);

    @PostMapping(value = "/attach-superior")
    Map<String, String> attachSuperiorToEmployee(@RequestBody SuperiorAttachRequestDTO requestBody);

    @DeleteMapping(value = "/disable/{employeeId}")
    Map<String, String> disableEmployee(
            @PathVariable("employeeId") UUID employeeId,
            @RequestParam("requesterId") UUID requesterId
    );

    @PutMapping(value = "/active/{employeeId}")
    Map<String, String> reActiveEmployee(
            @PathVariable("employeeId") UUID employeeId,
            @RequestParam("requesterId") UUID requesterId
    );

    @PutMapping("/update/{employeeId}")
    ProfileUpdateResponseDTO updateEmployeeProfile(
            @PathVariable("employeeId") UUID employeeId,
            @RequestBody UpdateEmployeeProfileDTO requestBody
    );

    @GetMapping("/profile")
    ProfileResponseDTO requestProfile(@RequestParam("userId") UUID userId);

    @GetMapping("/list")
    List<ProfileResponseDTO> requestListAllByRoleNotPageable(@RequestParam("role") RoleEntity.Role role);

    @GetMapping("/list-all")
    EmployeeFullListResponseDTO listAll(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "perPage", defaultValue = "5") int perPage,
            @RequestParam(value = "order", defaultValue = "desc") OrderBy orderBy,
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "position", required = false) String position,
            @RequestParam(value = "disabled", required = false, defaultValue = "false") boolean disabled
    );
}
