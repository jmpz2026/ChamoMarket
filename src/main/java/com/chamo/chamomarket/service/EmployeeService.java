package com.chamo.chamomarket.service;

import com.chamo.chamomarket.dto.EmployeeRequestDTO;
import com.chamo.chamomarket.dto.EmployeeResponseDTO;
import com.chamo.chamomarket.entity.EmployeeEntity;
import com.chamo.chamomarket.entity.EmployeeRole;
import com.chamo.chamomarket.exception.ResourceExistsException;
import com.chamo.chamomarket.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;


    public EmployeeResponseDTO createEmployee(EmployeeRequestDTO request) {

        if (employeeRepository.existsByDocument(request.getDocument())) {
            throw new ResourceExistsException("EL EMPLEADO CON ESTA CEDULA YA EXISTE");
        }

        EmployeeEntity employee = new EmployeeEntity();
        employee.setDocument(request.getDocument());
        employee.setName(request.getName());


        try {
            employee.setRole(EmployeeRole.valueOf(request.getRole().toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("CARGO NO VALIDO: Solo ADMINISTRADOR, CAJERO o AUXILIAR");
        }

        employee.setHireDate(request.getHireDate());
        employee.setSalary(request.getSalary());

        employeeRepository.save(employee);
        return mapToDTO(employee);
    }


    public List<EmployeeResponseDTO> findEmployees(String role, LocalDate start, LocalDate end) {
        List<EmployeeEntity> employees;

        if (role != null) {
            employees = employeeRepository.findByRole(EmployeeRole.valueOf(role.toUpperCase()));
        } else if (start != null && end != null) {
            employees = employeeRepository.findByHireDateBetween(start, end);
        } else {
            employees = employeeRepository.findAll();
        }

        return employees.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    private EmployeeResponseDTO mapToDTO(EmployeeEntity entity) {
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