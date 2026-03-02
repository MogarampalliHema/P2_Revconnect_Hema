package com.rev.app.config;

import com.rev.app.entity.User;
import com.rev.app.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void loadUserByUsername_WithValidUser_ShouldReturnUserDetails() {
        // Arrange
        User user = new User("testuser", "test@example.com", "password");
        user.setActive(true);
        user.setRole(User.UserRole.PERSONAL);
        when(userRepository.findByUsernameOrEmail("testuser", "testuser")).thenReturn(Optional.of(user));

        // Act
        UserDetails userDetails = customUserDetailsService.loadUserByUsername("testuser");

        // Assert
        assertNotNull(userDetails);
        assertEquals("testuser", userDetails.getUsername());
        assertEquals("password", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_PERSONAL")));
    }

    @Test
    void loadUserByUsername_WithDeactivatedUser_ShouldThrowException() {
        // Arrange
        User user = new User("testuser", "test@example.com", "password");
        user.setActive(false);
        when(userRepository.findByUsernameOrEmail("testuser", "testuser")).thenReturn(Optional.of(user));

        // Act & Assert
        assertThrows(UsernameNotFoundException.class, () -> {
            customUserDetailsService.loadUserByUsername("testuser");
        });
    }

    @Test
    void loadUserByUsername_WithNotFoundUser_ShouldThrowException() {
        // Arrange
        when(userRepository.findByUsernameOrEmail("notfound", "notfound")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UsernameNotFoundException.class, () -> {
            customUserDetailsService.loadUserByUsername("notfound");
        });
    }
}
