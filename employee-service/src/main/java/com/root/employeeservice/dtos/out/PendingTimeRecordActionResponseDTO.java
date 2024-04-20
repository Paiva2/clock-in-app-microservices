package com.root.employeeservice.dtos.out;

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
public class PendingTimeRecordActionResponseDTO {
    private UUID id;
    private String actionType;
    private boolean actionDone;
    private Date newTimeToUpdate;
    private TimeRecordResponseDTO originalTimeRecord;
    private Date createdAt;
}
