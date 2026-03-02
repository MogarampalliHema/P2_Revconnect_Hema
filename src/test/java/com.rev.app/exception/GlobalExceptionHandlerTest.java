package com.rev.app.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;

import static org.assertj.core.api.Assertions.assertThat;

public class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;
    private Model model;

    @BeforeEach
    public void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
        model = new ConcurrentModel();
    }

    @Test
    public void testHandleResourceNotFound() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Not Found");
        String view = exceptionHandler.handleNotFound(ex, model);

        assertThat(view).isEqualTo("error");
        assertThat(model.getAttribute("errorMessage")).isEqualTo("Not Found");
        assertThat(model.getAttribute("errorCode")).isEqualTo(404);
    }

    @Test
    public void testHandleUserAlreadyExists() {
        UserAlreadyExistsException ex = new UserAlreadyExistsException("Already Exists");
        String view = exceptionHandler.handleUserExists(ex, model);

        assertThat(view).isEqualTo("error");
        assertThat(model.getAttribute("errorMessage")).isEqualTo("Already Exists");
        assertThat(model.getAttribute("errorCode")).isEqualTo(409);
    }

    @Test
    public void testHandleGeneralException() {
        Exception ex = new RuntimeException("Generic Error");
        String view = exceptionHandler.handleGeneral(ex, model);

        assertThat(view).isEqualTo("error");
        assertThat(model.getAttribute("errorMessage")).toString().contains("Generic Error");
        assertThat(model.getAttribute("errorCode")).isEqualTo(500);
    }
}
