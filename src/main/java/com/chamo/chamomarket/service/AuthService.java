package com.chamo.chamomarket.service;

import com.chamo.chamomarket.dto.auth.AuthRequestDTO;
import com.chamo.chamomarket.dto.auth.AuthResponseDTO;
import com.chamo.chamomarket.dto.auth.RegisterRequestDTO;

import com.chamo.chamomarket.entity.employee.EmployeeRole;
import com.chamo.chamomarket.entity.employee.EmployeeEntity;
import com.chamo.chamomarket.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Value("${jwt.expiration-ms}")
    private Long expirationMs;

    public AuthResponseDTO login(AuthRequestDTO request) {

        EmployeeEntity employee = employeeRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Credenciales inválidas"));

        boolean passwordOk = passwordEncoder.matches(request.getPassword(), employee.getPassword());
        if (!passwordOk) {
            throw new RuntimeException("Credenciales inválidas");
        }

        String token = jwtService.generateToken(
                employee.getId(),
                employee.getRole().name(),
                employee.getUsername()
        );

        Long expiration = System.currentTimeMillis() + expirationMs;

        return new AuthResponseDTO(token, employee.getRole().name(), expiration);
    }

    public void register(RegisterRequestDTO request) {

        if (employeeRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("El username ya existe");
        }

        EmployeeEntity employee = new EmployeeEntity();

        employee.setDocument(request.getDocument());
        employee.setName(request.getName());
        employee.setRole(EmployeeRole.valueOf(request.getRole()));
        employee.setSalary(request.getSalary());
        employee.setHireDate(java.time.LocalDate.now());

        employee.setUsername(request.getUsername());

        // BCrypt
        employee.setPassword(passwordEncoder.encode(request.getPassword()));

        employeeRepository.save(employee);
    }
}