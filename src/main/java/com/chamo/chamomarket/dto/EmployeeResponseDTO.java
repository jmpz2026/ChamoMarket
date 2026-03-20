package com.chamo.chamomarket.dto;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Getter @Setter
public class EmployeeResponseDTO {
    private Long id;
    private String document;
    private String name;
    private String role;
    private LocalDate hireDate;
    private Double salary;
}