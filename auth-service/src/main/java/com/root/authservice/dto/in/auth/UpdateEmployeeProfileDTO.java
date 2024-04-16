package com.root.authservice.dto.in.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.Size;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateEmployeeProfileDTO {
    private UUID requesterId;

    private String name;

    @Email(message = "email must be an valid e-mail")
    private String email;

    @Size(min = 6, message = "password must have at least 6 characters")
    private String password;

    private String position;
}
