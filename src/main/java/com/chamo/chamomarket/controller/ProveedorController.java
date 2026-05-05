package com.chamo.chamomarket.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.chamo.chamomarket.entity.Proveedor;
import com.chamo.chamomarket.service.ProveedorService;

@RestController
@RequestMapping("/proveedores")
public class ProveedorController {

    @Autowired
    private ProveedorService service;

    @PostMapping
    public Proveedor crear(@RequestBody Proveedor proveedor) {
        return service.guardar(proveedor);
    }

    @PostMapping("/entrada")
    public String entradaStock(@RequestParam Long productoId,
                          @RequestParam Long proveedorId,
                          @RequestParam int cantidad) {

    return service.entradaStock(productoId, proveedorId, cantidad);
}
    @GetMapping("/{id}")
    public Proveedor obtenerPorId(@PathVariable Long id) {
        return service.obtenerPorId(id);
    }
    
}