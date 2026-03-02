package com.rev.app.repository;

import com.rev.app.entity.Notification;
import com.rev.app.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class NotificationRepositoryTest {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;

    private User recipient;
    private User actor;

    @BeforeEach
    public void setUp() {
        recipient = new User("recipient", "rec@example.com", "pass");
        actor = new User("actor", "actor@example.com", "pass");
        userRepository.save(recipient);
        userRepository.save(actor);
    }

    @Test
    public void testFindByRecipientIdAndReadFalse() {
        Notification n = new Notification();
        n.setRecipient(recipient);
        n.setActor(actor);
        n.setType(Notification.NotificationType.CONNECTION_REQUEST);
        n.setRead(false);
        notificationRepository.save(n);

        List<Notification> unread = notificationRepository
                .findByRecipientIdAndReadFalseOrderByCreatedAtDesc(recipient.getId());
        assertThat(unread).hasSize(1);
    }

    @Test
    public void testMarkAllAsRead() {
        Notification n = new Notification();
        n.setRecipient(recipient);
        n.setActor(actor);
        n.setType(Notification.NotificationType.POST_LIKED);
        n.setRead(false);
        notificationRepository.save(n);

        notificationRepository.markAllAsRead(recipient.getId());

        long count = notificationRepository.countByRecipientIdAndReadFalse(recipient.getId());
        assertThat(count).isEqualTo(0);
    }
}
