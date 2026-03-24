package com.chamo.chamomarket.controller;

import com.chamo.chamomarket.dto.sale.SaleRequestDTO;
import com.chamo.chamomarket.dto.sale.SaleResponseDTO;
import com.chamo.chamomarket.service.SaleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/sales")
public class SaleController {
    @Autowired
    private SaleService saleService;

    @PostMapping
    public ResponseEntity<SaleResponseDTO> create(@RequestBody SaleRequestDTO request) {
        return ResponseEntity.ok(saleService.createSale(request));
    }
}