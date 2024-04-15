package com.root.employeeservice.dtos.out;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProfileResponseDTO {
    private UUID id;
    private String name;
    private String email;
    private Set<String> roles;
    private Date createdAt;
}
