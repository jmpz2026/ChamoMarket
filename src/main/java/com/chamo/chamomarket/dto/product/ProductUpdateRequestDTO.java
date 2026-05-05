package com.chamo.chamomarket.dto.product;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductUpdateRequestDTO {

    private Long id;
    private String name;
    private Boolean status;
    private Integer quantity;
    private Double price;

}
