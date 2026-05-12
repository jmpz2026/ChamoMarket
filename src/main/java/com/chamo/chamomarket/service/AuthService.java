package com.chamo.chamomarket.service;

import com.chamo.chamomarket.dto.auth.AuthRequestDTO;
import com.chamo.chamomarket.dto.auth.AuthResponseDTO;
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
}