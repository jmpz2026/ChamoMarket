package com.chamo.chamomarket.service;

import com.chamo.chamomarket.dto.employee.EmployeeRequestDTO;
import com.chamo.chamomarket.dto.employee.EmployeeResponseDTO;
import com.chamo.chamomarket.entity.employee.EmployeeEntity;
import com.chamo.chamomarket.entity.employee.EmployeeRole;
import com.chamo.chamomarket.exception.ResourceConflictException;
import com.chamo.chamomarket.exception.ResourceExistsException;
import com.chamo.chamomarket.mapper.EmployeeMapper;
import com.chamo.chamomarket.repository.EmployeeRepository;
import com.chamo.chamomarket.repository.MessageRepository;
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
            throw new ResourceExistsException(MessageRepository.EMPLOYEE_EXISTS);
        }

        EmployeeEntity employee = new EmployeeEntity();
        employee.setDocument(request.getDocument());
        employee.setName(request.getName());


        try {
            employee.setRole(EmployeeRole.valueOf(request.getRole().toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw new ResourceConflictException(MessageRepository.EMPLOYEE_INVALID_ROLE);
        }

        employee.setHireDate(request.getHireDate());
        employee.setSalary(request.getSalary());

        employeeRepository.save(employee);
        return EmployeeMapper.mapToDTO(employee);
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

        return employees.stream().map(EmployeeMapper::mapToDTO).collect(Collectors.toList());
    }
 }