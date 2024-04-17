package com.root.authservice.dto.out.auth;

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
public class TimeRecordResponseDTO {
    private UUID id;
    private Date recordHour;
    private Date createdAt;
}
