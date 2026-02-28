package com.rev.app.service;

import com.rev.app.entity.Message;
import com.rev.app.entity.User;
import com.rev.app.repository.MessageRepository;
import com.rev.app.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    public MessageService(MessageRepository messageRepository, UserRepository userRepository) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
    }

    public Message sendMessage(User sender, Long recipientId, String content) {
        User recipient = userRepository.findById(recipientId)
                .orElseThrow(() -> new RuntimeException("Recipient not found"));
        Message message = new Message(sender, recipient, content);
        return messageRepository.save(message);
    }

    @Transactional(readOnly = true)
    public List<Message> getConversation(Long userId1, Long userId2) {
        return messageRepository.findConversation(userId1, userId2);
    }

    @Transactional(readOnly = true)
    public List<User> getContacts(Long userId) {
        List<Long> contactIds = messageRepository.findUserContactIds(userId);
        return userRepository.findAllById(contactIds);
    }

    public void markAsRead(Long recipientId, Long senderId) {
        messageRepository.markConversationAsRead(recipientId, senderId);
    }

    @Transactional(readOnly = true)
    public long getUnreadCount(Long userId) {
        return messageRepository.countUnreadMessages(userId);
    }

    public void deleteMessage(Long messageId, Long requesterId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Message not found"));
        if (!message.getSender().getId().equals(requesterId) && !message.getRecipient().getId().equals(requesterId)) {
            throw new RuntimeException("Unauthorized");
        }
        messageRepository.delete(message);
    }

    public void deleteConversation(Long userId1, Long userId2) {
        messageRepository.deleteConversation(userId1, userId2);
    }
}
