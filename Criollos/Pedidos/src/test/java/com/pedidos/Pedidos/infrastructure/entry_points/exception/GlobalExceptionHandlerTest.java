package com.pedidos.Pedidos.infrastructure.entry_points.exception;

import com.pedidos.Pedidos.domain.model.ApiError;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();
    private final HttpServletRequest request = mock(HttpServletRequest.class);

    @Test
    void manejaNoResourceFound() {
        when(request.getRequestURI()).thenReturn("/sin-ruta");

        ResponseEntity<ApiError> response = handler.handleNoResourceFound(mock(NoResourceFoundException.class), request);

        assertError(response, 404, "Ruta no encontrada", "La ruta solicitada no existe", "/sin-ruta");
    }

    @Test
    void manejaNoHandlerFound() {
        when(request.getRequestURI()).thenReturn("/sin-handler");
        NoHandlerFoundException exception = new NoHandlerFoundException("GET", "/sin-handler", null);

        ResponseEntity<ApiError> response = handler.handleNoHandlerFound(exception, request);

        assertError(response, 404, "Endpoint no encontrado", "No existe un endpoint", "/sin-handler");
    }

    @Test
    void manejaMetodoNoSoportado() {
        when(request.getRequestURI()).thenReturn("/pedidos");
        HttpRequestMethodNotSupportedException exception = new HttpRequestMethodNotSupportedException("PATCH");

        ResponseEntity<ApiError> response = handler.handleMethodNotSupported(exception, request);

        assertError(response, 405, "HTTP no permitido", "PATCH", "/pedidos");
    }

    @Test
    void manejaIllegalArgument() {
        when(request.getRequestURI()).thenReturn("/estado");

        ResponseEntity<ApiError> response = handler.handleIllegalArgument(new IllegalArgumentException("NUEVO"), request);

        assertError(response, 400, "Solicitud", "NUEVO", "/estado");
    }

    @Test
    void manejaRuntimeException() {
        when(request.getRequestURI()).thenReturn("/guardar");

        ResponseEntity<ApiError> response = handler.handleRuntimeException(new RuntimeException("Pedido no encontrado"), request);

        assertError(response, 400, "Error en la solicitud", "Pedido no encontrado", "/guardar");
    }

    @Test
    void manejaExcepcionGeneral() {
        when(request.getRequestURI()).thenReturn("/general");

        ResponseEntity<ApiError> response = handler.handleGeneralException(new Exception("fallo"), request);

        assertError(response, 500, "Error interno del servidor", "error inesperado", "/general");
    }

    private void assertError(ResponseEntity<ApiError> response, int status, String errorPart, String messagePart, String path) {
        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getTimestamp()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(status);
        assertThat(response.getBody().getError()).contains(errorPart);
        assertThat(response.getBody().getMessage()).contains(messagePart);
        assertThat(response.getBody().getPath()).isEqualTo(path);
    }
}
