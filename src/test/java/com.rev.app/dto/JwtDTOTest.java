package com.rev.app.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class JwtDTOTest {

    @Test
    void testJwtRequestDTO() {
        JwtRequestDTO request = new JwtRequestDTO();
        request.setUsername("testuser");
        request.setPassword("password");

        assertEquals("testuser", request.getUsername());
        assertEquals("password", request.getPassword());

        JwtRequestDTO request2 = new JwtRequestDTO("user2", "pass2");
        assertEquals("user2", request2.getUsername());
        assertEquals("pass2", request2.getPassword());
    }

    @Test
    void testJwtResponseDTO() {
        JwtResponseDTO response = new JwtResponseDTO();
        response.setJwttoken("token123");

        assertEquals("token123", response.getJwttoken());

        JwtResponseDTO response2 = new JwtResponseDTO("token456");
        assertEquals("token456", response2.getJwttoken());
    }
}
