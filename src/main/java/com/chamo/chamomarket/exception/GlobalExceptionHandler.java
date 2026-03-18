package com.chamo.chamomarket.exception;

import com.chamo.chamomarket.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<?>> handleApiException(ApiException exception) {

        ApiResponse<Object> response = new ApiResponse<>();
        response.setSuccess(false);
        response.setMessage(exception.getMessage());
        response.setData(null);

        return ResponseEntity
                .status(exception.getStatus())
                .body(response);
    }
}
