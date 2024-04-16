package com.root.authservice.clients;

import com.root.authservice.dto.in.auth.RegisterUserDto;
import com.root.authservice.dto.in.auth.SuperiorAttachRequestDTO;
import com.root.authservice.dto.out.auth.ProfileResponseDTO;
import com.root.crossdbservice.entities.RoleEntity;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@FeignClient(
        name = "employee-rest-calls",
        url = "http://localhost:9001/employee-service/api/v1"
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

    @GetMapping("/profile")
    ProfileResponseDTO requestProfile(@RequestParam("userId") UUID userId);

    @GetMapping("/list/all")
    List<ProfileResponseDTO> requestListAllByRoleNotPageable(@RequestParam("role") RoleEntity.Role role);
}
