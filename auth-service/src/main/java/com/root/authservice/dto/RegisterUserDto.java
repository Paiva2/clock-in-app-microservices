package com.root.authservice.dto;

import com.root.crossdbservice.entities.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class RegisterUserDto {
    @NotBlank(message = "username can't be blank")
    @NotNull(message = "username can't be null")
    private String username;

    @NotBlank(message = "password can't be blank")
    @NotNull(message = "password can't be null")
    @Size(min = 6, message = "password must have at least 6 characters")
    private String password;

    @Email(message = "email must be an valid e-mail")
    @NotBlank(message = "email can't be blank")
    @NotNull(message = "email can't be null")
    private String email;

    public UserEntity toEntity(){
        UserEntity user = new UserEntity();
        user.setEmail(this.email);
        user.setPassword(this.password);
        user.setName(this.username);

        return user;
    }
}
