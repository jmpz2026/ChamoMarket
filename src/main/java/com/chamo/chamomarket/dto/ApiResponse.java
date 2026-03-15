package com.chamo.chamomarket.dto;

public class ApiResponse<T> {

    private boolean status;
    private String message;
    private T data;
}