package com.chamo.chamomarket.dto.sale;

import lombok.Data;

@Data
public class SaleDetailDTO {
    private Long productId;
    private String productName;
    private Integer quantity;
    private Double price;
    private Double subtotal;
}