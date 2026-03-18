package com.chamo.chamomarket.dto.product;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductSimpleResponseDTO {

    private Long id;
    private String name;
    private String code;
    private Boolean status;
    private Integer quantity;
}
