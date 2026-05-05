package com.chamo.chamomarket.mapper;

import com.chamo.chamomarket.dto.sale.SaleDetailDTO;
import com.chamo.chamomarket.dto.sale.SaleResponseDTO;
import com.chamo.chamomarket.entity.SaleEntity;

import java.util.stream.Collectors;

public class SaleMapper {

    private SaleMapper() {
    }

    public static SaleResponseDTO toResponseDTO(SaleEntity sale) {
        SaleResponseDTO dto = new SaleResponseDTO();
        dto.setId(sale.getId());
        dto.setSaleDate(sale.getSaleDate());
        dto.setEmployeeId(sale.getEmployee().getId());
        dto.setSubtotal(sale.getSubtotal());
        dto.setIva(sale.getIva());
        dto.setTotal(sale.getTotal());

        dto.setDetails(
                sale.getDetails().stream().map(detail -> {
                    SaleDetailDTO item = new SaleDetailDTO();
                    item.setProductId(detail.getProduct().getId());
                    item.setProductName(detail.getProduct().getName());
                    item.setQuantity(detail.getQuantity());
                    item.setPrice(detail.getPrice());
                    item.setSubtotal(detail.getSubtotal());
                    return item;
                }).collect(Collectors.toList())
        );

        return dto;
    }
}