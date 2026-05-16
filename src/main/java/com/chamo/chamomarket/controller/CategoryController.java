package com.chamo.chamomarket.controller;

import com.chamo.chamomarket.dto.ApiResponse;
import com.chamo.chamomarket.dto.category.CategoryRequestDTO;
import com.chamo.chamomarket.dto.category.CategoryResponseDTO;
import com.chamo.chamomarket.dto.category.CategoryUpdateRequestDTO;
import com.chamo.chamomarket.service.CategoryService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@Controller
@RequestMapping("/category")
public class CategoryController {

    private final CategoryService categoryService;

    /**
     * @return Lista de todas las categorias
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<CategoryResponseDTO>>> getAllCategories() {
        ApiResponse<List<CategoryResponseDTO>> response = categoryService.getAllCategories();
        return ResponseEntity
                .ok(response);
    }

    /**
     * @return Categoria en especifico
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryResponseDTO>> getCategoryById(@PathVariable @NotNull @Min(1) Long id) {
        ApiResponse<CategoryResponseDTO> response = categoryService.getCategoryById(id);
        return ResponseEntity
                .ok(response);
    }

    /**
     * Metodo para crear una categoria segun CategoryRequestDTO
     * @param categoryRequestDTO
     * @return DTO, con mensaje de que categoria fue creada
     */
    @PostMapping
    public ResponseEntity<ApiResponse<CategoryResponseDTO>> createCategory(@RequestBody @Valid CategoryRequestDTO categoryRequestDTO) {
        ApiResponse<CategoryResponseDTO> response = categoryService.createCategory(categoryRequestDTO);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    /**
     * Meotodo para modificar una categoria
     * @param categoryUpdateRequestDTO
     * @return DTO, con mensaje de que categoria fue modificada
     */
    @PutMapping
    public ResponseEntity<ApiResponse<CategoryResponseDTO>> updateCategory(@RequestBody @Valid CategoryUpdateRequestDTO categoryUpdateRequestDTO) {
        ApiResponse<CategoryResponseDTO> response = categoryService.updateCategory(categoryUpdateRequestDTO);
        return ResponseEntity.ok(response);
    }

    /**
     * Meotodo para eliminar una categoria
     * @param id
     * @return Mensaje de que la categoria fue eliminada
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> deleteCategory(@PathVariable @NotNull @Min(1) Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity
                .noContent()
                .build();
    }
}
