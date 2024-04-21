package com.root.authservice.clients.employee_service_clients;

import com.root.authservice.dto.in.auth.NewPendingUpdateTimeRecord;
import com.root.authservice.dto.out.auth.PendingTimeRecordResponseDTO;
import com.root.authservice.dto.out.auth.TimeRecordResponseDTO;
import com.root.crossdbservice.entities.PendingTimeRecordAction;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.Set;
import java.util.UUID;

@FeignClient(
        name = "employee-time-rest-calls",
        url = "http://localhost:9003/api/v1/time"
)
@Component
public interface TimeRecordClientRest {
    @PostMapping("/register")
    TimeRecordResponseDTO registerTimeRecord(@RequestParam("employeeId") UUID employeeId);

    @PostMapping("/request-action")
    PendingTimeRecordResponseDTO requestUpdate(
            @RequestParam(name = "action", required = true) PendingTimeRecordAction.ActionType actionType,
            @RequestParam("employeeId") UUID employeeId,
            @RequestBody @Valid NewPendingUpdateTimeRecord dto
    );

    @GetMapping("/list-all")
    Set<TimeRecordResponseDTO> listAllEmployeeRecords(
            @RequestParam(name = "employeeId", required = true) UUID employeeId,
            @RequestParam(name = "month", required = true) int month,
            @RequestParam(name = "year", required = true) int year,
            @RequestParam(name = "day", required = false) int day
    );
}
