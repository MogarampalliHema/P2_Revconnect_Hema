package com.rev.app.repository;

import com.rev.app.entity.NotificationPreference;
import com.rev.app.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class NotificationPreferenceRepositoryTest {

    @Autowired
    private NotificationPreferenceRepository preferenceRepository;

    @Autowired
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    public void setUp() {
        user = new User("user1", "user1@example.com", "pass");
        userRepository.save(user);
    }

    @Test
    public void testFindByUserId() {
        NotificationPreference prefs = new NotificationPreference(user);
        prefs.setPostLikes(true);
        preferenceRepository.save(prefs);

        Optional<NotificationPreference> result = preferenceRepository.findByUserId(user.getId());
        assertThat(result).isPresent();
        assertThat(result.get().isPostLikes()).isTrue();
    }
}
