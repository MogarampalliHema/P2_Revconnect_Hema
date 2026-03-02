package com.rev.app.repository;

import com.rev.app.entity.Message;
import com.rev.app.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class MessageRepositoryTest {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private UserRepository userRepository;

    private User user1;
    private User user2;

    @BeforeEach
    public void setUp() {
        user1 = new User("user1", "user1@example.com", "pass");
        user2 = new User("user2", "user2@example.com", "pass");
        userRepository.save(user1);
        userRepository.save(user2);
    }

    @Test
    public void testFindConversation() {
        Message m1 = new Message();
        m1.setSender(user1);
        m1.setRecipient(user2);
        m1.setContent("Hello");
        m1.setCreatedAt(LocalDateTime.now().minusMinutes(5));
        messageRepository.save(m1);

        Message m2 = new Message();
        m2.setSender(user2);
        m2.setRecipient(user1);
        m2.setContent("Hi");
        m2.setCreatedAt(LocalDateTime.now());
        messageRepository.save(m2);

        List<Message> conversation = messageRepository.findConversation(user1.getId(), user2.getId());
        assertThat(conversation).hasSize(2);
        assertThat(conversation.get(0).getContent()).isEqualTo("Hello");
        assertThat(conversation.get(1).getContent()).isEqualTo("Hi");
    }

    @Test
    public void testCountUnreadMessages() {
        Message m1 = new Message();
        m1.setSender(user1);
        m1.setRecipient(user2);
        m1.setContent("Unread");
        m1.setRead(false);
        messageRepository.save(m1);

        long count = messageRepository.countUnreadMessages(user2.getId());
        assertThat(count).isEqualTo(1);
    }
}
