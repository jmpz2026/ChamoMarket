package com.chamo.chamomarket.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.chamo.chamomarket.entity.Producto;
import com.chamo.chamomarket.repository.ProductoRepository;

@RestController
@RequestMapping("/productos")
public class ProductoController {

    @Autowired
    private ProductoRepository repository;

    @PostMapping
    public Producto crear(@RequestBody Producto producto) {
        return repository.save(producto);
    }
}