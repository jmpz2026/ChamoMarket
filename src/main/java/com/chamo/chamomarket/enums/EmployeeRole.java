package com.chamo.chamomarket.enums;


import lombok.Getter;

/**
 * El manejo de los Roles
 */
@Getter
public enum EmployeeRole {
    ADMINISTRADOR(1L),
    CAJERO(2L);

    private final long id;

    EmployeeRole(long id) {
        this.id = id;
    }

}