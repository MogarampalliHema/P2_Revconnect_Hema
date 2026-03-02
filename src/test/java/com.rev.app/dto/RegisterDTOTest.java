package com.rev.app.dto;

import com.rev.app.entity.User;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class RegisterDTOTest {

    @Test
    public void testGettersAndSetters() {
        RegisterDTO dto = new RegisterDTO();
        dto.setUsername("testuser");
        dto.setEmail("test@email.com");
        dto.setPassword("password123");
        dto.setFullName("Test User");
        dto.setRole(User.UserRole.PERSONAL);

        assertThat(dto.getUsername()).isEqualTo("testuser");
        assertThat(dto.getEmail()).isEqualTo("test@email.com");
        assertThat(dto.getPassword()).isEqualTo("password123");
        assertThat(dto.getFullName()).isEqualTo("Test User");
        assertThat(dto.getRole()).isEqualTo(User.UserRole.PERSONAL);
    }
}
