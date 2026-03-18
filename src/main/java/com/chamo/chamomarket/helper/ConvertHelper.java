package com.chamo.chamomarket.helper;

import com.chamo.chamomarket.dto.category.CategoryResponseDTO;
import com.chamo.chamomarket.dto.product.ProductResponseDTO;
import com.chamo.chamomarket.dto.product.ProductSimpleResponseDTO;
import com.chamo.chamomarket.entity.CategoryEntity;
import com.chamo.chamomarket.entity.ProductEntity;

import java.util.List;

public class ConvertHelper {

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

    public static CategoryResponseDTO convertCategoryEntityToCategoryResponseDTO(CategoryEntity categoryEntity, List<ProductSimpleResponseDTO> productsList){
        CategoryResponseDTO categoryResponseDTO = new CategoryResponseDTO();
        categoryResponseDTO.setId(categoryEntity.getId());
        categoryResponseDTO.setName(categoryEntity.getName());
        categoryResponseDTO.setStatus(categoryEntity.getStatus());
        categoryResponseDTO.setProducts(productsList);
        return categoryResponseDTO;
    }

}
