package com.chamo.chamomarket.filter;

import com.chamo.chamomarket.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtValidationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    private final List<String> PUBLIC_ROUTES = List.of(
            "/auth"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws java.io.IOException {
        String autHeader = request.getHeader("Authorization");

        if (autHeader == null || !autHeader.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Header is missing in the request\"}");
            return;
        }

        String token = autHeader.replace("Bearer ", "");

        try {
            if (jwtService.isTokenValid(token)) {
                String username = jwtService.extractUserName(token);
                Long employeeId = jwtService.extractEmployeeId(token);
                String role = jwtService.extractRole(token);

                request.setAttribute("username", username);
                request.setAttribute("employeeId", employeeId);
                request.setAttribute("role", role);

                // MUY IMPORTANTE
                filterChain.doFilter(request, response);
            } else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\": \"Token is invalid or expired\"}");
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Validation failed\"}");
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return PUBLIC_ROUTES.stream().anyMatch(path::startsWith);
    }
}
