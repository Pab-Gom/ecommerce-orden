package com.ecommerce.orden_service.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // ****ERROR ORDEN NO ENCONTRADA
    @ExceptionHandler(OrdenNoEncontradaException.class)
    public ResponseEntity<ErrorResponse> manejarOrdenNoEncontrada(OrdenNoEncontradaException ex){

        ErrorResponse error = new ErrorResponse(
            ex.getMessage(),
            HttpStatus.NOT_FOUND.value(),
            LocalDateTime.now()
        );
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    // ****ERROR GENERAL
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> manejarErrorGeneral(RuntimeException ex) {

        ErrorResponse error = new ErrorResponse(
                ex.getMessage(),
                HttpStatus.BAD_REQUEST.value(),
                LocalDateTime.now()
        );

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
}