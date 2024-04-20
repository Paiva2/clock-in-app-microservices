package com.root.employeeservice.controllers;

import com.root.crossdbservice.entities.PendingTimeRecordAction;
import com.root.crossdbservice.entities.TimeRecord;
import com.root.crossdbservice.entities.UserEntity;
import com.root.employeeservice.dtos.in.NewPendingUpdateTimeRecordDTO;
import com.root.employeeservice.dtos.out.PendingTimeRecordResponseDTO;
import com.root.employeeservice.dtos.out.TimeRecordResponseDTO;
import com.root.employeeservice.services.TimeRecordService;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/time")
public class TimeRecordController {
    private final TimeRecordService timeRecordService;

    public TimeRecordController(TimeRecordService timeRecordService) {
        this.timeRecordService = timeRecordService;
    }

    @PostMapping("/register")
    public TimeRecordResponseDTO recordTime(@RequestParam("employeeId") UUID employeeId) {
        UserEntity employee = new UserEntity();
        employee.setId(employeeId);

        TimeRecord newRecordTime = this.timeRecordService.insertRegister(employee);

        TimeRecordResponseDTO timeRecordResponseDTO = new TimeRecordResponseDTO(
                newRecordTime.getId(),
                newRecordTime.getRecordHour(),
                newRecordTime.getCreatedAt()
        );

        return timeRecordResponseDTO;
    }

    @PostMapping("/request-update")
    public PendingTimeRecordResponseDTO requestUpdate(
            @RequestParam("employeeId") UUID employeeId,
            @RequestBody @Valid NewPendingUpdateTimeRecordDTO dto
    ) {
        PendingTimeRecordAction requestUpdate =
                this.timeRecordService.requestTimeRecordUpdate(employeeId, dto.toEntity());

        PendingTimeRecordResponseDTO pendingTimeRecordResponseDTO = new PendingTimeRecordResponseDTO(
                requestUpdate.getId(),
                requestUpdate.getActionType(),
                requestUpdate.getTimeUpdated()
        );

        return pendingTimeRecordResponseDTO;
    }
}
