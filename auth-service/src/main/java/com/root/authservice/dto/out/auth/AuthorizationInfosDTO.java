package com.root.authservice.dto.out.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class AuthorizationInfosDTO {
    private String message;
    private String authToken;
}
