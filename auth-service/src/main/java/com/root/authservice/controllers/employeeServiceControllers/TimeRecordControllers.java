package com.root.authservice.controllers.employeeServiceControllers;

import com.root.authservice.clients.employee_service_clients.EmployeeClientRest;
import com.root.authservice.clients.employee_service_clients.TimeRecordClientRest;
import com.root.authservice.dto.out.auth.TimeRecordResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/employee")
public class TimeRecordControllers {
    private final TimeRecordClientRest timeRecordClientRest;

    public TimeRecordControllers(TimeRecordClientRest timeRecordClientRest) {
        this.timeRecordClientRest = timeRecordClientRest;
    }

    @PostMapping("/time-record")
    public ResponseEntity<TimeRecordResponseDTO> registerTimeRecord(Authentication authentication) {
        UUID employeeId = UUID.fromString(authentication.getName());
        TimeRecordResponseDTO responseClient = this.timeRecordClientRest.registerTimeRecord(employeeId);

        return ResponseEntity.status(201).body(responseClient);
    }
}
