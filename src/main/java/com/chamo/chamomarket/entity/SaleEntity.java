package com.chamo.chamomarket.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "sales")
@Getter
@Setter
public class SaleEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "sale_date")
    private LocalDateTime saleDate;

    private Double total;

    @OneToMany(mappedBy = "sale", cascade = CascadeType.ALL)
    private List<SaleDetailEntity> details;
}