package com.Criollos.Producto.infraestructure.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.mock.http.MockHttpInputMessage;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;

import java.lang.reflect.Method;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }


    @Test
    void handleJsonError_precioInvalido() {

        HttpMessageNotReadableException ex =
                new HttpMessageNotReadableException(
                        "\"abc\" invalid Double",
                        new MockHttpInputMessage(new byte[0])
                );

        ResponseEntity<?> response = handler.handleJsonError(ex);

        Map<?, ?> body = (Map<?, ?>) response.getBody();

        assertEquals(400, response.getStatusCode().value());
        assertNotNull(body);

        assertEquals("Bad Request", body.get("error"));
        assertEquals("El campo 'precio' tiene un valor inválido",
                body.get("mensaje"));
        assertEquals("precio", body.get("campo"));
        assertEquals("abc", body.get("valor_recibido"));
    }

    @Test
    void handleJsonError_stockInvalido() {

        HttpMessageNotReadableException ex =
                new HttpMessageNotReadableException(
                        "\"abc\" invalid Integer",
                        new MockHttpInputMessage(new byte[0])
                );

        ResponseEntity<?> response = handler.handleJsonError(ex);

        Map<?, ?> body = (Map<?, ?>) response.getBody();

        assertEquals(400, response.getStatusCode().value());
        assertNotNull(body);

        assertEquals("Un campo numérico entero tiene un valor inválido",
                body.get("mensaje"));
        assertEquals("stock o stockMinimo", body.get("campo"));
        assertEquals("abc", body.get("valor_recibido"));
    }

    @Test
    void handleJsonError_activoInvalido() {

        HttpMessageNotReadableException ex =
                new HttpMessageNotReadableException(
                        "\"truee\" invalid Boolean",
                        new MockHttpInputMessage(new byte[0])
                );

        ResponseEntity<?> response = handler.handleJsonError(ex);

        Map<?, ?> body = (Map<?, ?>) response.getBody();

        assertEquals(400, response.getStatusCode().value());
        assertNotNull(body);

        assertEquals("El campo 'activo' tiene un valor inválido",
                body.get("mensaje"));
        assertEquals("activo", body.get("campo"));
        assertEquals("truee", body.get("valor_recibido"));
    }

    @Test
    void handleJsonError_jsonMalFormado() {

        HttpMessageNotReadableException ex =
                new HttpMessageNotReadableException(
                        "JSON parse error",
                        new MockHttpInputMessage(new byte[0])
                );

        ResponseEntity<?> response = handler.handleJsonError(ex);

        Map<?, ?> body = (Map<?, ?>) response.getBody();

        assertEquals(400, response.getStatusCode().value());

        assertEquals("JSON mal formado",
                body.get("mensaje"));
    }

    @Test
    void handleJsonError_errorGenerico() {

        HttpMessageNotReadableException ex =
                new HttpMessageNotReadableException(
                        "Error desconocido",
                        new MockHttpInputMessage(new byte[0])
                );

        ResponseEntity<?> response = handler.handleJsonError(ex);

        Map<?, ?> body = (Map<?, ?>) response.getBody();

        assertEquals(400, response.getStatusCode().value());

        assertEquals("Error en el formato de los datos enviados",
                body.get("mensaje"));
    }


    @Test
    void handleValidationErrors_retornaMensaje() {

        ResponseEntity<?> response =
                handler.handleValidationErrors(
                        new IllegalArgumentException(
                                "El nombre es obligatorio"
                        )
                );

        Map<?, ?> body = (Map<?, ?>) response.getBody();

        assertEquals(400, response.getStatusCode().value());

        assertEquals("Validation Error",
                body.get("error"));

        assertEquals("El nombre es obligatorio",
                body.get("mensaje"));
    }


    @Test
    void handleIllegalState_retornaMensaje() {

        ResponseEntity<?> response =
                handler.handleIllegalState(
                        new IllegalStateException(
                                "Stock insuficiente"
                        )
                );

        Map<?, ?> body = (Map<?, ?>) response.getBody();

        assertEquals(409, response.getStatusCode().value());

        assertEquals("Business Rule Error",
                body.get("error"));

        assertEquals("Stock insuficiente",
                body.get("mensaje"));
    }


    @Test
    void handleDatabaseErrors_nombreDuplicado() {

        ResponseEntity<?> response =
                handler.handleDatabaseErrors(
                        new DataIntegrityViolationException(
                                "Duplicate entry nombre"
                        )
                );

        Map<?, ?> body = (Map<?, ?>) response.getBody();

        assertEquals(409, response.getStatusCode().value());

        assertEquals("Ya existe un producto con ese nombre",
                body.get("mensaje"));
    }

    @Test
    void handleDatabaseErrors_campoNull() {

        ResponseEntity<?> response =
                handler.handleDatabaseErrors(
                        new DataIntegrityViolationException(
                                "cannot be null"
                        )
                );

        Map<?, ?> body = (Map<?, ?>) response.getBody();

        assertEquals(409, response.getStatusCode().value());

        assertEquals("Campo obligatorio faltante",
                body.get("mensaje"));
    }

    @Test
    void handleDatabaseErrors_duplicateEntry() {

        ResponseEntity<?> response =
                handler.handleDatabaseErrors(
                        new DataIntegrityViolationException(
                                "Duplicate entry datos"
                        )
                );

        Map<?, ?> body = (Map<?, ?>) response.getBody();

        assertEquals(409, response.getStatusCode().value());

        assertEquals("Ya existe un producto con esos datos",
                body.get("mensaje"));
    }

    @Test
    void handleDatabaseErrors_categoriaInvalida() {

        ResponseEntity<?> response =
                handler.handleDatabaseErrors(
                        new DataIntegrityViolationException(
                                "categoria_id"
                        )
                );

        Map<?, ?> body = (Map<?, ?>) response.getBody();

        assertEquals(409, response.getStatusCode().value());

        assertEquals("La categoría especificada no existe",
                body.get("mensaje"));
    }

    @Test
    void handleDatabaseErrors_errorGenerico() {

        ResponseEntity<?> response =
                handler.handleDatabaseErrors(
                        new DataIntegrityViolationException(
                                "error desconocido"
                        )
                );

        Map<?, ?> body = (Map<?, ?>) response.getBody();

        assertEquals(409, response.getStatusCode().value());

        assertEquals("Error de integridad de datos",
                body.get("mensaje"));
    }


    @Test
    void handleMissingParameter_retornaMensaje()
            throws Exception {

        ResponseEntity<?> response =
                handler.handleMissingParameter(
                        new MissingServletRequestParameterException(
                                "cantidad",
                                "Integer"
                        )
                );

        Map<?, ?> body = (Map<?, ?>) response.getBody();

        assertEquals(400, response.getStatusCode().value());

        assertTrue(
                body.get("mensaje")
                        .toString()
                        .contains("cantidad")
        );
    }


    @Test
    void handleGeneralErrors_retornaMensaje() {

        ResponseEntity<?> response =
                handler.handleGeneralErrors(
                        new Exception("Error inesperado")
                );

        Map<?, ?> body = (Map<?, ?>) response.getBody();

        assertEquals(500, response.getStatusCode().value());

        assertEquals("Internal Server Error",
                body.get("error"));

        assertEquals("Error interno del servidor",
                body.get("mensaje"));
    }
    @Test
    void handleValidationExceptions_retornaErrores() throws NoSuchMethodException {

        BeanPropertyBindingResult bindingResult =
                new BeanPropertyBindingResult(new Object(), "producto");

        bindingResult.addError(
                new FieldError(
                        "producto",
                        "nombre",
                        "El nombre es obligatorio"
                )
        );

        bindingResult.addError(
                new FieldError(
                        "producto",
                        "precio",
                        "El precio debe ser mayor a 0"
                )
        );

        Method method = this.getClass()
                .getDeclaredMethod(
                        "handleValidationExceptions_retornaErrores"
                );

        MethodArgumentNotValidException ex =
                new MethodArgumentNotValidException(
                        null,
                        bindingResult
                );

        ResponseEntity<?> response =
                handler.handleValidationExceptions(ex);

        Map<?, ?> body = (Map<?, ?>) response.getBody();

        assertEquals(400, response.getStatusCode().value());

        assertEquals("Validation Error",
                body.get("error"));

        assertEquals("Errores de validación",
                body.get("mensaje"));

        assertTrue(
                body.get("detalles")
                        .toString()
                        .contains("nombre")
        );

        assertTrue(
                body.get("detalles")
                        .toString()
                        .contains("precio")
        );
    }
}