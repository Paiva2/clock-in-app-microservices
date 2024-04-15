package com.root.authservice.clients;

import com.root.authservice.dto.in.auth.RegisterUserDto;
import com.root.authservice.dto.out.auth.ProfileResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@FeignClient(
        name = "employee-rest-calls",
        url = "http://localhost:9001/employee-service/api/v1"
)
@Component
public interface EmployeeClientRest {
    @PostMapping(value = "/register/hr")
    void requestRegisterHr(RegisterUserDto requestBody);

    @PostMapping(value = "/register/employee")
    void requestRegisterEmployee(RegisterUserDto requestBody);

    @GetMapping("/profile")
    ProfileResponseDTO requestProfile(@RequestParam("userId") UUID userId);
}
