package com.chamo.chamomarket.controller;

import com.chamo.chamomarket.enums.EmployeeRole;
import com.chamo.chamomarket.security.RequiresRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.chamo.chamomarket.entity.Proveedor;
import com.chamo.chamomarket.service.ProveedorService;

@RestController
@RequestMapping("/proveedores")
public class ProveedorController {

    @Autowired
    private ProveedorService service;

    @RequiresRole({EmployeeRole.ADMINISTRADOR})
    @PostMapping
    public Proveedor crear(@RequestBody Proveedor proveedor) {
        return service.guardar(proveedor);
    }

    @RequiresRole({EmployeeRole.ADMINISTRADOR})
    @PostMapping("/entrada")
    public String entradaStock(@RequestParam Long productoId,
                          @RequestParam Long proveedorId,
                          @RequestParam int cantidad) {
        return service.entradaStock(productoId, proveedorId, cantidad);
    }

    @RequiresRole({EmployeeRole.ADMINISTRADOR})
    @GetMapping("/{id}")
    public Proveedor obtenerPorId(@PathVariable Long id) {
        return service.obtenerPorId(id);
    }
}