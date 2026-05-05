package com.chamo.chamomarket.service;

import com.chamo.chamomarket.dto.ApiResponse;
import com.chamo.chamomarket.dto.sale.SaleRequestDTO;
import com.chamo.chamomarket.dto.sale.SaleResponseDTO;
import com.chamo.chamomarket.entity.ProductEntity;
import com.chamo.chamomarket.entity.SaleDetailEntity;
import com.chamo.chamomarket.entity.SaleEntity;
import com.chamo.chamomarket.entity.employee.EmployeeEntity;
import com.chamo.chamomarket.exception.ResourceBadRequestException;
import com.chamo.chamomarket.exception.ResourceNoContentException;
import com.chamo.chamomarket.exception.ResourceNotFoundException;
import com.chamo.chamomarket.mapper.SaleMapper;
import com.chamo.chamomarket.repository.EmployeeRepository;
import com.chamo.chamomarket.repository.MessageRepository;
import com.chamo.chamomarket.repository.ProductRepository;
import com.chamo.chamomarket.repository.SaleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class SaleService {

    private static final Double IVA_RATE = Double.valueOf("0.19");

    private final SaleRepository saleRepository;
    private final ProductRepository productRepository;
    private final EmployeeRepository employeeRepository;

    @Transactional
    public ApiResponse<SaleResponseDTO> createSale(SaleRequestDTO request) {
        EmployeeEntity employee = employeeRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new ResourceNotFoundException("NO SE ENCONTRO EL EMPLEADO"));

        SaleEntity sale = new SaleEntity();
        sale.setSaleDate(LocalDateTime.now());
        sale.setEmployee(employee);
        sale.setDetails(new ArrayList<>());

        Double subtotal = Double.valueOf("0.0");

        for (var item : request.getItems()) {
            ProductEntity product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException(MessageRepository.PRODUCT_NOT_FOUND));

            if (Boolean.FALSE.equals(product.getStatus())) {
                throw new ResourceNoContentException(MessageRepository.PRODUCT_NOT_AVAILABLE);
            }

            if (item.getQuantity() > product.getQuantity()) {
                throw new ResourceBadRequestException(MessageRepository.PRODUCT_NOT_ENOUGH);
            }

            Double lineSubtotal = Double.valueOf(
                    item.getUnitPrice().doubleValue() * item.getQuantity().doubleValue()
            );

            SaleDetailEntity detail = new SaleDetailEntity();
            detail.setSale(sale);
            detail.setProduct(product);
            detail.setQuantity(item.getQuantity());
            detail.setPrice(item.getUnitPrice());
            detail.setSubtotal(lineSubtotal);

            sale.getDetails().add(detail);
            subtotal += lineSubtotal;

            Integer newQuantity = Integer.valueOf(
                    product.getQuantity().intValue() - item.getQuantity().intValue()
            );

            product.setQuantity(newQuantity);
            productRepository.save(product);
        }

        Double iva = Double.valueOf(subtotal.doubleValue() * IVA_RATE.doubleValue());
        Double total = Double.valueOf(subtotal.doubleValue() + iva.doubleValue());

        sale.setSubtotal(subtotal);
        sale.setIva(iva);
        sale.setTotal(total);

        saleRepository.save(sale);

        ApiResponse<SaleResponseDTO> response = new ApiResponse<>();
        response.setSuccess(true);
        response.setMessage("VENTA REGISTRADA EXITOSAMENTE");
        response.setData(SaleMapper.toResponseDTO(sale));
        return response;
    }
}