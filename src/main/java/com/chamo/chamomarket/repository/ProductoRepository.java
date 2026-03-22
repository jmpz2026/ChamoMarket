package com.chamo.chamomarket.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.chamo.chamomarket.entity.Producto;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
}