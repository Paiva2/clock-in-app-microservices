package com.root.authservice.clients.employee_service_clients;

import com.root.authservice.dto.out.auth.PendingTimeRecordUpdateListResponseDTO;
import com.root.authservice.enums.OrderBy;
import com.root.crossdbservice.entities.PendingTimeRecordAction;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@FeignClient(
        name = "pending-time-records-client",
        url = "http://localhost:9003/api/v1/pending"
)
@Component
public interface PendingTimeRecordClient {
    @GetMapping("/list")
    PendingTimeRecordUpdateListResponseDTO listAllPendingToUpdate(
            @RequestParam(value = "action", required = true, defaultValue = "UPDATE") PendingTimeRecordAction.ActionType actionType,
            @RequestParam(value = "managerId", required = true) UUID managerId,
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "size", required = false, defaultValue = "5") int perPage,
            @RequestParam(value = "employeeName", required = false) String employeeName,
            @RequestParam(value = "minDate", required = false) String minDate,
            @RequestParam(value = "maxDate", required = false) String maxDate,
            @RequestParam(value = "order", required = false, defaultValue = "asc") OrderBy orderBy
    );
}
