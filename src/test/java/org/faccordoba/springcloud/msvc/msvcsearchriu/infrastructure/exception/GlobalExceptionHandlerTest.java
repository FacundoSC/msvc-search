package org.faccordoba.springcloud.msvc.msvcsearchriu.infrastructure.exception;

import org.faccordoba.springcloud.msvc.msvcsearchriu.domain.exception.SearchDomainException;
import org.faccordoba.springcloud.msvc.msvcsearchriu.domain.exception.SearchNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {
    private GlobalExceptionHandler exceptionHandler;
    private WebRequest webRequest;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
        webRequest = mock(WebRequest.class);
        when(webRequest.getDescription(false))
        .thenReturn("uri=/search");
    }

    @AfterEach
    void tearDown() {
        exceptionHandler = null;
        webRequest = null;
    }

    @Test
    public void handleMethodArgumentNotValidShouldReturn400WithValidationErrorsTest() throws Exception {
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError("searchRequest", "hotelId", "Hotel ID cannot be blank");
        when(bindingResult.getAllErrors()).thenReturn(List.of(fieldError));

        java.lang.reflect.Method method = this.getClass().getDeclaredMethod("setUp");
        MethodParameter methodParameter = new MethodParameter(method, -1);
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(methodParameter, bindingResult);

        ResponseEntity<ValidationErrorResponse> response = exceptionHandler.handleMethodArgumentNotValid(ex, webRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo(400);
        assertThat(response.getBody().error()).isEqualTo("Validation Failed");
        assertThat(response.getBody().errors().get("hotelId")).isEqualTo("Hotel ID cannot be blank");
        assertThat(response.getBody().path()).isEqualTo("/search");
    }

    @Test
    public void handleIllegalArgumentExceptionShouldReturn400WithErrorResponseTest() {
        IllegalArgumentException ex = new IllegalArgumentException("Dates cannot be null");

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleIllegalArgumentException(ex, webRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo(400);
        assertThat(response.getBody().error()).isEqualTo("Bad Request");
        assertThat(response.getBody().message()).isEqualTo("Dates cannot be null");
        assertThat(response.getBody().path()).isEqualTo("/search");
    }

    @Test
    public void handleSearchNotFoundShouldReturn404WithErrorResponseTest() {
        SearchNotFoundException ex = new SearchNotFoundException("session-999");

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleSearchNotFound(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo(404);
        assertThat(response.getBody().error()).isEqualTo("Not Found");
        assertThat(response.getBody().message()).contains("session-999");
    }

    @Test
    public void handleDomainErrorShouldReturn500WithErrorResponseTest() {
        SearchDomainException ex = new SearchDomainException("Database connection failure");

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleDomainError(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo(500);
        assertThat(response.getBody().error()).isEqualTo("Internal Server Error");
        assertThat(response.getBody().message()).isEqualTo("Database connection failure");
    }

    @Test
    public void handleGlobalErrorShouldReturn500WithGenericErrorMessageTest() {
        Exception ex = new NullPointerException("Unexpected null reference");

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleGlobalError(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo(500);
        assertThat(response.getBody().error()).isEqualTo("unexpected error");
        assertThat(response.getBody().message()).isEqualTo("Unexpected null reference");
    }


}