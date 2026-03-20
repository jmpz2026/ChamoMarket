package com.chamo.chamomarket.mapper;

import com.chamo.chamomarket.dto.product.ProductResponseDTO;
import com.chamo.chamomarket.dto.product.ProductSimpleResponseDTO;
import com.chamo.chamomarket.entity.ProductEntity;

public class ProductMapper {

    public static ProductResponseDTO convertProductEntityToProductResponseDTO(ProductEntity productEntity){
        ProductResponseDTO productResponseDTO = new ProductResponseDTO();
        productResponseDTO.setId(productEntity.getId());
        productResponseDTO.setCode(productEntity.getCode());
        productResponseDTO.setName(productEntity.getName());
        productResponseDTO.setQuantity(productEntity.getQuantity());
        productResponseDTO.setStatus(productEntity.getStatus());
        productResponseDTO.setCategoryId(productEntity.getCategory().getId());
        return productResponseDTO;
    }

    public static ProductSimpleResponseDTO convertProductEntityToProductSimpleResponseDTO(ProductEntity productEntity){
        ProductSimpleResponseDTO productSimpleResponseDTO = new ProductSimpleResponseDTO();
        productSimpleResponseDTO.setId(productEntity.getId());
        productSimpleResponseDTO.setCode(productEntity.getCode());
        productSimpleResponseDTO.setName(productEntity.getName());
        productSimpleResponseDTO.setQuantity(productEntity.getQuantity());
        productSimpleResponseDTO.setStatus(productEntity.getStatus());
        return productSimpleResponseDTO;
    }
}
