package com.chamo.chamomarket.controller;

import com.chamo.chamomarket.dto.auth.AuthRequestDTO;
import com.chamo.chamomarket.dto.auth.AuthResponseDTO;
import com.chamo.chamomarket.dto.auth.GenerateTokenRequestDTO;
import com.chamo.chamomarket.dto.auth.RegisterRequestDTO;
import com.chamo.chamomarket.dto.auth.TokenDataDTO;
import com.chamo.chamomarket.dto.auth.TokenResponseDTO;

import com.chamo.chamomarket.service.AuthService;
import com.chamo.chamomarket.service.JwtService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final JwtService jwtService;
    private final AuthService authService;

    @PostMapping("/login")
    public AuthResponseDTO login(@RequestBody @Valid AuthRequestDTO request) {
        return authService.login(request);
    }

    @PostMapping("/generate")
    public TokenResponseDTO generate(@RequestBody GenerateTokenRequestDTO request) {

        String token = jwtService.generateToken(
                request.getEmployeeId(),
                request.getRole(),
                request.getUsername()
        );

        return new TokenResponseDTO(token);
    }

    @PostMapping("/validate")
    public Map<String, Boolean> validate(@RequestBody TokenResponseDTO request) {

        boolean valid = jwtService.isTokenValid(request.getToken());

        Map<String, Boolean> response = new HashMap<>();
        response.put("valid", valid);

        return response;
    }

    @PostMapping("/data")
    public TokenDataDTO data(@RequestBody TokenResponseDTO request) {
        return jwtService.extractTokenData(request.getToken());
    }

    @PostMapping("/refresh")
    public TokenResponseDTO refresh(@RequestBody TokenResponseDTO request) {

        String newToken = jwtService.refreshToken(request.getToken());

        return new TokenResponseDTO(newToken);
    }

    @PostMapping("/register")
    public Map<String, String> register(
            @RequestBody @Valid RegisterRequestDTO request
    ) {

        authService.register(request);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Usuario registrado correctamente");

        return response;
    }
}