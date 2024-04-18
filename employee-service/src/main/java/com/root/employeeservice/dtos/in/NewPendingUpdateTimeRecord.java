package com.root.employeeservice.dtos.in;

import com.root.crossdbservice.entities.TimeRecord;
import com.root.employeeservice.exceptions.BadRequestException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class NewPendingUpdateTimeRecord {
    @NotBlank(message = "id can't be blank")
    @NotNull(message = "id can't be null")
    @NotEmpty(message = "id can't be empty")
    private String id;

    @Pattern(
            regexp = "^\\d{4}-(?:0[1-9]|1[0-2])-(?:[0-2][1-9]|[1-3]0|3[01])T(?:[0-1][0-9]|2[0-3])(?::[0-6]\\d)(?::[0-6]\\d)?(?:\\.\\d{3})?(?:[+-][0-2]\\d:[0-5]\\d|Z)?$",
            message = "recordHour must be an ISO-8601 date format"
    )
    @NotEmpty(message = "recordHour can't be empty")
    @NotNull(message = "recordHour can't be null")
    @NotBlank(message = "recordHour can't be blank")
    private Date recordHour;

    public TimeRecord toEntity() {
        TimeRecord entity = new TimeRecord();
        entity.setId(UUID.fromString(this.id));

        entity.setRecordHour(this.recordHour);

        return entity;
    }
}
