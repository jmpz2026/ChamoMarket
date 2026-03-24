package com.chamo.chamomarket.entity;
import com.chamo.chamomarket.entity.ProductEntity;

import jakarta.persistence.*;
import java.util.List;

@Entity
public class Proveedor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String nit;

    private String nombre;

    
    @ManyToMany
    private List<ProductEntity> productos;
    
    public Long getId() { return id; }

    public String getNit() { return nit; }
    public void setNit(String nit) { this.nit = nit; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public List<ProductEntity> getProductos() { return productos; }
    public void setProductos(List<ProductEntity> productos) { this.productos = productos; }
}
    

