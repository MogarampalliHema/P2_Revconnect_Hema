package com.rev.app.service;


import com.rev.app.entity.Notification;
import com.rev.app.entity.NotificationPreference;
import com.rev.app.entity.User;
import com.rev.app.repository.NotificationPreferenceRepository;
import com.rev.app.repository.NotificationRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private NotificationPreferenceRepository preferenceRepository;

    @InjectMocks
    private NotificationService notificationService;

    private User recipient;
    private User actor;

    @Before
    public void setUp() {
        recipient = new User();
        recipient.setId(1L);
        recipient.setUsername("recipient");

        actor = new User();
        actor.setId(2L);
        actor.setUsername("actor");
    }

    @Test
    public void testNotifyConnectionRequest_Enabled() {
        NotificationPreference pref = new NotificationPreference();
        pref.setConnectionRequests(true);
        when(preferenceRepository.findByUserId(recipient.getId())).thenReturn(Optional.of(pref));

        notificationService.notifyConnectionRequest(recipient, actor);

        verify(notificationRepository, times(1)).save(any(Notification.class));
    }

    @Test
    public void testNotifyConnectionRequest_Disabled() {
        NotificationPreference pref = new NotificationPreference();
        pref.setConnectionRequests(false);
        when(preferenceRepository.findByUserId(recipient.getId())).thenReturn(Optional.of(pref));

        notificationService.notifyConnectionRequest(recipient, actor);

        verify(notificationRepository, never()).save(any(Notification.class));
    }

    @Test
    public void testNotifySelf_NoNotification() {
        notificationService.notifyConnectionRequest(recipient, recipient);
        verify(notificationRepository, never()).save(any(Notification.class));
    }

    @Test
    public void testNotifyPostLiked() {
        NotificationPreference pref = new NotificationPreference();
        pref.setPostLikes(true);
        when(preferenceRepository.findByUserId(recipient.getId())).thenReturn(Optional.of(pref));

        notificationService.notifyPostLiked(recipient, actor, 100L);

        verify(notificationRepository, times(1))
                .save(argThat(n -> n.getType() == Notification.NotificationType.POST_LIKED &&
                        n.getReferenceId().equals(100L)));
    }

    @Test
    public void testGetUnreadCount() {
        when(notificationRepository.countByRecipientIdAndReadFalse(1L)).thenReturn(5L);
        long count = notificationService.getUnreadCount(1L);
        assertEquals(5L, count);
    }

    @Test
    public void testMarkAsRead() {
        notificationService.markAsRead(1L);
        verify(notificationRepository, times(1)).markAsRead(1L);
    }

    @Test
    public void testMarkAllAsRead() {
        notificationService.markAllAsRead(1L);
        verify(notificationRepository, times(1)).markAllAsRead(1L);
    }
}
