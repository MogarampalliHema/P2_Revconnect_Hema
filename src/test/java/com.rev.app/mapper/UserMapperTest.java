package com.rev.app.mapper;

import com.rev.app.dto.RegisterDTO;
import com.rev.app.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class UserMapperTest {

    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        userMapper = new UserMapper();
    }

    @Test
    public void testToEntity() {
        RegisterDTO dto = new RegisterDTO();
        dto.setUsername("testuser");
        dto.setEmail("test@email.com");
        dto.setPassword("rawPassword");
        dto.setFullName("Test User");
        dto.setRole(User.UserRole.PERSONAL);

        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

        User user = userMapper.toEntity(dto, passwordEncoder);

        assertThat(user.getUsername()).isEqualTo("testuser");
        assertThat(user.getEmail()).isEqualTo("test@email.com");
        assertThat(user.getPassword()).isEqualTo("encodedPassword");
        assertThat(user.getFullName()).isEqualTo("Test User");
        assertThat(user.getRole()).isEqualTo(User.UserRole.PERSONAL);
    }

    @Test
    public void testToEntityWithNullRole() {
        RegisterDTO dto = new RegisterDTO();
        dto.setRole(null);

        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

        User user = userMapper.toEntity(dto, passwordEncoder);

        assertThat(user.getRole()).isEqualTo(User.UserRole.PERSONAL);
    }
}
