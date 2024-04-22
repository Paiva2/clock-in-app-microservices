package com.root.employeeservice.controllers;

import com.root.crossdbservice.entities.PendingTimeRecordAction;
import com.root.employeeservice.dtos.in.UpdatePendingTimeRecordActionDTO;
import com.root.employeeservice.dtos.out.PendingTimeRecordActionResponseDTO;
import com.root.employeeservice.dtos.out.PendingTimeRecordListResponseDTO;
import com.root.employeeservice.dtos.out.PendingTimeRecordResponseDTO;
import com.root.employeeservice.dtos.out.TimeRecordResponseDTO;
import com.root.employeeservice.enums.OrderBy;
import com.root.employeeservice.services.PendingTimeRecordActionService;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
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
    public PendingTimeRecordListResponseDTO listAlLPending(
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

        PendingTimeRecordListResponseDTO pendingTimeRecordListResponseDTO =
                new PendingTimeRecordListResponseDTO(
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

        return pendingTimeRecordListResponseDTO;
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

    @GetMapping("/list/own")
    public PendingTimeRecordListResponseDTO listOwnPendingActions(
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
    ) {
        Page<PendingTimeRecordAction> pendingsPageable = this.pendingTimeRecordActionService.listOwnPendingActions(
                employeeId,
                actionDone,
                page,
                perPage,
                month,
                year,
                day
        );

        PendingTimeRecordListResponseDTO pendingsListDto = new PendingTimeRecordListResponseDTO(
                pendingsPageable.getNumber() + 1,
                pendingsPageable.getSize(),
                pendingsPageable.getTotalElements(),
                pendingsPageable.getContent().stream().map(pendingAction ->
                        new PendingTimeRecordActionResponseDTO(
                                pendingAction.getId(),
                                pendingAction.getActionType().actionName(),
                                pendingAction.isActionDone(),
                                pendingAction.getTimeUpdated(),
                                new TimeRecordResponseDTO(
                                        pendingAction.getTimeRecord().getId(),
                                        pendingAction.getTimeRecord().getRecordHour(),
                                        pendingAction.getTimeRecord().getCreatedAt()
                                ),
                                pendingAction.getCreatedAt()
                        )
                ).collect(Collectors.toList())
        );

        return pendingsListDto;
    }

    @PutMapping("/update/{pendingTimeRecordId}")
    public PendingTimeRecordResponseDTO updatePendingAction(
            @RequestParam(value = "employeeId") UUID employeeId,
            @RequestBody @Valid UpdatePendingTimeRecordActionDTO dto,
            @PathVariable("pendingTimeRecordId") UUID pendingTimeRecordId
    ) {
        PendingTimeRecordAction pendingUpdated = this.pendingTimeRecordActionService.updatePendingTimeRecord(
                employeeId,
                dto.toEntity(pendingTimeRecordId)
        );

        PendingTimeRecordResponseDTO pendingTimeRecordResponseDTO =
                new PendingTimeRecordResponseDTO(
                        pendingUpdated.getId(),
                        pendingUpdated.getActionType(),
                        pendingUpdated.getTimeUpdated()
                );

        return pendingTimeRecordResponseDTO;
    }
}
