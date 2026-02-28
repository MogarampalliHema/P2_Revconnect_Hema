package com.rev.app.service;

import com.rev.app.entity.Message;
import com.rev.app.entity.User;
import com.rev.app.repository.MessageRepository;
import com.rev.app.repository.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class MessageServiceTest {

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private MessageService messageService;

    private User sender;
    private User recipient;

    @Before
    public void setUp() {
        sender = new User();
        sender.setId(1L);
        sender.setUsername("sender");

        recipient = new User();
        recipient.setId(2L);
        recipient.setUsername("recipient");
    }

    @Test
    public void testSendMessage_Success() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(recipient));
        when(messageRepository.save(any(Message.class))).thenReturn(new Message(sender, recipient, "Hello"));

        Message result = messageService.sendMessage(sender, 2L, "Hello");

        assertNotNull(result);
        verify(messageRepository, times(1)).save(any(Message.class));
    }

    @Test(expected = RuntimeException.class)
    public void testSendMessage_RecipientNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());
        messageService.sendMessage(sender, 99L, "Hello");
    }

    @Test
    public void testGetConversation() {
        messageService.getConversation(1L, 2L);
        verify(messageRepository, times(1)).findConversation(1L, 2L);
    }

    @Test
    public void testMarkAsRead() {
        messageService.markAsRead(1L, 2L);
        verify(messageRepository, times(1)).markConversationAsRead(1L, 2L);
    }
}
