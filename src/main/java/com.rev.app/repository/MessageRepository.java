package com.rev.app.repository;

import com.rev.app.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {

    List<Message> findByRecipientIdOrderByCreatedAtDesc(Long recipientId);

    @Query("SELECT m FROM Message m WHERE (m.sender.id = :u1 AND m.recipient.id = :u2) OR (m.sender.id = :u2 AND m.recipient.id = :u1) ORDER BY m.createdAt ASC")
    List<Message> findConversation(@Param("u1") Long userId1, @Param("u2") Long userId2);

    @Query("SELECT COUNT(m) FROM Message m WHERE m.recipient.id = :recipientId AND m.read = false")
    long countUnreadMessages(@Param("recipientId") Long recipientId);

    @Modifying
    @Query("UPDATE Message m SET m.read = true WHERE m.recipient.id = :recipientId AND m.sender.id = :senderId")
    void markConversationAsRead(@Param("recipientId") Long recipientId, @Param("senderId") Long senderId);

    @Query("SELECT DISTINCT m.sender.id FROM Message m WHERE m.recipient.id = :userId UNION SELECT DISTINCT m.recipient.id FROM Message m WHERE m.sender.id = :userId")
    List<Long> findUserContactIds(@Param("userId") Long userId);

    @Modifying
    @Query("DELETE FROM Message m WHERE (m.sender.id = :u1 AND m.recipient.id = :u2) OR (m.sender.id = :u2 AND m.recipient.id = :u1)")
    void deleteConversation(@Param("u1") Long userId1, @Param("u2") Long userId2);
}
