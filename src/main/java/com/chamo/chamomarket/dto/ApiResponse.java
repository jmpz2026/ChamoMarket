package com.chamo.chamomarket.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApiResponse<T> {

    private boolean status;
    private String message;
    private T data;
}