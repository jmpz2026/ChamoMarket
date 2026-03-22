package com.chamo.chamomarket.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.chamo.chamomarket.entity.Proveedor;
import com.chamo.chamomarket.repository.ProveedorRepository;
import com.chamo.chamomarket.repository.ProductoRepository;
import com.chamo.chamomarket.entity.Producto;



@Service
public class ProveedorService {

    @Autowired
    private ProveedorRepository repository;
    @Autowired
    private ProductoRepository productoRepository;

    public Proveedor guardar(Proveedor proveedor) {
        if (repository.existsByNit(proveedor.getNit())) {
            throw new RuntimeException("El NIT ya existe");
        }
        return repository.save(proveedor);
    }
    public String entradaStock(Long productoId, Long proveedorId, int cantidad) {

    Producto producto = productoRepository.findById(productoId)
            .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

    producto.setStock(producto.getStock() + cantidad);

    productoRepository.save(producto);

    return "Stock actualizado correctamente";
}
}