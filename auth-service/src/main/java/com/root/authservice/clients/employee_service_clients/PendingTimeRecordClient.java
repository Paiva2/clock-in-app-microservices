package com.root.authservice.clients.employee_service_clients;

import com.root.authservice.dto.in.auth.UpdatePendingTimeRecordActionDTO;
import com.root.authservice.dto.out.auth.PendingTimeRecordListResponseDTO;
import com.root.authservice.dto.out.auth.PendingTimeRecordResponseDTO;
import com.root.authservice.enums.OrderBy;
import com.root.crossdbservice.entities.PendingTimeRecordAction;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.UUID;

@FeignClient(
        name = "pending-time-records-client",
        url = "http://localhost:9003/api/v1/pending"
)
@Component
public interface PendingTimeRecordClient {
    @GetMapping("/list")
    PendingTimeRecordListResponseDTO listAllPending(
            @RequestParam(value = "action", required = true, defaultValue = "UPDATE") PendingTimeRecordAction.ActionType actionType,
            @RequestParam(value = "managerId", required = true) UUID managerId,
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "size", required = false, defaultValue = "5") int perPage,
            @RequestParam(value = "employeeName", required = false) String employeeName,
            @RequestParam(value = "minDate", required = false) String minDate,
            @RequestParam(value = "maxDate", required = false) String maxDate,
            @RequestParam(value = "order", required = false, defaultValue = "asc") OrderBy orderBy
    );

    @PostMapping("/confirm-action/{pendingTimeRecordId}")
    void performPendingAction(
            @PathVariable("pendingTimeRecordId") UUID pendingTimeRecordId,
            @RequestParam(name = "action", required = true) PendingTimeRecordAction.ActionType action,
            @RequestParam(name = "managerId", required = true) UUID managerId
    );

    @GetMapping("/list/own")
    PendingTimeRecordListResponseDTO listAllOwnPendingActions(
            @RequestParam(value = "employeeId", required = true) UUID employeeId,
            @RequestParam(name = "done", required = false, defaultValue = "false") boolean actionDone,
            @RequestParam(name = "page", required = false, defaultValue = "1") int page,
            @RequestParam(name = "perPage", required = false, defaultValue = "5") int perPage,
            @RequestParam(name = "month", required = false, defaultValue = "0")
            @Min(value = 1, message = "month must be more than 1")
            @Max(value = 12, message = "month must be less than 12")
            int month,
            @RequestParam(name = "year", required = false, defaultValue = "0") int year,
            @Min(value = 1, message = "day must be more than 1")
            @Max(value = 32, message = "day must be less than 32")
            @RequestParam(name = "day", required = false, defaultValue = "0") int day
    );

    @PutMapping("/update/{pendingTimeRecordId}")
    PendingTimeRecordResponseDTO updateOwnPendingAction(
            @RequestParam(value = "employeeId") UUID employeeId,
            @RequestBody @Valid UpdatePendingTimeRecordActionDTO dto,
            @PathVariable("pendingTimeRecordId") UUID pendingTimeRecordId
    );
}
