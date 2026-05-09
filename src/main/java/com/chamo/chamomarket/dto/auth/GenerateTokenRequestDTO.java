package com.chamo.chamomarket.dto.auth;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GenerateTokenRequestDTO {
    private Long employeeId;
    private String role;
    private String username;
}