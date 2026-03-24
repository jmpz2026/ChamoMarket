package com.chamo.chamomarket.dto.sale;

import lombok.Data;
import java.util.List;

@Data
public class SaleRequestDTO {
    private List<SaleItemDTO> items;
}