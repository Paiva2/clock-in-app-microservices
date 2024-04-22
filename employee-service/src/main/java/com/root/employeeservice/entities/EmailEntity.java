package com.root.employeeservice.entities;

import lombok.Data;

@Data
public class EmailEntity {
    private String message;
    private String from;
    private String to;
    private String subject;
}
