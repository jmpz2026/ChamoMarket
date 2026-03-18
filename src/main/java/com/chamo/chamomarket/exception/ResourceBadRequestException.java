package com.chamo.chamomarket.exception;

import org.springframework.http.HttpStatus;

public class ResourceBadRequestException extends ApiException {

    public ResourceBadRequestException(String message) {
        super(message, HttpStatus.BAD_REQUEST.value());
    }
}
