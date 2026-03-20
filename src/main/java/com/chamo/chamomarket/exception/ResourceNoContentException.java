package com.chamo.chamomarket.exception;

import org.springframework.http.HttpStatus;

public class ResourceNoContentException extends ApiException {

    public ResourceNoContentException(String message) {
        super(message, HttpStatus.NO_CONTENT.value());
    }
}
