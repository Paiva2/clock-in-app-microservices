package com.root.employeeservice.entities;

import lombok.Data;

@Data
public class EmailEntity {
    private String message;
    private String to;
    private String subject;

    public String forgotPasswordMessage(String newPassword) {
        return "You requested a new password, as a result we've changed your password to a new one!\n" +
                "Your new password is: " + newPassword + "\n" +
                "Don't forget to store this new password on a safe place! ;)";
    }
}
