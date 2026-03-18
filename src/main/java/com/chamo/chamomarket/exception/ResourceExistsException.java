package com.chamo.chamomarket.exception;

import org.springframework.http.HttpStatus;

public class ResourceExistsException extends ApiException {

    public ResourceExistsException(String message) {
        super(message, HttpStatus.CONFLICT.value());
    }

}
