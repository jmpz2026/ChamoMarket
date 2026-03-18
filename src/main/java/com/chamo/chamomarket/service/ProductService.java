package com.chamo.chamomarket.service;

import com.chamo.chamomarket.dto.ApiResponse;
import com.chamo.chamomarket.dto.product.ProductRequestDTO;
import com.chamo.chamomarket.dto.product.ProductResponseDTO;
import com.chamo.chamomarket.dto.product.ProductUpdateRequestDTO;
import com.chamo.chamomarket.entity.CategoryEntity;
import com.chamo.chamomarket.entity.ProductEntity;
import com.chamo.chamomarket.exception.ResourceNotFoundException;
import com.chamo.chamomarket.helper.ConvertHelper;
import com.chamo.chamomarket.repository.CategoryRepository;
import com.chamo.chamomarket.repository.MessageRepository;
import com.chamo.chamomarket.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;


@RequiredArgsConstructor
@Service
public class ProductService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    public ApiResponse<ProductResponseDTO> getProductById(Long id){
        ProductEntity productEntity = productRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException(MessageRepository.PRODUCT_NOT_FOUND)
        );

        ProductResponseDTO productResponseDTO = ConvertHelper.convertProductEntityToProductResponseDTO(productEntity);

        ApiResponse<ProductResponseDTO> response = new ApiResponse<>();
        response.setData(productResponseDTO);
        response.setMessage(MessageRepository.PRODUCT_FOUND);
        response.setSuccess(true);

        return response;
    }

    public ApiResponse<ProductResponseDTO> createProduct(ProductRequestDTO productRequestDTO) {
        CategoryEntity categoryEntity = categoryRepository.findById(productRequestDTO.getCategoryId()).orElseThrow(
                () -> new ResourceNotFoundException(MessageRepository.CATEGORY_NOT_FOUND)
        );

        String code = UUID.randomUUID().toString();

        ProductEntity productEntity = new ProductEntity();
        productEntity.setName(productRequestDTO.getName());
        productEntity.setStatus(productRequestDTO.getStatus());
        productEntity.setQuantity(productRequestDTO.getQuantity());
        productEntity.setCode(code);
        productEntity.setCategory(categoryEntity);
        productRepository.save(productEntity);


        ProductResponseDTO productResponseDTO = ConvertHelper.convertProductEntityToProductResponseDTO(productEntity);

        ApiResponse<ProductResponseDTO> response = new ApiResponse<>();
        response.setData(productResponseDTO);
        response.setMessage(MessageRepository.PRODUCT_CREATED);
        response.setSuccess(true);

        return response;
    }

    public ApiResponse<ProductResponseDTO> updateProduct(ProductUpdateRequestDTO productUpdateRequestDTO) {
        ProductEntity productEntity = productRepository.findById(productUpdateRequestDTO.getId()).orElseThrow(
                () -> new ResourceNotFoundException(MessageRepository.PRODUCT_NOT_FOUND)
        );

        productEntity.setName(productUpdateRequestDTO.getName());
        productEntity.setStatus(productUpdateRequestDTO.getStatus());
        productEntity.setQuantity(productUpdateRequestDTO.getQuantity());
        productEntity.setStatus(productUpdateRequestDTO.getStatus());
        productRepository.save(productEntity);

        ProductResponseDTO productResponseDTO = ConvertHelper.convertProductEntityToProductResponseDTO(productEntity);

        ApiResponse<ProductResponseDTO> response = new ApiResponse<>();
        response.setData(productResponseDTO);
        response.setSuccess(true);
        response.setMessage(MessageRepository.PRODUCT_UPDATED);

        return response;
    }
}
