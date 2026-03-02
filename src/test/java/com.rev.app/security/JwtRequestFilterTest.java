package com.rev.app.security;

import com.rev.app.config.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.IOException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JwtRequestFilterTest {

    @Mock
    private CustomUserDetailsService userDetailsService;

    @Mock
    private JwtTokenUtil jwtTokenUtil;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtRequestFilter jwtRequestFilter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void doFilterInternal_WithValidToken_ShouldSetAuthentication() throws ServletException, IOException {
        // Arrange
        String token = "valid-token";
        String username = "testuser";
        UserDetails userDetails = new User(username, "password", new ArrayList<>());

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtTokenUtil.getUsernameFromToken(token)).thenReturn(username);
        when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);
        when(jwtTokenUtil.validateToken(token, userDetails)).thenReturn(true);

        // Act
        jwtRequestFilter.doFilterInternal(request, response, filterChain);

        // Assert
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals(username, SecurityContextHolder.getContext().getAuthentication().getName());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_WithNoToken_ShouldNotSetAuthentication() throws ServletException, IOException {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn(null);

        // Act
        jwtRequestFilter.doFilterInternal(request, response, filterChain);

        // Assert
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void shouldNotFilter_ForNonApiPaths_ShouldReturnTrue() throws ServletException {
        // Arrange
        when(request.getRequestURI()).thenReturn("/login");

        // Act
        boolean result = jwtRequestFilter.shouldNotFilter(request);

        // Assert
        assertTrue(result);
    }

    @Test
    void shouldNotFilter_ForApiPaths_ShouldReturnFalse() throws ServletException {
        // Arrange
        when(request.getRequestURI()).thenReturn("/api/posts");

        // Act
        boolean result = jwtRequestFilter.shouldNotFilter(request);

        // Assert
        assertFalse(result);
    }
}
