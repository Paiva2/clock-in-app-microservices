package com.root.authservice.dto.in.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SuperiorAttachRequestDTO {
    @NotNull(message = "superiorId can't be empty")
    @NotEmpty(message = "superiorId can't be empty")
    private String superiorId;

    @NotNull(message = "employeeId can't be empty")
    @NotEmpty(message = "employeeId can't be empty")
    private String employeeId;
}
