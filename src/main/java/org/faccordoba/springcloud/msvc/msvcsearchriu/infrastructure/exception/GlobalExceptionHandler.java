package org.faccordoba.springcloud.msvc.msvcsearchriu.infrastructure.exception;

import org.faccordoba.springcloud.msvc.msvcsearchriu.domain.exception.SearchDomainException;
import org.faccordoba.springcloud.msvc.msvcsearchriu.domain.exception.SearchNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Maneja errores de validación de Bean Validation (@Valid)
     * HTTP 400 - Bad Request
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            WebRequest request) {
        LOGGER.warn("Error de validación en request: {}", ex.getMessage());
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        ValidationErrorResponse error = new ValidationErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Validation Failed",
                "Los datos enviados no son válidos",
                request.getDescription(false).replace("uri=", ""),
                errors
        );
        return ResponseEntity.badRequest().body(error);
    }


    /**
     * Maneja excepciones de dominio (Negocio)
     * HTTP 400 - Bad Request
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(
            IllegalArgumentException ex,
            WebRequest request) {

        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                ex.getMessage(),
                request.getDescription(false).replace("uri=", "")
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }


    /**
     * Maneja excepciones de sesión no encontrada (Negocio)
     * HTTP 404 - Not Found
     */
    @ExceptionHandler({
            org.faccordoba.springcloud.msvc.msvcsearchriu.domain.exception.SearchNotFoundException.class,
            org.faccordoba.springcloud.msvc.msvcsearchriu.application.exception.SearchNotFoundException.class
    })    public ResponseEntity<ErrorResponse> handleSearchNotFound(RuntimeException ex) {
        LOGGER.warn("Search Not Found: {}", ex.getMessage());
        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                "Not Found",
                ex.getMessage(),
                null);

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    /**
     * Maneja excepciones genéricas de infraestructura (DB, Redis, Kafka)
     * HTTP 500 - Internal Server Error
     */
    @ExceptionHandler(SearchDomainException.class)
    public ResponseEntity<ErrorResponse> handleDomainError(SearchDomainException ex) {
        LOGGER.error("Error to infra traslate to domain: {}", ex.getMessage(), ex);
        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                ex.getMessage(),null);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    /**
     * Manejo de excepciones no controladas (Falls back)
     * HTTP 500 - Internal Server Error
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalError(Exception ex) {
        LOGGER.error("unexpected error: ", ex);
        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "unexpected error",
                ex.getMessage(),null);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

}
