package com.chamo.chamomarket.service;

import com.chamo.chamomarket.dto.ApiResponse;
import com.chamo.chamomarket.dto.category.CategoryRequestDTO;
import com.chamo.chamomarket.dto.category.CategoryResponseDTO;
import com.chamo.chamomarket.dto.category.CategoryUpdateRequestDTO;
import com.chamo.chamomarket.dto.product.ProductSimpleResponseDTO;
import com.chamo.chamomarket.entity.CategoryEntity;
import com.chamo.chamomarket.entity.ProductEntity;
import com.chamo.chamomarket.exception.ResourceConflictException;
import com.chamo.chamomarket.exception.ResourceNotFoundException;
import com.chamo.chamomarket.mapper.CategoryMapper;
import com.chamo.chamomarket.mapper.ProductMapper;
import com.chamo.chamomarket.repository.CategoryRepository;
import com.chamo.chamomarket.repository.MessageRepository;
import com.chamo.chamomarket.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Validated
@RequiredArgsConstructor
@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    public ApiResponse<List<CategoryResponseDTO>> getAllCategories(){
        List<CategoryResponseDTO> categoryResponseDTOS = new ArrayList<>();
        List<CategoryEntity> categoryEntities = categoryRepository.findAllByStatusTrueOrderByNameAsc();
        categoryEntities.forEach(categoryEntity -> {
            List<ProductEntity> productsEntity = productRepository.findByCategoryId(categoryEntity.getId());
            List<ProductSimpleResponseDTO> productsList = productsEntity.stream().map(ProductMapper::convertProductEntityToProductSimpleResponseDTO).collect(Collectors.toList());
            CategoryResponseDTO categoryResponseDTO = CategoryMapper.convertCategoryEntityToCategoryResponseDTO(categoryEntity, productsList);
            categoryResponseDTOS.add(categoryResponseDTO);
        });

        ApiResponse<List<CategoryResponseDTO>> response = new ApiResponse<>();
        response.setSuccess(true);
        response.setData(categoryResponseDTOS);
        response.setMessage(MessageRepository.CATEGORY_ALL_FOUND);

        return response;
    }

    public ApiResponse<CategoryResponseDTO> getCategoryById(Long id){
        CategoryEntity categoryEntity = categoryRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException(MessageRepository.CATEGORY_NOT_FOUND)
        );

        List<ProductEntity> productsEntity = productRepository.findByCategoryId(id);

        List<ProductSimpleResponseDTO> productsList = productsEntity.stream().map(ProductMapper::convertProductEntityToProductSimpleResponseDTO).collect(Collectors.toList());

        CategoryResponseDTO categoryResponseDTO = CategoryMapper.convertCategoryEntityToCategoryResponseDTO(categoryEntity, productsList);

        ApiResponse<CategoryResponseDTO> response = new ApiResponse<>();
        response.setSuccess(true);
        response.setData(categoryResponseDTO);
        response.setMessage(MessageRepository.CATEGORY_FOUND);

        return response;
    }

    public ApiResponse<CategoryResponseDTO> createCategory(CategoryRequestDTO categoryRequestDTO){

        CategoryEntity categoryEntity = new CategoryEntity();
        categoryEntity.setName(categoryRequestDTO.getName());
        categoryEntity.setStatus(true);
        categoryRepository.save(categoryEntity);

        CategoryResponseDTO categoryResponseDTO = CategoryMapper.convertCategoryEntityToCategoryResponseDTO(categoryEntity, new ArrayList<>());

        ApiResponse<CategoryResponseDTO> response = new ApiResponse<>();
        response.setSuccess(true);
        response.setData(categoryResponseDTO);
        response.setMessage(MessageRepository.CATEGORY_CREATED);

        return response;
    }

    public ApiResponse<CategoryResponseDTO> updateCategory(CategoryUpdateRequestDTO categoryUpdateRequestDTO){
        CategoryEntity categoryEntity = categoryRepository.findById(categoryUpdateRequestDTO.getId()).orElseThrow(
                () -> new ResourceNotFoundException(MessageRepository.CATEGORY_NOT_FOUND)
        );

        if (categoryUpdateRequestDTO.getStatus() == categoryEntity.getStatus()){
            throw new ResourceConflictException(MessageRepository.CATEGORY_CONFLICT_STATUS);
        }

        List<ProductEntity> productsEntity = productRepository.findByCategoryId(categoryUpdateRequestDTO.getId());
        List<ProductSimpleResponseDTO> productsList = productsEntity.stream().map(ProductMapper::convertProductEntityToProductSimpleResponseDTO).collect(Collectors.toList());

        categoryEntity.setName(categoryUpdateRequestDTO.getName());
        categoryEntity.setStatus(categoryUpdateRequestDTO.getStatus());
        categoryRepository.save(categoryEntity);

        CategoryResponseDTO categoryResponseDTO = CategoryMapper.convertCategoryEntityToCategoryResponseDTO(categoryEntity, productsList);

        ApiResponse<CategoryResponseDTO> response = new ApiResponse<>();
        response.setSuccess(true);
        response.setData(categoryResponseDTO);
        response.setMessage(MessageRepository.CATEGORY_UPDATED);

        return response;
    }

    public void deleteCategory(Long id){
        CategoryEntity categoryEntity = categoryRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException(MessageRepository.CATEGORY_NOT_FOUND)
        );

        List<ProductEntity> productsEntity = productRepository.findByCategoryId(id);
        productsEntity.forEach(productEntity -> {
            productEntity.setStatus(false);
        });

        categoryEntity.setStatus(false);
        categoryRepository.save(categoryEntity);
    }
}
