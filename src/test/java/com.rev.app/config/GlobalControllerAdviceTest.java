package com.rev.app.config;

import com.rev.app.entity.User;
import com.rev.app.service.NotificationService;
import com.rev.app.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GlobalControllerAdviceTest {

    @Mock
    private UserService userService;

    @Mock
    private NotificationService notificationService;

    @Mock
    private UserDetails userDetails;

    @InjectMocks
    private GlobalControllerAdvice globalControllerAdvice;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void currentUser_WithUserDetails_ShouldReturnUser() {
        // Arrange
        User user = new User();
        user.setUsername("testuser");
        when(userDetails.getUsername()).thenReturn("testuser");
        when(userService.findByUsername("testuser")).thenReturn(user);

        // Act
        User result = globalControllerAdvice.currentUser(userDetails);

        // Assert
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
    }

    @Test
    void currentUser_WithNullUserDetails_ShouldReturnNull() {
        // Act
        User result = globalControllerAdvice.currentUser(null);

        // Assert
        assertNull(result);
    }

    @Test
    void unreadCount_WithUserDetails_ShouldReturnCount() {
        // Arrange
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        when(userDetails.getUsername()).thenReturn("testuser");
        when(userService.findByUsername("testuser")).thenReturn(user);
        when(notificationService.getUnreadCount(1L)).thenReturn(5L);

        // Act
        Long result = globalControllerAdvice.unreadCount(userDetails);

        // Assert
        assertEquals(5L, result);
    }

    @Test
    void unreadCount_WithNullUserDetails_ShouldReturnZero() {
        // Act
        Long result = globalControllerAdvice.unreadCount(null);

        // Assert
        assertEquals(0L, result);
    }
}
