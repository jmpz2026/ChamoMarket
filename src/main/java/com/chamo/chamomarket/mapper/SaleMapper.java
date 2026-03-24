package com.chamo.chamomarket.mapper;

import com.chamo.chamomarket.dto.sale.SaleResponseDTO;
import com.chamo.chamomarket.dto.sale.SaleDetailDTO;
import com.chamo.chamomarket.entity.SaleEntity;
import com.chamo.chamomarket.entity.SaleDetailEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SaleMapper {

    SaleResponseDTO toSaleResponseDTO(SaleEntity saleEntity);

    List<SaleResponseDTO> toSalesResponseDTO(List<SaleEntity> salesEntity);

    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "productName", source = "product.name")
    SaleDetailDTO toSaleDetailDTO(SaleDetailEntity saleDetailEntity);
}