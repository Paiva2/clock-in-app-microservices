package com.root.authservice.dto.in.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ForgotPasswordEmployeeDTO {
    @Email(message = "email must be an valid e-mail")
    @NotBlank(message = "email can't be blank")
    @NotNull(message = "email can't be null")
    @NotEmpty(message = "email can't be empty")
    private String email;
}
