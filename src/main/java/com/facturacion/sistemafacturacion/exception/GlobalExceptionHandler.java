package com.facturacion.sistemafacturacion.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Date;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorDetalles> manejarResourceNotFoundException
            (ResourceNotFoundException ex, WebRequest request) {
        ErrorDetalles errorDetalles = new ErrorDetalles(
                new Date(),
                ex.getMessage(),
                request.getDescription(false)
        );
        return new ResponseEntity<>(errorDetalles, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDetalles> manejarGlobalException(Exception ex, WebRequest
            request) {
        ErrorDetalles errorDetalles = new ErrorDetalles(
                new Date(),
                ex.getMessage(),
                request.getDescription(false)
        );
        return new ResponseEntity<>(errorDetalles, HttpStatus.INTERNAL_SERVER_ERROR);
    }


    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorDetalles> manejarRuntimeException(RuntimeException ex, WebRequest
            request) {
        ErrorDetalles errorDetalles = new ErrorDetalles(
                new Date(),
                ex.getMessage(),
                request.getDescription(false)
        );

        return new ResponseEntity<>(errorDetalles, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorDetalles> manejarDataIntegrityViolationException(
            DataIntegrityViolationException ex,
            WebRequest request) {

        String mensaje = "Error de integridad de datos.";

        if (ex.getMessage() != null && ex.getMessage().contains("clientes_ruc_cedula_key")) {
            mensaje = "El RUC/Cédula ya está registrado.";
        }

        ErrorDetalles errorDetalles = new ErrorDetalles(
                new Date(),
                mensaje,
                request.getDescription(false)
        );

        return new ResponseEntity<>(errorDetalles, HttpStatus.CONFLICT); // 409
    }

}
