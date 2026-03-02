package com.rev.app.repository;

import com.rev.app.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class UserSpecificationTest {

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void setUp() {
        User user1 = new User("alice", "alice@example.com", "pass");
        user1.setFullName("Alice Smith");
        user1.setActive(true);
        userRepository.save(user1);

        User user2 = new User("bob", "bob@example.com", "pass");
        user2.setFullName("Robert Jones");
        user2.setActive(true);
        userRepository.save(user2);
    }

    @Test
    public void testSearchByNameOrUsername() {
        Specification<User> spec = UserSpecification.searchByNameOrUsername("alice");
        List<User> result = userRepository.findAll(spec);
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUsername()).isEqualTo("alice");
    }

    @Test
    public void testHasFullName() {
        Specification<User> spec = UserSpecification.hasFullName("smith");
        List<User> result = userRepository.findAll(spec);
        assertThat(result).hasSize(1);
    }
}
