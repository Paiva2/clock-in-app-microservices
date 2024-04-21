package com.root.authservice.controllers.employeeServiceControllers;

import com.root.authservice.clients.employee_service_clients.TimeRecordClientRest;
import com.root.authservice.dto.in.auth.NewPendingUpdateTimeRecord;
import com.root.authservice.dto.out.auth.PendingTimeRecordResponseDTO;
import com.root.authservice.dto.out.auth.TimeRecordResponseDTO;
import com.root.authservice.exceptions.BadRequestException;
import com.root.crossdbservice.entities.PendingTimeRecordAction;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.Set;
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

    @PostMapping("/request-action")
    public ResponseEntity<PendingTimeRecordResponseDTO> requestTimeRecordUpdate(
            Authentication authentication,
            @RequestParam(name = "action", required = true) PendingTimeRecordAction.ActionType actionType,
            @RequestBody @Valid NewPendingUpdateTimeRecord dto
    ) {
        UUID employeeId = UUID.fromString(authentication.getName());

        if (actionType.equals(PendingTimeRecordAction.ActionType.UPDATE) && dto.getRecordHour() == null) {
            throw new BadRequestException("recordHour must be provided to request update");
        }

        PendingTimeRecordResponseDTO responseClient = this.timeRecordClientRest.requestUpdate(
                actionType,
                employeeId,
                dto
        );

        return ResponseEntity.status(201).body(responseClient);
    }

    @GetMapping("/time-record/list")
    public ResponseEntity<Set<TimeRecordResponseDTO>> listAllEmployeeRecords(
            Authentication authentication,
            @RequestParam(name = "day", required = false, defaultValue = "0")
            @Min(value = 1, message = "day must be more than 0") int day,
            @RequestParam(name = "month", required = true) int month,
            @RequestParam(name = "year", required = true) int year
    ) {
        UUID employeeId = UUID.fromString(authentication.getName());

        Set<TimeRecordResponseDTO> clientResponse = this.timeRecordClientRest.listAllEmployeeRecords(
                employeeId,
                month,
                year,
                day
        );

        return ResponseEntity.status(200).body(clientResponse);
    }
}
