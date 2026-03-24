package com.chamo.chamomarket.service;

import com.chamo.chamomarket.dto.sale.SaleRequestDTO;
import com.chamo.chamomarket.dto.sale.SaleResponseDTO;
import com.chamo.chamomarket.entity.SaleDetailEntity;
import com.chamo.chamomarket.entity.SaleEntity;
import com.chamo.chamomarket.repository.SaleRepository;
import com.chamo.chamomarket.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;

@Service
public class SaleService {
    @Autowired
    private SaleRepository saleRepository;

    @Autowired
    private ProductRepository productRepository;

    @Transactional
    public SaleResponseDTO createSale(SaleRequestDTO request) {
        SaleEntity sale = new SaleEntity();
        sale.setSaleDate(LocalDateTime.now());
        sale.setDetails(new ArrayList<>());

        double total = 0;

        for (var item : request.getItems()) {
            var product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

            SaleDetailEntity detail = new SaleDetailEntity();
            detail.setProduct(product);
            detail.setQuantity(item.getQuantity());
            detail.setPrice(product.getPrice());
            detail.setSubtotal(product.getPrice() * item.getQuantity());
            detail.setSale(sale);

            sale.getDetails().add(detail);
            total += detail.getSubtotal();
        }

        sale.setTotal(total);
        saleRepository.save(sale);


        return new SaleResponseDTO();
    }
}