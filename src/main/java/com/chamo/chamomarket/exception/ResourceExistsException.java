package com.chamo.chamomarket.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class ResourceExistsException extends ApiException {
    public ResourceExistsException(String message) {
        super(message, HttpStatus.CONFLICT.value());
    }
}