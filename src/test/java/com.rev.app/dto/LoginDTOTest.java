package com.rev.app.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class LoginDTOTest {
    @Test
    void testGettersAndSetters() {
        LoginDTO dto = new LoginDTO();
        dto.setUsernameOrEmail("testuser");
        dto.setPassword("password123");

        assertEquals("testuser", dto.getUsernameOrEmail());
        assertEquals("password123", dto.getPassword());
    }
}
