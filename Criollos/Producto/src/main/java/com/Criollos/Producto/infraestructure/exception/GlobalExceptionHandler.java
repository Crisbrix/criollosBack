package com.Criollos.Producto.infraestructure.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.converter.HttpMessageNotReadableException;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Pattern DOUBLE_PATTERN = Pattern.compile("\"([^\"]+)\".*Double");
    private static final Pattern INTEGER_PATTERN = Pattern.compile("\"([^\"]+)\".*Integer");
    private static final Pattern BOOLEAN_PATTERN = Pattern.compile("\"([^\"]+)\".*Boolean");

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> handleJsonError(HttpMessageNotReadableException ex) {
        String error = ex.getMessage();
        String mensaje = "Error en el formato de los datos enviados";
        String sugerencia = "Verifica el formato JSON de los campos";
        String valor = "";
        String campo = "";

        if (error.contains("Double")) {
            Matcher matcher = DOUBLE_PATTERN.matcher(error);
            if (matcher.find()) {
                valor = matcher.group(1);
                campo = "precio";
                mensaje = "El campo 'precio' tiene un valor inválido";
                sugerencia = "Debe ser un número sin comillas, por ejemplo: 15000 o 15000.0";
            }
        } else if (error.contains("Integer")) {
            Matcher matcher = INTEGER_PATTERN.matcher(error);
            if (matcher.find()) {
                valor = matcher.group(1);
                campo = "stock o stockMinimo";
                mensaje = "Un campo numérico entero tiene un valor inválido";
                sugerencia = "Debe ser un número entero sin comillas, por ejemplo: 100";
            }
        } else if (error.contains("Boolean")) {
            Matcher matcher = BOOLEAN_PATTERN.matcher(error);
            if (matcher.find()) {
                valor = matcher.group(1);
                campo = "activo";
                mensaje = "El campo 'activo' tiene un valor inválido";
                sugerencia = "Debe ser true o false sin comillas";
            }
        } else if (error.contains("JSON parse error")) {
            mensaje = "JSON mal formado";
            sugerencia = "Verifica que el JSON esté bien estructurado";
        }

        return ResponseEntity.badRequest().body(Map.of(
                "timestamp", LocalDateTime.now(),
                "status", HttpStatus.BAD_REQUEST.value(),
                "error", "Bad Request",
                "mensaje", mensaje,
                "campo", campo,
                "valor_recibido", valor,
                "sugerencia", sugerencia
        ));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleValidationErrors(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(Map.of(
                "timestamp", LocalDateTime.now(),
                "status", HttpStatus.BAD_REQUEST.value(),
                "error", "Validation Error",
                "mensaje", ex.getMessage(),
                "sugerencia", "Verifica los datos enviados"
        ));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<?> handleIllegalState(IllegalStateException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
                "timestamp", LocalDateTime.now(),
                "status", HttpStatus.CONFLICT.value(),
                "error", "Business Rule Error",
                "mensaje", ex.getMessage(),
                "sugerencia", "Verifica el stock disponible del producto"
        ));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationExceptions(MethodArgumentNotValidException ex) {
        StringBuilder errores = new StringBuilder();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errores.append(error.getField()).append(": ").append(error.getDefaultMessage()).append("; ");
        });

        return ResponseEntity.badRequest().body(Map.of(
                "timestamp", LocalDateTime.now(),
                "status", HttpStatus.BAD_REQUEST.value(),
                "error", "Validation Error",
                "mensaje", "Errores de validación",
                "detalles", errores.toString(),
                "sugerencia", "Corrige los campos marcados"
        ));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<?> handleDatabaseErrors(DataIntegrityViolationException ex) {
        String mensaje = "Error de integridad de datos";
        String sugerencia = "Verifica las restricciones de la base de datos";

        if (ex.getMessage() != null) {
            if (ex.getMessage().contains("nombre")) {
                mensaje = "Ya existe un producto con ese nombre";
                sugerencia = "Usa un nombre diferente para el producto";
            } else if (ex.getMessage().contains("cannot be null")) {
                mensaje = "Campo obligatorio faltante";
                sugerencia = "Asegúrate de enviar todos los campos requeridos: nombre, precio, stock, categoria";
            } else if (ex.getMessage().contains("Duplicate entry")) {
                mensaje = "Ya existe un producto con esos datos";
                sugerencia = "Verifica que el nombre sea único";
            } else if (ex.getMessage().contains("categoria_id")) {
                mensaje = "La categoría especificada no existe";
                sugerencia = "Verifica que la categoría sea válida";
            }
        }

        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
                "timestamp", LocalDateTime.now(),
                "status", HttpStatus.CONFLICT.value(),
                "error", "Data Integrity Error",
                "mensaje", mensaje,
                "sugerencia", sugerencia
        ));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<?> handleMissingParameter(MissingServletRequestParameterException ex) {
        return ResponseEntity.badRequest().body(Map.of(
                "timestamp", LocalDateTime.now(),
                "status", HttpStatus.BAD_REQUEST.value(),
                "error", "Missing Parameter",
                "mensaje", "Falta el parámetro requerido: " + ex.getParameterName(),
                "sugerencia", "Agrega el parámetro '" + ex.getParameterName() + "' a la solicitud"
        ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGeneralErrors(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "timestamp", LocalDateTime.now(),
                "status", HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "error", "Internal Server Error",
                "mensaje", "Error interno del servidor",
                "sugerencia", "Intenta más tarde o contacta al administrador"
        ));
    }
}