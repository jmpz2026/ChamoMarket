package com.chamo.chamomarket.service;

import com.chamo.chamomarket.dto.ApiResponse;
import com.chamo.chamomarket.dto.product.ProductRequestDTO;
import com.chamo.chamomarket.dto.product.ProductResponseDTO;
import com.chamo.chamomarket.dto.product.ProductStockRequestDTO;
import com.chamo.chamomarket.dto.product.ProductUpdateRequestDTO;
import com.chamo.chamomarket.entity.CategoryEntity;
import com.chamo.chamomarket.entity.ProductEntity;
import com.chamo.chamomarket.exception.ResourceBadRequestException;
import com.chamo.chamomarket.exception.ResourceNoContentException;
import com.chamo.chamomarket.exception.ResourceNotFoundException;
import com.chamo.chamomarket.mapper.ProductMapper;
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

        ProductResponseDTO productResponseDTO = ProductMapper.convertProductEntityToProductResponseDTO(productEntity);

        ApiResponse<ProductResponseDTO> response = new ApiResponse<>();
        response.setSuccess(true);
        response.setData(productResponseDTO);
        response.setMessage(MessageRepository.PRODUCT_FOUND);

        return response;
    }

    public ApiResponse<ProductResponseDTO> createProduct(ProductRequestDTO productRequestDTO) {
        CategoryEntity categoryEntity = categoryRepository.findById(productRequestDTO.getCategoryId()).orElseThrow(
                () -> new ResourceNotFoundException(MessageRepository.CATEGORY_NOT_FOUND)
        );

        if (categoryEntity.getStatus() == false){
            throw new ResourceNoContentException(MessageRepository.CATEGORY_NOT_AVAILABLE);
        }

        String code = UUID.randomUUID().toString();

        ProductEntity productEntity = new ProductEntity();
        productEntity.setName(productRequestDTO.getName());
        productEntity.setStatus(productRequestDTO.getStatus());
        productEntity.setQuantity(productRequestDTO.getQuantity());
        productEntity.setCode(code);
        productEntity.setCategory(categoryEntity);
        productRepository.save(productEntity);

        ProductResponseDTO productResponseDTO = ProductMapper.convertProductEntityToProductResponseDTO(productEntity);

        ApiResponse<ProductResponseDTO> response = new ApiResponse<>();
        response.setSuccess(true);
        response.setData(productResponseDTO);
        response.setMessage(MessageRepository.PRODUCT_CREATED);

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

        ProductResponseDTO productResponseDTO = ProductMapper.convertProductEntityToProductResponseDTO(productEntity);

        ApiResponse<ProductResponseDTO> response = new ApiResponse<>();
        response.setSuccess(true);
        response.setData(productResponseDTO);
        response.setMessage(MessageRepository.PRODUCT_UPDATED);

        return response;
    }

    public ApiResponse<?> deleteProduct(Long id){
        ProductEntity productEntity = productRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException(MessageRepository.PRODUCT_NOT_FOUND)
        );

        if (productEntity.getStatus() == false){
            throw new ResourceNoContentException(MessageRepository.PRODUCT_NOT_AVAILABLE);
        }

        productEntity.setStatus(false);
        productRepository.save(productEntity);

        ApiResponse<?> response = new ApiResponse<>();
        response.setSuccess(true);
        response.setData(null);
        response.setMessage(MessageRepository.PRODUCT_DISABLED);

        return response;
    }

    // Añadir y remover Stock
    public ApiResponse<ProductResponseDTO> addStock(ProductStockRequestDTO productStockRequestDTO, Long id) {
        ProductEntity productEntity = productRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException(MessageRepository.PRODUCT_NOT_FOUND)
        );

        if (productEntity.getStatus() == false){
            throw new ResourceNoContentException(MessageRepository.PRODUCT_NOT_AVAILABLE);
        }

        productEntity.setQuantity(productEntity.getQuantity() + productStockRequestDTO.getQuantity());
        productRepository.save(productEntity);

        ApiResponse<ProductResponseDTO> response = new ApiResponse<>();
        response.setSuccess(true);
        response.setData(ProductMapper.convertProductEntityToProductResponseDTO(productEntity));
        response.setMessage(MessageRepository.PRODUCT_ADDED);

        return response;
    }

    public ApiResponse<ProductResponseDTO> removeStock(ProductStockRequestDTO productStockRequestDTO, Long id) {
        ProductEntity productEntity = productRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException(MessageRepository.PRODUCT_NOT_FOUND)
        );

        if (productEntity.getStatus() == false){
            throw new ResourceNoContentException(MessageRepository.PRODUCT_NOT_AVAILABLE);
        }

        if (productEntity.getQuantity() < productStockRequestDTO.getQuantity() || (productEntity.getQuantity() - productStockRequestDTO.getQuantity() <= 0)){
            throw new ResourceBadRequestException(MessageRepository.PRODUCT_NOT_ENOUGH);
        }

        productEntity.setQuantity(productEntity.getQuantity() - productStockRequestDTO.getQuantity());
        productRepository.save(productEntity);

        ApiResponse<ProductResponseDTO> response = new ApiResponse<>();
        response.setSuccess(true);
        response.setData(ProductMapper.convertProductEntityToProductResponseDTO(productEntity));
        response.setMessage(MessageRepository.PRODUCT_REMOVED);

        return response;
    }
}
