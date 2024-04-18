package com.root.authservice.controllers.employeeServiceControllers;

import com.root.authservice.clients.employee_service_clients.EmployeeClientRest;
import com.root.authservice.clients.employee_service_clients.TimeRecordClientRest;
import com.root.authservice.dto.in.auth.NewPendingUpdateTimeRecord;
import com.root.authservice.dto.out.auth.PendingTimeRecordResponseDTO;
import com.root.authservice.dto.out.auth.TimeRecordResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
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

    @PostMapping("/request-update")
    public ResponseEntity<PendingTimeRecordResponseDTO> requestTimeRecordUpdate(
            Authentication authentication,
            @RequestBody @Valid NewPendingUpdateTimeRecord dto
    ) {
        UUID employeeId = UUID.fromString(authentication.getName());

        PendingTimeRecordResponseDTO responseClient = this.timeRecordClientRest.requestUpdate(
                employeeId,
                dto
        );

        return ResponseEntity.status(201).body(responseClient);
    }
}
