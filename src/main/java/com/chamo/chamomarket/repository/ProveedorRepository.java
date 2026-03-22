package com.chamo.chamomarket.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.chamo.chamomarket.entity.Proveedor;

public interface ProveedorRepository extends JpaRepository<Proveedor, Long> {
    boolean existsByNit(String nit);
}