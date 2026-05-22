package com.chamo.chamomarket.entity.employee;

import lombok.Getter;

@Getter
public enum EmployeeRole {
    ADMINISTRADOR(1L),
    CAJERO(2L);

    private final long id;

    EmployeeRole(long id) {
        this.id = id;
    }
}v