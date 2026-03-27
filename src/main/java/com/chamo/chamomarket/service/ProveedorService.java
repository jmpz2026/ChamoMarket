package com.chamo.chamomarket.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.chamo.chamomarket.entity.Proveedor;
import com.chamo.chamomarket.entity.ProductEntity;
import com.chamo.chamomarket.repository.ProveedorRepository;
import com.chamo.chamomarket.repository.ProductRepository;

@Service
public class ProveedorService {

    @Autowired
    private ProveedorRepository proveedorRepository;

    @Autowired
    private ProductRepository productRepository;

    public Proveedor guardar(Proveedor proveedor) {
        if (proveedorRepository.existsByNit(proveedor.getNit())) {
            throw new RuntimeException("Ya existe un proveedor con ese NIT");
        }
        return proveedorRepository.save(proveedor);
    }

    public String entradaStock(Long productoId, Long proveedorId, int cantidad) {
        ProductEntity producto = productRepository.findById(productoId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        Proveedor proveedor = proveedorRepository.findById(proveedorId)
                .orElseThrow(() -> new RuntimeException("Proveedor no encontrado"));

        // Vincular proveedor al producto si no está ya vinculado
        if (!producto.getProveedores().contains(proveedor)) {
            producto.getProveedores().add(proveedor);
        }

    return "Stock actualizado correctamente";
}
    public Proveedor obtenerPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Proveedor no encontrado"));
    }
}