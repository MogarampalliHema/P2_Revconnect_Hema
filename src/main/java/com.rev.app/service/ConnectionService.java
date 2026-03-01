package com.rev.app.service;

import com.rev.app.entity.Connection;
import com.rev.app.entity.User;
import com.rev.app.exception.ResourceNotFoundException;
import com.rev.app.repository.ConnectionRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ConnectionService {

    private static final Logger logger = LogManager.getLogger(ConnectionService.class);

    private final ConnectionRepository connectionRepository;
    private final NotificationService notificationService;

    public ConnectionService(ConnectionRepository connectionRepository,
                             NotificationService notificationService) {
        this.connectionRepository = connectionRepository;
        this.notificationService = notificationService;
    }

    public Connection sendRequest(User sender, User receiver) {
        if (connectionRepository.connectionExists(sender.getId(), receiver.getId())) {
            throw new IllegalStateException("Connection already exists or is pending.");
        }
        Connection conn = new Connection(sender, receiver);
        Connection saved = connectionRepository.save(conn);
        notificationService.notifyConnectionRequest(receiver, sender);
        logger.info("Connection request sent: {} -> {}", sender.getUsername(), receiver.getUsername());
        return saved;
    }

    public Connection acceptRequest(Long connectionId, User currentUser) {
        Connection conn = findById(connectionId);
        if (!conn.getReceiver().getId().equals(currentUser.getId())) {
            throw new com.rev.app.exception.AccessDeniedException("Not authorized to accept this request.");
        }
        conn.setStatus(Connection.Status.ACCEPTED);
        Connection saved = connectionRepository.save(conn);
        notificationService.notifyConnectionAccepted(conn.getSender(), currentUser);
        logger.info("Connection accepted: {} <-> {}", conn.getSender().getUsername(), currentUser.getUsername());
        return saved;
    }

    public void rejectRequest(Long connectionId, User currentUser) {
        Connection conn = findById(connectionId);
        if (!conn.getReceiver().getId().equals(currentUser.getId())) {
            throw new com.rev.app.exception.AccessDeniedException("Not authorized to reject this request.");
        }
        conn.setStatus(Connection.Status.REJECTED);
        connectionRepository.save(conn);
    }

    public void removeConnection(Long connectionId, User currentUser) {
        Connection conn = findById(connectionId);
        boolean isMember = conn.getSender().getId().equals(currentUser.getId())
                || conn.getReceiver().getId().equals(currentUser.getId());
        if (!isMember) {
            throw new com.rev.app.exception.AccessDeniedException("Not authorized.");
        }
        connectionRepository.delete(conn);
        logger.info("Connection {} removed.", connectionId);
    }

    public void removeByUsers(User user1, User user2) {
        Connection conn = connectionRepository.findConnectionBetween(user1.getId(), user2.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Connection not found"));
        connectionRepository.delete(conn);
        logger.info("Connection between {} and {} removed.", user1.getUsername(), user2.getUsername());
    }

    @Transactional(readOnly = true)
    public List<Connection> getPendingReceived(User user) {
        return connectionRepository.findByReceiverAndStatus(user, Connection.Status.PENDING);
    }

    @Transactional(readOnly = true)
    public List<Connection> getPendingSent(User user) {
        return connectionRepository.findBySenderAndStatus(user, Connection.Status.PENDING);
    }

    @Transactional(readOnly = true)
    public List<User> getConnections(User user) {
        return connectionRepository.findAcceptedConnections(user.getId()).stream()
                .map(c -> c.getSender().getId().equals(user.getId()) ? c.getReceiver() : c.getSender())
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<Long> getConnectionIds(User user) {
        return getConnections(user).stream().map(User::getId).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public boolean areConnected(Long userId1, Long userId2) {
        return connectionRepository.areConnected(userId1, userId2);
    }

    @Transactional(readOnly = true)
    public boolean hasPendingRequest(Long senderId, Long receiverId) {
        return connectionRepository.connectionExists(senderId, receiverId);
    }

    private Connection findById(Long id) {
        return connectionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Connection not found: " + id));
    }
}
