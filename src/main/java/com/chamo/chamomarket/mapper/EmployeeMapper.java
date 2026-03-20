package com.chamo.chamomarket.mapper;

import com.chamo.chamomarket.dto.employee.EmployeeResponseDTO;
import com.chamo.chamomarket.entity.employee.EmployeeEntity;

public class EmployeeMapper {

    public static EmployeeResponseDTO mapToDTO(EmployeeEntity entity) {
        EmployeeResponseDTO dto = new EmployeeResponseDTO();
        dto.setId(entity.getId());
        dto.setDocument(entity.getDocument());
        dto.setName(entity.getName());
        dto.setRole(entity.getRole().name());
        dto.setHireDate(entity.getHireDate());
        dto.setSalary(entity.getSalary());
        return dto;
    }

}
