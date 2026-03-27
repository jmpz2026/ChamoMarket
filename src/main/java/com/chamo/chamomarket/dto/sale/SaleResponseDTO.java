package com.chamo.chamomarket.dto.sale;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class SaleResponseDTO {
    private Long id;
    private LocalDateTime saleDate;
    private Long employeeId;
    private Double subtotal;
    private Double iva;
    private Double total;
    private List<SaleDetailDTO> details;
}