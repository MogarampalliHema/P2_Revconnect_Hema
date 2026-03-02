package com.rev.app.security;

import com.rev.app.config.CustomUserDetailsService;
import com.rev.app.dto.JwtRequestDTO;
import com.rev.app.dto.JwtResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class JwtAuthControllerTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenUtil jwtTokenUtil;

    @Mock
    private CustomUserDetailsService userDetailsService;

    @InjectMocks
    private JwtAuthController jwtAuthController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createAuthenticationToken_WithValidCredentials_ShouldReturnToken() throws Exception {
        // Arrange
        JwtRequestDTO request = new JwtRequestDTO("testuser", "password");
        UserDetails userDetails = new User("testuser", "password", new ArrayList<>());
        String token = "mocked-jwt-token";

        when(userDetailsService.loadUserByUsername("testuser")).thenReturn(userDetails);
        when(jwtTokenUtil.generateToken(userDetails)).thenReturn(token);

        // Act
        ResponseEntity<?> response = jwtAuthController.createAuthenticationToken(request);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody() instanceof JwtResponseDTO);
        assertEquals(token, ((JwtResponseDTO) response.getBody()).getJwttoken());

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void createAuthenticationToken_WithDisabledUser_ShouldThrowException() {
        // Arrange
        JwtRequestDTO request = new JwtRequestDTO("testuser", "password");
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new DisabledException("USER_DISABLED"));

        // Act & Assert
        Exception exception = assertThrows(Exception.class, () -> {
            jwtAuthController.createAuthenticationToken(request);
        });

        assertTrue(exception.getMessage().contains("USER_DISABLED"));
    }

    @Test
    void createAuthenticationToken_WithBadCredentials_ShouldThrowException() {
        // Arrange
        JwtRequestDTO request = new JwtRequestDTO("testuser", "wrongpassword");
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("INVALID_CREDENTIALS"));

        // Act & Assert
        Exception exception = assertThrows(Exception.class, () -> {
            jwtAuthController.createAuthenticationToken(request);
        });

        assertTrue(exception.getMessage().contains("INVALID_CREDENTIALS"));
    }
}
