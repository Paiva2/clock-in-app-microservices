package com.root.employeeservice.controllers;

import com.root.crossdbservice.entities.PendingTimeRecordAction;
import com.root.employeeservice.dtos.out.PendingTimeRecordActionResponseDTO;
import com.root.employeeservice.dtos.out.PendingTimeRecordUpdateListResponseDTO;
import com.root.employeeservice.dtos.out.TimeRecordResponseDTO;
import com.root.employeeservice.enums.OrderBy;
import com.root.employeeservice.services.PendingTimeRecordActionService;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/pending")
public class PendingTimeRecordController {
    private final PendingTimeRecordActionService pendingTimeRecordActionService;

    public PendingTimeRecordController(PendingTimeRecordActionService pendingTimeRecordActionService) {
        this.pendingTimeRecordActionService = pendingTimeRecordActionService;
    }

    @GetMapping("/list")
    public PendingTimeRecordUpdateListResponseDTO listAllPendingToUpdate(
            @RequestParam(value = "action", required = true, defaultValue = "UPDATE") PendingTimeRecordAction.ActionType actionType,
            @RequestParam(value = "managerId", required = true) UUID managerId,
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "size", required = false, defaultValue = "5") int perPage,
            @RequestParam(value = "employeeName", required = false) String employeeName,
            @RequestParam(value = "minDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date minDate,
            @RequestParam(value = "maxDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date maxDate,
            @RequestParam(value = "order", required = false, defaultValue = "ASC") OrderBy orderBy
    ) {
        Page<PendingTimeRecordAction> list = null;

        if (actionType.equals(PendingTimeRecordAction.ActionType.UPDATE)) {
            list = this.pendingTimeRecordActionService.listPendingToUpdate(
                    managerId,
                    page,
                    perPage,
                    employeeName,
                    minDate,
                    maxDate,
                    orderBy
            );
        } else if (actionType.equals(PendingTimeRecordAction.ActionType.DELETE)) {
            list = this.pendingTimeRecordActionService.listPendingToDelete(
                    managerId,
                    page,
                    perPage,
                    employeeName,
                    minDate,
                    maxDate,
                    orderBy
            );
        }

        PendingTimeRecordUpdateListResponseDTO pendingTimeRecordUpdateListResponseDTO =
                new PendingTimeRecordUpdateListResponseDTO(
                        list.getNumber() + 1,
                        list.getSize(),
                        list.getTotalElements(),
                        list.stream().map(pending ->
                                new PendingTimeRecordActionResponseDTO(
                                        pending.getId(),
                                        pending.getActionType().actionName(),
                                        pending.isActionDone(),
                                        pending.getTimeUpdated(),
                                        new TimeRecordResponseDTO(
                                                pending.getTimeRecord().getId(),
                                                pending.getTimeRecord().getRecordHour(),
                                                pending.getTimeRecord().getCreatedAt()
                                        ),
                                        pending.getCreatedAt()
                                )).collect(Collectors.toList())
                );

        return pendingTimeRecordUpdateListResponseDTO;
    }

    @PostMapping("/confirm-action/{pendingTimeRecordId}")
    public void proceedRequestedAction(
            @PathVariable("pendingTimeRecordId") UUID pendingTimeRecordId,
            @RequestParam(name = "action", required = true) PendingTimeRecordAction.ActionType action,
            @RequestParam(name = "managerId", required = true) UUID managerId
    ) {
        if (action.equals(PendingTimeRecordAction.ActionType.UPDATE)) {
            this.pendingTimeRecordActionService.confirmUpdate(
                    managerId,
                    pendingTimeRecordId
            );
        } else if (action.equals(PendingTimeRecordAction.ActionType.DELETE)) {
            this.pendingTimeRecordActionService.confirmDeletion(
                    managerId,
                    pendingTimeRecordId
            );
        }
    }
}
