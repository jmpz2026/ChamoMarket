package com.chamo.chamomarket.dto.product;

import com.chamo.chamomarket.entity.CategoryEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductResponseDTO {

    private Long id;
    private String name;
    private String code;
    private Boolean status;
    private CategoryEntity category;
}
