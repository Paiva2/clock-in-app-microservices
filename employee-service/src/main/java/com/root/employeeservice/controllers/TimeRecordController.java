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
import javax.validation.constraints.Min;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

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

    @PostMapping("/request-action")
    public PendingTimeRecordResponseDTO requestUpdate(
            @RequestParam(name = "action", required = true) PendingTimeRecordAction.ActionType actionType,
            @RequestParam("employeeId") UUID employeeId,
            @RequestBody @Valid NewPendingUpdateTimeRecordDTO dto
    ) {
        PendingTimeRecordAction requestUpdate = null;

        if (actionType.equals(PendingTimeRecordAction.ActionType.UPDATE)) {
            requestUpdate =
                    this.timeRecordService.requestTimeRecordUpdate(employeeId, dto.toEntity());
        } else if (actionType.equals(PendingTimeRecordAction.ActionType.DELETE)) {
            requestUpdate =
                    this.timeRecordService.requestTimeRecordDelete(employeeId, dto.toEntity());
        }

        PendingTimeRecordResponseDTO pendingTimeRecordResponseDTO = new PendingTimeRecordResponseDTO(
                requestUpdate.getId(),
                requestUpdate.getActionType(),
                requestUpdate.getTimeUpdated()
        );

        return pendingTimeRecordResponseDTO;
    }

    @GetMapping("/list-all")
    public Set<TimeRecordResponseDTO> listUserTimeRecords(
            @RequestParam(name = "employeeId", required = true) UUID employeeId,
            @RequestParam(name = "month", required = true) int month,
            @RequestParam(name = "year", required = true) int year,
            @RequestParam(name = "day", required = false, defaultValue = "0")
            @Min(value = 1, message = "day must be more than 0") int day
    ) {
        Set<TimeRecord> recordsList = this.timeRecordService.listEmployeeTimeRecords(
                employeeId,
                month,
                year,
                day
        );

        Set<TimeRecordResponseDTO> listInDto = recordsList.stream()
                .map(record -> new TimeRecordResponseDTO(
                        record.getId(),
                        record.getRecordHour(),
                        record.getCreatedAt()
                )).collect(Collectors.toSet());

        return listInDto;
    }
}
