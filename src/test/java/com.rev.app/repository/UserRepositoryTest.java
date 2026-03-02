package com.rev.app.repository;

import com.rev.app.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testSaveAndFindByUsername() {
        User user = new User("testuser", "test@example.com", "password");
        user.setFullName("Test User");
        userRepository.save(user);

        Optional<User> found = userRepository.findByUsername("testuser");
        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("test@example.com");
    }

    @Test
    public void testExistsByUsername() {
        User user = new User("unique_user", "unique@example.com", "password");
        userRepository.save(user);

        assertThat(userRepository.existsByUsername("unique_user")).isTrue();
        assertThat(userRepository.existsByUsername("non_existent")).isFalse();
    }
}
