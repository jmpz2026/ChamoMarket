package com.chamo.chamomarket.dto.category;

import com.chamo.chamomarket.dto.product.ProductResponseDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CategoryResponseDTO {

    private Long id;
    private String name;
    private String status;
    private List<ProductResponseDTO> products;
}
