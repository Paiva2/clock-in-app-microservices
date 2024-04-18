package com.root.authservice.clients.employee_service_clients;

import com.root.authservice.dto.in.auth.NewPendingUpdateTimeRecord;
import com.root.authservice.dto.out.auth.PendingTimeRecordResponseDTO;
import com.root.authservice.dto.out.auth.TimeRecordResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import java.util.UUID;

@FeignClient(
        name = "employee-time-rest-calls",
        url = "http://localhost:9003/api/v1/time"
)
@Component
public interface TimeRecordClientRest {
    @PostMapping("/register")
    TimeRecordResponseDTO registerTimeRecord(@RequestParam("employeeId") UUID employeeId);

    @PostMapping("/request-update")
    PendingTimeRecordResponseDTO requestUpdate(
            @RequestParam("employeeId") UUID employeeId,
            @RequestBody @Valid NewPendingUpdateTimeRecord dto
    );
}
