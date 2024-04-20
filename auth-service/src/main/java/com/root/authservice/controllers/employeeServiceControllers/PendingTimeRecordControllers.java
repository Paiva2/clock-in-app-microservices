package com.root.authservice.controllers.employeeServiceControllers;

import com.root.authservice.clients.employee_service_clients.PendingTimeRecordClient;
import com.root.authservice.dto.out.auth.PendingTimeRecordUpdateListResponseDTO;
import com.root.authservice.enums.OrderBy;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/employee")
public class PendingTimeRecordControllers {
    private final PendingTimeRecordClient pendingTimeRecordClient;

    public PendingTimeRecordControllers(PendingTimeRecordClient pendingTimeRecordClient) {
        this.pendingTimeRecordClient = pendingTimeRecordClient;
    }

    @GetMapping("/list-update-pending")
    public ResponseEntity<PendingTimeRecordUpdateListResponseDTO> listPendingToUpdate(
            Authentication authentication,
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "size", required = false, defaultValue = "5") int perPage,
            @RequestParam(value = "employeeName", required = false) String employeeName,
            @RequestParam(value = "minDate", required = false) String minDate,
            @RequestParam(value = "maxDate", required = false) String maxDate,
            @RequestParam(value = "order", required = false, defaultValue = "ASC") OrderBy orderBy
    ) {
        UUID managerId = UUID.fromString(authentication.getName());

        PendingTimeRecordUpdateListResponseDTO clientResponse =
                this.pendingTimeRecordClient.listAllPendingToUpdate(
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
}
