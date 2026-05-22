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

    /**
     * Metodo para autentificarse en el sistema
     * @param request
     * @return Token e informacion del usuario con confirmacion de inicio de sesion
     */
    @PostMapping("/login")
    public AuthResponseDTO login(@RequestBody @Valid AuthRequestDTO request) {
        return authService.login(request);
    }

    /**
     * Metodo para generar token manualmente
     * @param request
     * @return Token
     */
    @PostMapping("/generate")
    public TokenResponseDTO generate(@RequestBody GenerateTokenRequestDTO request) {

        String token = jwtService.generateToken(
                request.getEmployeeId(),
                request.getRole(),
                request.getUsername()
        );

        return new TokenResponseDTO(token);
    }

    /**
     * Metodo para validar que el token es valido
     * @param request
     * @return Si el token es valido o no
     */
    @PostMapping("/validate")
    public Map<String, Boolean> validate(@RequestBody TokenResponseDTO request) {

        boolean valid = jwtService.isTokenValid(request.getToken());

        Map<String, Boolean> response = new HashMap<>();
        response.put("valid", valid);

        return response;
    }

    /**
     * Metodo para obtener informacion del token.
     * @param request
     * @return DTO con la informacion del Token
     */
    @PostMapping("/data")
    public TokenDataDTO data(@RequestBody TokenResponseDTO request) {
        return jwtService.extractTokenData(request.getToken());
    }

    /**
     * Metodo para refrescar validez del token
     * @param request
     * @return DTO con el token nuevo
     */
    @PostMapping("/refresh")
    public TokenResponseDTO refresh(@RequestBody TokenResponseDTO request) {

        String newToken = jwtService.refreshToken(request.getToken());

        return new TokenResponseDTO(newToken);
    }

    /**
     * Metodo para registrar al usuario
     * @param request
     * @return Mensaje con que el usuario se ha registrado correctamente
     */
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