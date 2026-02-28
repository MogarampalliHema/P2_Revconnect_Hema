package com.rev.app.service;

import com.rev.app.entity.Notification;
import com.rev.app.entity.NotificationPreference;
import com.rev.app.entity.User;
import com.rev.app.repository.NotificationPreferenceRepository;
import com.rev.app.repository.NotificationRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class NotificationService {

    private static final Logger logger = LogManager.getLogger(NotificationService.class);

    private final NotificationRepository notificationRepository;
    private final NotificationPreferenceRepository preferenceRepository;

    public NotificationService(NotificationRepository notificationRepository,
            NotificationPreferenceRepository preferenceRepository) {
        this.notificationRepository = notificationRepository;
        this.preferenceRepository = preferenceRepository;
    }

    private boolean isEnabled(User recipient, Notification.NotificationType type) {
        return preferenceRepository.findByUserId(recipient.getId())
                .map(pref -> switch (type) {
                    case CONNECTION_REQUEST -> pref.isConnectionRequests();
                    case CONNECTION_ACCEPTED -> pref.isConnectionAccepted();
                    case NEW_FOLLOWER -> pref.isNewFollowers();
                    case POST_LIKED -> pref.isPostLikes();
                    case POST_COMMENTED -> pref.isPostComments();
                    case POST_SHARED -> pref.isPostShares();
                    default -> true;
                })
                .orElse(true);
    }

    private void create(User recipient, User actor, Notification.NotificationType type,
            String message, Long referenceId) {
        if (recipient.getId().equals(actor.getId()))
            return; // no self-notifs
        if (!isEnabled(recipient, type))
            return;
        Notification n = new Notification(recipient, actor, type, message);
        n.setReferenceId(referenceId);
        notificationRepository.save(n);
        logger.debug("Notification created: {} -> {} type={}", actor.getUsername(), recipient.getUsername(), type);
    }

    public void notifyConnectionRequest(User receiver, User sender) {
        create(receiver, sender, Notification.NotificationType.CONNECTION_REQUEST,
                sender.getUsername() + " sent you a connection request.", null);
    }

    public void notifyConnectionAccepted(User requester, User acceptor) {
        create(requester, acceptor, Notification.NotificationType.CONNECTION_ACCEPTED,
                acceptor.getUsername() + " accepted your connection request.", null);
    }

    public void notifyNewFollower(User followed, User follower) {
        create(followed, follower, Notification.NotificationType.NEW_FOLLOWER,
                follower.getUsername() + " started following you.", null);
    }

    public void notifyPostLiked(User postOwner, User liker, Long postId) {
        create(postOwner, liker, Notification.NotificationType.POST_LIKED,
                liker.getUsername() + " liked your post.", postId);
    }

    public void notifyPostCommented(User postOwner, User commenter, Long postId) {
        create(postOwner, commenter, Notification.NotificationType.POST_COMMENTED,
                commenter.getUsername() + " commented on your post.", postId);
    }

    public void notifyPostShared(User postOwner, User sharer, Long postId) {
        create(postOwner, sharer, Notification.NotificationType.POST_SHARED,
                sharer.getUsername() + " shared your post.", postId);
    }

    @Transactional(readOnly = true)
    public List<Notification> getNotifications(Long userId) {
        return notificationRepository.findByRecipientIdOrderByCreatedAtDesc(userId);
    }

    @Transactional(readOnly = true)
    public long getUnreadCount(Long userId) {
        return notificationRepository.countByRecipientIdAndReadFalse(userId);
    }

    public void markAsRead(Long notificationId) {
        notificationRepository.markAsRead(notificationId);
    }

    public void markAllAsRead(Long userId) {
        notificationRepository.markAllAsRead(userId);
    }

    public void deleteNotification(Long notificationId) {
        notificationRepository.deleteById(notificationId);
    }

    public void deletePostNotifications(Long postId) {
        notificationRepository.deleteByReferenceIdAndTypes(postId, List.of(
                Notification.NotificationType.POST_LIKED,
                Notification.NotificationType.POST_COMMENTED,
                Notification.NotificationType.POST_SHARED));
    }

    public void clearAllNotifications(Long userId) {
        notificationRepository.deleteByRecipientId(userId);
    }
}
