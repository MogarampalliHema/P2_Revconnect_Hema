package com.rev.app.exception;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ExceptionClassesTest {

    @Test
    void testAccessDeniedException() {
        AccessDeniedException ex = new AccessDeniedException("Access Denied");
        assertEquals("Access Denied", ex.getMessage());
    }

    @Test
    void testResourceNotFoundException() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Not Found");
        assertEquals("Not Found", ex.getMessage());
    }

    @Test
    void testUserAlreadyExistsException() {
        UserAlreadyExistsException ex = new UserAlreadyExistsException("Already Exists");
        assertEquals("Already Exists", ex.getMessage());
    }
}
