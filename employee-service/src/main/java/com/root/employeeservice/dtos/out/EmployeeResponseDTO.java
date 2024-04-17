package com.root.employeeservice.dtos.out;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeResponseDTO {
    private UUID id;
    private String name;
    private String email;
    private String position;
    private Set<String> roles;
    private List<ManagerResponseDTO> managers;
    private boolean disabled;
    private Date disabledAt;
}
