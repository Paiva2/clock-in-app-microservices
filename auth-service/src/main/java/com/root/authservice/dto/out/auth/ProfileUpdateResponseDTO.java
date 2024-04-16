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
public class ProfileUpdateResponseDTO {
    private UUID id;
    private String name;
    private String email;
    private String position;
    private Date updatedAt;

}
