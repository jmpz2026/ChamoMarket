package com.chamo.chamomarket.mapper;

import com.chamo.chamomarket.dto.category.CategoryResponseDTO;
import com.chamo.chamomarket.dto.product.ProductSimpleResponseDTO;
import com.chamo.chamomarket.entity.CategoryEntity;

import java.util.List;

public class CategoryMapper {

    public static CategoryResponseDTO convertCategoryEntityToCategoryResponseDTO(CategoryEntity categoryEntity, List<ProductSimpleResponseDTO> productsList){
        CategoryResponseDTO categoryResponseDTO = new CategoryResponseDTO();
        categoryResponseDTO.setId(categoryEntity.getId());
        categoryResponseDTO.setName(categoryEntity.getName());
        categoryResponseDTO.setStatus(categoryEntity.getStatus());
        categoryResponseDTO.setProducts(productsList);
        return categoryResponseDTO;
    }
}
