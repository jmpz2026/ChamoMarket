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
    private ProductService productService;
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

    repository.findById(proveedorId)
        .orElseThrow(() -> new RuntimeException("Proveedor no encontrado"));

    productService.actualizarStock(productoId, cantidad);

    return "Stock actualizado correctamente";
}
    public Proveedor obtenerPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Proveedor no encontrado"));
    }
}