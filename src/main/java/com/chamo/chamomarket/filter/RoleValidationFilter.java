package com.chamo.chamomarket.filter;

import com.chamo.chamomarket.entity.employee.EmployeeRole;
import com.chamo.chamomarket.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class RoleValidationFilter extends OncePerRequestFilter {

    private static final Map<String, List<EmployeeRole>> ROUTE_ROLES = Map.of(
            "/category", List.of(EmployeeRole.ADMINISTRADOR),
            "/product", List.of(EmployeeRole.ADMINISTRADOR, EmployeeRole.CAJERO),
            "/proveedores", List.of(EmployeeRole.ADMINISTRADOR),
            "/employee", List.of(EmployeeRole.ADMINISTRADOR),
            "/sales", List.of(EmployeeRole.ADMINISTRADOR, EmployeeRole.CAJERO)
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws java.io.IOException {

        boolean allowed = false;

        try {
            String path = request.getRequestURI();
            String role = request.getAttribute("role").toString();
            logger.info("Role: " + role);

            if (role == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\": \"ROLE IS NOT FOUND\"}");
                return;
            }

            allowed = ROUTE_ROLES.entrySet()
                    .stream()
                    .filter(entry -> path.startsWith(entry.getKey()))
                    // Esto es para que solo encuentre un valor y retorne un solo booleano
                    .findFirst()
                    .map(entry -> entry.getValue().contains(EmployeeRole.valueOf(role)))
                    .orElse(false);

            if (!allowed) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\": \"ROLE IS NOT AUTHORIZED\"}");
                return;
            }

            filterChain.doFilter(request, response);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Validation failed\"}");
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return PublicRoutes.PUBLIC_ROUTES.stream().anyMatch(path::startsWith);
    }
}
