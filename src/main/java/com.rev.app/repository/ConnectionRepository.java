package com.rev.app.repository;

import com.rev.app.entity.Connection;
import com.rev.app.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConnectionRepository extends JpaRepository<Connection, Long> {

        Optional<Connection> findBySenderAndReceiver(User sender, User receiver);

        List<Connection> findByReceiverAndStatus(User receiver, Connection.Status status);

        List<Connection> findBySenderAndStatus(User sender, Connection.Status status);

        @Query("SELECT c FROM Connection c WHERE " +
                        "(c.sender = :user OR c.receiver = :user) AND c.status = 'ACCEPTED'")
        List<Connection> findAcceptedConnections(@Param("user") User user);

        @Query("SELECT COUNT(c) FROM Connection c WHERE " +
                        "(c.sender = :user OR c.receiver = :user) AND c.status = 'ACCEPTED'")
        long countConnections(@Param("user") User user);

        // Check if two users are connected
        @Query("SELECT CASE WHEN COUNT(c) > 0 THEN TRUE ELSE FALSE END FROM Connection c WHERE " +
                        "((c.sender.id = :userId1 AND c.receiver.id = :userId2) OR " +
                        " (c.sender.id = :userId2 AND c.receiver.id = :userId1)) " +
                        "AND c.status = 'ACCEPTED'")
        boolean areConnected(@Param("userId1") Long userId1, @Param("userId2") Long userId2);

        // Check if connection request already exists
        @Query("SELECT CASE WHEN COUNT(c) > 0 THEN TRUE ELSE FALSE END FROM Connection c WHERE " +
                        "((c.sender.id = :userId1 AND c.receiver.id = :userId2) OR " +
                        " (c.sender.id = :userId2 AND c.receiver.id = :userId1))")
        boolean connectionExists(@Param("userId1") Long userId1, @Param("userId2") Long userId2);

        @Query("SELECT c FROM Connection c WHERE " +
                        "((c.sender.id = :u1 AND c.receiver.id = :u2) OR (c.sender.id = :u2 AND c.receiver.id = :u1))")
        Optional<Connection> findConnectionBetween(@Param("u1") Long u1, @Param("u2") Long u2);
}
