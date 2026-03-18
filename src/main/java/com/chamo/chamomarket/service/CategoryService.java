package com.chamo.chamomarket.service;

import com.chamo.chamomarket.dto.ApiResponse;
import com.chamo.chamomarket.dto.category.CategoryRequestDTO;
import com.chamo.chamomarket.dto.category.CategoryResponseDTO;
import com.chamo.chamomarket.dto.category.CategoryUpdateRequestDTO;
import com.chamo.chamomarket.dto.product.ProductResponseDTO;
import com.chamo.chamomarket.entity.CategoryEntity;
import com.chamo.chamomarket.entity.ProductEntity;
import com.chamo.chamomarket.exception.ResourceExistsException;
import com.chamo.chamomarket.exception.ResourceNotFoundException;
import com.chamo.chamomarket.helper.ConvertHelper;
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

    public ApiResponse<CategoryResponseDTO> getCategoryById(Long id){
        CategoryEntity categoryEntity = categoryRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException(MessageRepository.CATEGORY_NOT_FOUND)
        );

        List<ProductEntity> productsEntity = productRepository.findByCategoryId(id);

        List<ProductResponseDTO> productsList = productsEntity.stream().map(ConvertHelper::convertProductEntityToProductResponseDTO).collect(Collectors.toList());

        CategoryResponseDTO categoryResponseDTO = new CategoryResponseDTO();
        categoryResponseDTO.setId(categoryEntity.getId());
        categoryResponseDTO.setName(categoryEntity.getName());
        categoryResponseDTO.setStatus(categoryEntity.getStatus());
        categoryResponseDTO.setProducts(productsList);

        ApiResponse<CategoryResponseDTO> response = new ApiResponse<>();
        response.setSuccess(true);
        response.setData(categoryResponseDTO);
        response.setMessage(MessageRepository.CATEGORY_FOUND);

        return response;
    }

    public ApiResponse<CategoryResponseDTO> createCategory(CategoryRequestDTO categoryRequestDTO){
        if (categoryRepository.existsByName(categoryRequestDTO.getName())){
            throw new ResourceExistsException(MessageRepository.CATEGORY_CONFLICT_NAME);
        }

        CategoryEntity categoryEntity = new CategoryEntity();
        categoryEntity.setName(categoryRequestDTO.getName());
        categoryEntity.setStatus(true);
        categoryRepository.save(categoryEntity);

        CategoryResponseDTO categoryResponseDTO = new CategoryResponseDTO();
        categoryResponseDTO.setId(categoryEntity.getId());
        categoryResponseDTO.setName(categoryEntity.getName());
        categoryResponseDTO.setStatus(categoryEntity.getStatus());
        categoryResponseDTO.setProducts(new ArrayList<>());

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

        if (categoryRepository.existsByName(categoryUpdateRequestDTO.getName()) && !categoryEntity.getName().equals(categoryUpdateRequestDTO.getName())){
            throw new ResourceExistsException(MessageRepository.CATEGORY_CONFLICT_NAME);
        }

        if (categoryUpdateRequestDTO.getStatus() == categoryEntity.getStatus()){
            throw new ResourceExistsException(MessageRepository.CATEGORY_CONFLICT_STATUS);
        }

        List<ProductEntity> productsEntity = productRepository.findByCategoryId(categoryUpdateRequestDTO.getId());
        List<ProductResponseDTO> productsList = productsEntity.stream().map(ConvertHelper::convertProductEntityToProductResponseDTO).collect(Collectors.toList());

        categoryEntity.setName(categoryUpdateRequestDTO.getName());
        categoryEntity.setStatus(categoryUpdateRequestDTO.getStatus());
        categoryRepository.save(categoryEntity);

        CategoryResponseDTO categoryResponseDTO = new CategoryResponseDTO();
        categoryResponseDTO.setId(categoryEntity.getId());
        categoryResponseDTO.setName(categoryEntity.getName());
        categoryResponseDTO.setStatus(categoryEntity.getStatus());
        categoryResponseDTO.setProducts(productsList);

        ApiResponse<CategoryResponseDTO> response = new ApiResponse<>();
        response.setSuccess(true);
        response.setData(categoryResponseDTO);
        response.setMessage(MessageRepository.CATEGORY_UPDATED);

        return response;
    }
}
