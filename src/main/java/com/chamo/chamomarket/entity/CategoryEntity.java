package com.chamo.chamomarket.entity;

import jakarta.persistence.*;
import java.util.ArrayList;

@Entity
public class CategoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Column(name = "status", nullable = false)
    private Boolean status;

    @OneToMany(mappedBy = "category")
    private ArrayList<ProductEntity> products;
}
