package com.chamo.chamomarket.config;

import com.chamo.chamomarket.filter.JwtValidationFilter;
import com.chamo.chamomarket.filter.RoleValidationFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration // Fábrica de beans
public class FilterConfig {

    @Bean
    FilterRegistrationBean<JwtValidationFilter> jwtFilter(JwtValidationFilter jwtValidationFilter) {

        // Creamos un contenedor de registro del bean para el filtro
        FilterRegistrationBean<JwtValidationFilter> registrationBean = new FilterRegistrationBean<>();

        // Es decirle a spring que este es el filtro con el que quiero que trabaje
        registrationBean.setFilter(jwtValidationFilter);

        // Definir el alcance de este filtro, quiero que revise todas las peticiones que entre a mi aplicacion
        registrationBean.addUrlPatterns("/*");

        // Definimos el orden de prioridad de ejecución de este bean
        registrationBean.setOrder(1);

        // Retornmos el registro configurado para que spring lo guarde en su contexto
        return registrationBean;
    }

    @Bean
    FilterRegistrationBean<RoleValidationFilter> roleFilter(RoleValidationFilter roleValidationFilter) {

        // Creamos un contenedor de registro del bean para el filtro
        FilterRegistrationBean<RoleValidationFilter> registrationBean = new FilterRegistrationBean<>();

        // Es decirle a spring que este es el filtro con el que quiero que trabaje
        registrationBean.setFilter(roleValidationFilter);

        // Definir el alcance de este filtro, quiero que revise todas las peticiones que entre a mi aplicacion
        registrationBean.addUrlPatterns("/*");

        // Definimos el orden de prioridad de ejecución de este bean
        registrationBean.setOrder(2);

        // Retornmos el registro configurado para que spring lo guarde en su contexto
        return registrationBean;
    }
}