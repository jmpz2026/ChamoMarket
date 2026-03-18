package com.chamo.chamomarket.helper;

import com.chamo.chamomarket.dto.product.ProductResponseDTO;
import com.chamo.chamomarket.entity.ProductEntity;

public class ConvertHelper {

    public static ProductResponseDTO convertProductEntityToProductResponseDTO(ProductEntity productEntity){
        ProductResponseDTO productResponseDTO = new ProductResponseDTO();
        productResponseDTO.setId(productEntity.getId());
        productResponseDTO.setCode(productEntity.getCode());
        productResponseDTO.setName(productEntity.getName());
        productResponseDTO.setStatus(productEntity.getStatus());
        productResponseDTO.setCategoryId(productEntity.getCategory().getId());
        return productResponseDTO;
    }
}
