package com.chamo.chamomarket.dto.auth;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequestDTO {

    private String document;
    private String name;
    private String role;
    private Double salary;

    private String username;
    private String password;
}