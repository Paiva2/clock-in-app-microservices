package com.root.authservice.dto.in.auth;

import com.root.crossdbservice.entities.TimeRecord;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class NewPendingUpdateTimeRecordDTO {
    @NotBlank(message = "id can't be blank")
    @NotNull(message = "id can't be null")
    @NotEmpty(message = "id can't be empty")
    private String id;

    @NotNull(message = "recordHour can't be null")
    private Date recordHour;

    public TimeRecord toEntity() {
        TimeRecord entity = new TimeRecord();
        entity.setId(UUID.fromString(this.id));

        entity.setRecordHour(this.recordHour);

        return entity;
    }
}
