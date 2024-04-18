package com.root.authservice.dto.out.auth;

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
public class PendingTimeRecordResponseDTO {
    private UUID id;
    private PendingTimeRecordAction.ActionType actionType;
    private Date timeUpdated;
}
