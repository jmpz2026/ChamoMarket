package com.chamo.chamomarket.controller;

import com.chamo.chamomarket.dto.employee.EmployeeRequestDTO;
import com.chamo.chamomarket.dto.employee.EmployeeResponseDTO;
import com.chamo.chamomarket.service.EmployeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/employee")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    /**
     * Metodo para guardar un usuario
     * @param request (EmployeeRequestDTO)
     * @return DTO, con mensaje que el empleado se guardo exitosamente
     */
    @PostMapping
    public ResponseEntity<EmployeeResponseDTO> save(@RequestBody @Valid EmployeeRequestDTO request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(employeeService.createEmployee(request));
    }

    /**
     * Metodo para buscar un archivo por los siguientes parametros:
     * @param role
     * @param start
     * @param end
     * @return DTO, con la informacion dle empleado
     */
    @GetMapping("/search")
    public ResponseEntity<List<EmployeeResponseDTO>> search(
            @RequestParam(required = false) String role,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {

        return ResponseEntity
                .ok(employeeService.findEmployees(role, start, end));
    }
}