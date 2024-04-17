package com.root.employeeservice.dtos.out;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeFullListResponseDTO {
    int page;
    int perPage;
    long totalElements;
    List<EmployeeResponseDTO> data;
}
