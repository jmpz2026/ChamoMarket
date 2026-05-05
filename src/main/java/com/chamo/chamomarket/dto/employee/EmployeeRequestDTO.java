package com.chamo.chamomarket.dto.employee;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Getter @Setter
public class EmployeeRequestDTO {

    @NotBlank(message = "La cédula es obligatoria")
    private String document;

    @NotBlank(message = "El nombre es obligatorio")
    private String name;

    @NotBlank(message = "El cargo es obligatorio")
    private String role; // Se valida contra el Enum en el Service

    @NotNull(message = "La fecha de ingreso es obligatoria")
    private LocalDate hireDate;

    @Positive(message = "El salario debe ser mayor a cero")
    private Double salary;
}