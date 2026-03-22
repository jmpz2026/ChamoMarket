package com.chamo.chamomarket.entity;


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

    // Relación con productos (la conectamos luego)
    @ManyToMany
    private List<Producto> productos;

    // getters y setters
    public Long getId() { return id; }

    public String getNit() { return nit; }
    public void setNit(String nit) { this.nit = nit; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public List<Producto> getProductos() { return productos; }
    public void setProductos(List<Producto> productos) { this.productos = productos; }
}
    

