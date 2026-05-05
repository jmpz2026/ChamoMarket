package com.chamo.chamomarket.dto.sale;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class SaleRequestDTO {

    @NotNull(message = "employeeId es obligatorio")
    private Long employeeId;

    @NotEmpty(message = "Debe enviar al menos un item")
    private List<@Valid SaleItemDTO> items;
}