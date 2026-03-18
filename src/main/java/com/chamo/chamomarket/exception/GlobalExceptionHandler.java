package com.chamo.chamomarket.exception;

import com.chamo.chamomarket.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Este es el manejador global de las apis
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiResponse<?>> handleApiException(ApiException exception) {

        ApiResponse<Object> response = new ApiResponse<>();
        response.setSuccess(false);
        response.setMessage(exception.getMessage());
        response.setData(null);

        return ResponseEntity
                .status(exception.getStatus())
                .body(response);
    }

    // Esto maneja las excepciones cuando las excepciones del @Valid se ejecutan
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<?>> handleValidationErrors(MethodArgumentNotValidException exception) {

        List<String> errors = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .toList();

        ApiResponse<Object> response = new ApiResponse<>();
        response.setSuccess(false);
        response.setMessage(String.valueOf(errors));
        response.setData(exception.getFieldErrors());

        return ResponseEntity
                .status(exception.getStatusCode())
                .body(response);
    }
}
