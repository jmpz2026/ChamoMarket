package com.chamo.chamomarket.dto.sale;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SaleItemDTO {

    @NotNull(message = "productId es obligatorio")
    private Long productId;

    @NotNull(message = "quantity es obligatorio")
    @Min(value = 1, message = "quantity debe ser mayor a 0")
    private Integer quantity;

    @NotNull(message = "unitPrice es obligatorio")
    @DecimalMin(value = "0.0", inclusive = false, message = "unitPrice debe ser mayor a 0")
    private Double unitPrice;
}