package com.root.employeeservice.dtos.in;

import com.root.crossdbservice.entities.PendingTimeRecordAction;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdatePendingTimeRecordActionDTO {
    private Date recordHour;

    private PendingTimeRecordAction.ActionType actionType;

    public PendingTimeRecordAction toEntity(UUID pendingId) {
        PendingTimeRecordAction entity = new PendingTimeRecordAction();
        entity.setId(pendingId);
        entity.setTimeUpdated(this.recordHour);
        entity.setActionType(this.actionType);

        return entity;
    }
}
