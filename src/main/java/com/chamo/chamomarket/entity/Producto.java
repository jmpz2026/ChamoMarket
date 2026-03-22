package com.chamo.chamomarket.entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

    private int stock;

    @ManyToMany(mappedBy = "productos")
    private List<Proveedor> proveedores;

    // getters y setters
    public Long getId() { return id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }

    public List<Proveedor> getProveedores() { return proveedores; }
    public void setProveedores(List<Proveedor> proveedores) { this.proveedores = proveedores; }
}