package com.root.authservice.dto.out.auth;

import com.root.crossdbservice.entities.RoleEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserRoleResponseDTO {
    private RoleEntity.Role role;
}
