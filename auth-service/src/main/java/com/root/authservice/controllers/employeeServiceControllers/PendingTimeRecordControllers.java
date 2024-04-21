package com.root.authservice.controllers.employeeServiceControllers;

import com.root.authservice.clients.employee_service_clients.PendingTimeRecordClient;
import com.root.authservice.dto.out.auth.PendingTimeRecordListResponseDTO;
import com.root.authservice.enums.OrderBy;
import com.root.crossdbservice.entities.PendingTimeRecordAction;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/employee")
public class PendingTimeRecordControllers {
    private final PendingTimeRecordClient pendingTimeRecordClient;

    public PendingTimeRecordControllers(PendingTimeRecordClient pendingTimeRecordClient) {
        this.pendingTimeRecordClient = pendingTimeRecordClient;
    }

    @GetMapping("/list-update-pending")
    public ResponseEntity<PendingTimeRecordListResponseDTO> listPendingToUpdate(
            Authentication authentication,
            @RequestParam(value = "action", required = true, defaultValue = "UPDATE") PendingTimeRecordAction.ActionType actionType,
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "size", required = false, defaultValue = "5") int perPage,
            @RequestParam(value = "employeeName", required = false) String employeeName,
            @RequestParam(value = "minDate", required = false) String minDate,
            @RequestParam(value = "maxDate", required = false) String maxDate,
            @RequestParam(value = "order", required = false, defaultValue = "ASC") OrderBy orderBy
    ) {
        UUID managerId = UUID.fromString(authentication.getName());

        PendingTimeRecordListResponseDTO clientResponse =
                this.pendingTimeRecordClient.listAllPendingToUpdate(
                        actionType,
                        managerId,
                        page,
                        perPage,
                        employeeName,
                        minDate,
                        maxDate,
                        orderBy
                );

        return ResponseEntity.status(200).body(clientResponse);
    }

    @PostMapping("/pending/{pendingTimeRecordId}")
    public ResponseEntity<Void> performPendingAction(
            @PathVariable("pendingTimeRecordId") UUID pendingTimeRecordId,
            @RequestParam(name = "action", required = true) PendingTimeRecordAction.ActionType action,
            Authentication authentication
    ) {
        this.pendingTimeRecordClient.performPendingAction(
                pendingTimeRecordId,
                action,
                UUID.fromString(authentication.getName())
        );

        return ResponseEntity.status(201).build();
    }

    @GetMapping("/pending/list/own")
    public ResponseEntity<PendingTimeRecordListResponseDTO> listAllOwnPendingActions(
            Authentication authentication,
            @RequestParam(name = "done", required = false, defaultValue = "false") boolean actionDone,
            @RequestParam(name = "page", required = false, defaultValue = "1") int page,
            @RequestParam(name = "perPage", required = false, defaultValue = "5") int perPage,
            @RequestParam(name = "month", required = false, defaultValue = "0")
            @Min(value = 1, message = "month must be more than 1")
            @Max(value = 12, message = "month must be less than 12")
            int month,
            @RequestParam(name = "year", required = false, defaultValue = "0")
            @Size(max = 4, min = 4, message = "year must have 4 characters")
            int year,
            @Min(value = 1, message = "day must be more than 1")
            @Max(value = 32, message = "day must be less than 32")
            @RequestParam(name = "day", required = false, defaultValue = "0") int day
    ) {
        UUID employeeId = UUID.fromString(authentication.getName());

        PendingTimeRecordListResponseDTO clientResponse = this.pendingTimeRecordClient.listAllOwnPendingActions(
                employeeId,
                actionDone,
                page,
                perPage,
                month,
                year,
                day
        );

        return ResponseEntity.status(200).body(clientResponse);
    }
}
