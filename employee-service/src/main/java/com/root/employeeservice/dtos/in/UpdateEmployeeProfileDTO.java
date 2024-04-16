package com.root.employeeservice.dtos.in;

import com.root.crossdbservice.entities.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateEmployeeProfileDTO {
    @NotNull(message = "requesterId can't be empty")
    @NotEmpty(message = "requesterId can't be empty")
    private String requesterId;

    private String name;

    @Email(message = "email must be an valid e-mail")
    private String email;

    @Size(min = 6, message = "password must have at least 6 characters")
    private String password;

    private String position;

    public UserEntity toEntity(UUID userId) {
        UserEntity user = new UserEntity();
        user.setId(userId);
        user.setName(this.name);
        user.setEmail(this.email);
        user.setPassword(this.password);
        user.setPosition(this.position);

        return user;
    }
}
