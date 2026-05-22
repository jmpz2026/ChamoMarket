package com.chamo.chamomarket.security;

import com.chamo.chamomarket.constants.MessageJsonConstants;
import com.chamo.chamomarket.dto.ApiResponse;
import com.chamo.chamomarket.enums.EmployeeRole;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Arrays;

@Component
public class RequiresRoleInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        // Si la peticion que se hace a la API no es un metodo, que pase
        if (!(handler instanceof HandlerMethod method)) {
            return true;
        }

        RequiresRole annotation = method.getMethodAnnotation(RequiresRole.class);

        if (annotation == null) annotation = method.getBeanType().getAnnotation(RequiresRole.class);

        if (annotation == null) return true;

        Object rol = request.getAttribute("rolId");

        if (!(rol instanceof Long rolId)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write(MessageJsonConstants.ROLE_NOT_FOUND);
            return false;
        }

        boolean hasRole = Arrays.stream(annotation.value()).anyMatch(employeeRole -> employeeRole.getId() == rolId);

        if (!hasRole) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write(MessageJsonConstants.ROLE_UNAUTHORIZED);
            return false;
        }

        return true;
    }
}
