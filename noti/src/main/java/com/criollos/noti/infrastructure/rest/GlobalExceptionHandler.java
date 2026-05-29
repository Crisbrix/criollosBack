package com.criollos.noti.infrastructure.rest;

import com.criollos.noti.domain.exception.NotificationException;
import com.criollos.noti.domain.exception.NotificationValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotificationValidationException.class)
    public ResponseEntity<ApiErrorResponse> handleValidation(NotificationValidationException exception) {
        return ResponseEntity.badRequest().body(new ApiErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                exception.getMessage(),
                Instant.now()
        ));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrorResponse> handleMalformedBody(HttpMessageNotReadableException exception) {
        return ResponseEntity.badRequest().body(new ApiErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "El cuerpo de la peticion no tiene un formato valido.",
                Instant.now()
        ));
    }

    @ExceptionHandler(NotificationException.class)
    public ResponseEntity<ApiErrorResponse> handleNotification(NotificationException exception) {
        String msg = exception.getMessage();
        if (exception.getCause() != null) {
            msg += " | " + exception.getCause().getMessage();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                msg,
                Instant.now()
        ));
    }

    public record ApiErrorResponse(
            int status,
            String message,
            Instant timestamp
    ) {
    }
}
