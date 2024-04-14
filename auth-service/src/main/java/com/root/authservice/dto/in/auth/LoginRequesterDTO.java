package com.root.authservice.dto.in.auth;

import com.root.crossdbservice.entities.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequesterDTO {
    @Email(message = "email must be an valid e-mail")
    @NotNull(message = "email can't be null")
    @NotBlank(message = "email can't be blank")
    private String email;

    @NotNull(message = "password can't be null")
    @NotBlank(message = "password can't be blank")
    @Size(min = 6, message = "password must have at least 6 characters")
    private String password;

    public UserEntity toEntity() {
        UserEntity user = new UserEntity();
        user.setPassword(this.password);
        user.setEmail(this.email);

        return user;
    }
}
