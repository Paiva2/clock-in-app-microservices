package com.root.authservice.dto.in.auth;

import com.root.crossdbservice.entities.PendingTimeRecordAction;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdatePendingTimeRecordActionDTO {
    private Date recordHour;
    private PendingTimeRecordAction.ActionType actionType;
}
