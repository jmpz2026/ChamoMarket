package com.chamo.chamomarket.controller;

import com.chamo.chamomarket.dto.ApiResponse;
import com.chamo.chamomarket.dto.category.CategoryRequestDTO;
import com.chamo.chamomarket.dto.category.CategoryResponseDTO;
import com.chamo.chamomarket.dto.product.ProductRequestDTO;
import com.chamo.chamomarket.dto.product.ProductResponseDTO;
import com.chamo.chamomarket.dto.product.ProductStockRequestDTO;
import com.chamo.chamomarket.dto.product.ProductUpdateRequestDTO;
import com.chamo.chamomarket.service.ProductService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@Controller
@RequestMapping("/product")
public class ProductController {

    private final ProductService productService;

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponseDTO>> getProductById(@PathVariable @NotNull @Min(1) Long id) {
        ApiResponse<ProductResponseDTO> response = productService.getProductById(id);
        return ResponseEntity.status(HttpStatus.FOUND).body(response);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ProductResponseDTO>> createProduct(@RequestBody @Valid ProductRequestDTO productRequestDTO) {
        ApiResponse<ProductResponseDTO> response = productService.createProduct(productRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping
    public ResponseEntity<ApiResponse<ProductResponseDTO>> updateProduct(@RequestBody @Valid ProductUpdateRequestDTO productUpdateRequestDTO) {
        ApiResponse<ProductResponseDTO> response = productService.updateProduct(productUpdateRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> deleteCategory(@PathVariable @NotNull @Min(1) Long id) {
        ApiResponse<?> response = productService.deleteProduct(id);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    // Añadir y Remover Stock

    @PutMapping("/{id}/add-stock")
    public ResponseEntity<ApiResponse<ProductResponseDTO>> addStock(@RequestBody @Valid ProductStockRequestDTO productStockRequestDTO, @PathVariable @NotNull @Min(1) Long id) {
        ApiResponse<ProductResponseDTO> response = productService.addStock(productStockRequestDTO, id);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}/remove-stock")
    public ResponseEntity<ApiResponse<ProductResponseDTO>> removeStock(@RequestBody @Valid ProductStockRequestDTO productStockRequestDTO, @PathVariable @NotNull @Min(1) Long id) {
        ApiResponse<ProductResponseDTO> response = productService.removeStock(productStockRequestDTO, id);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
