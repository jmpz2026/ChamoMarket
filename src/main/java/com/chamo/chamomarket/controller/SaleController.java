package com.chamo.chamomarket.controller;

import com.chamo.chamomarket.dto.ApiResponse;
import com.chamo.chamomarket.dto.sale.SaleRequestDTO;
import com.chamo.chamomarket.dto.sale.SaleResponseDTO;
import com.chamo.chamomarket.service.SaleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/sales")
@RequiredArgsConstructor
public class SaleController {

    private final SaleService saleService;

    @PostMapping
    public ResponseEntity<ApiResponse<SaleResponseDTO>> create(@RequestBody @Valid SaleRequestDTO request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(saleService.createSale(request));
    }
}