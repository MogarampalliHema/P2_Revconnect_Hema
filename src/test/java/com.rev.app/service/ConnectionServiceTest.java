package com.rev.app.service;


import com.rev.app.entity.Connection;
import com.rev.app.entity.User;
import com.rev.app.repository.ConnectionRepository;
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
public class ConnectionServiceTest {

    @Mock
    private ConnectionRepository connectionRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private ConnectionService connectionService;

    private User alice;
    private User bob;
    private Connection pendingConn;

    @Before
    public void setUp() {
        alice = new User();
        alice.setId(1L);
        alice.setUsername("alice");
        alice.setRole(User.UserRole.PERSONAL);

        bob = new User();
        bob.setId(2L);
        bob.setUsername("bob");
        bob.setRole(User.UserRole.PERSONAL);

        pendingConn = new Connection();
        pendingConn.setId(100L);
        pendingConn.setSender(alice);
        pendingConn.setReceiver(bob);
        pendingConn.setStatus(Connection.Status.PENDING);
    }

    @Test
    public void testSendRequest_Success() {
        // connectionExists returns false - no existing connection
        when(connectionRepository.connectionExists(1L, 2L)).thenReturn(false);
        when(connectionRepository.save(any(Connection.class))).thenReturn(pendingConn);

        Connection result = connectionService.sendRequest(alice, bob);

        assertNotNull(result);
        assertEquals(Connection.Status.PENDING, result.getStatus());
        // notifyConnectionRequest(receiver, sender) - bob is receiver
        verify(notificationService).notifyConnectionRequest(bob, alice);
    }

    @Test(expected = IllegalStateException.class)
    public void testSendRequest_AlreadyExists() {
        when(connectionRepository.connectionExists(1L, 2L)).thenReturn(true);

        connectionService.sendRequest(alice, bob);
    }

    @Test
    public void testAcceptRequest_Success() {
        when(connectionRepository.findById(100L)).thenReturn(Optional.of(pendingConn));
        when(connectionRepository.save(any(Connection.class))).thenReturn(pendingConn);

        connectionService.acceptRequest(100L, bob);

        assertEquals(Connection.Status.ACCEPTED, pendingConn.getStatus());
        verify(notificationService).notifyConnectionAccepted(alice, bob);
    }

    @Test(expected = com.rev.app.exception.AccessDeniedException.class)
    public void testAcceptRequest_WrongReceiver() {
        when(connectionRepository.findById(100L)).thenReturn(Optional.of(pendingConn));

        // Alice is the sender, not receiver — should throw
        connectionService.acceptRequest(100L, alice);
    }

    @Test
    public void testAreConnected_True() {
        when(connectionRepository.areConnected(1L, 2L)).thenReturn(true);

        boolean result = connectionService.areConnected(1L, 2L);

        assertTrue(result);
    }

    @Test
    public void testAreConnected_False() {
        when(connectionRepository.areConnected(1L, 2L)).thenReturn(false);

        boolean result = connectionService.areConnected(1L, 2L);

        assertFalse(result);
    }

    @Test
    public void testGetConnections_ReturnsUserList() {
        Connection acceptedConn = new Connection();
        acceptedConn.setSender(alice);
        acceptedConn.setReceiver(bob);
        acceptedConn.setStatus(Connection.Status.ACCEPTED);

        when(connectionRepository.findAcceptedConnections(alice.getId()))
                .thenReturn(Arrays.asList(acceptedConn));

        List<User> connections = connectionService.getConnections(alice);

        assertNotNull(connections);
        assertFalse(connections.isEmpty());
        // alice is sender, so we expect bob to be returned
        assertEquals("bob", connections.get(0).getUsername());
    }

    @Test
    public void testHasPendingRequest_True() {
        when(connectionRepository.connectionExists(1L, 2L)).thenReturn(true);

        boolean result = connectionService.hasPendingRequest(1L, 2L);

        assertTrue(result);
    }

    @Test
    public void testHasPendingRequest_False() {
        when(connectionRepository.connectionExists(1L, 2L)).thenReturn(false);

        boolean result = connectionService.hasPendingRequest(1L, 2L);

        assertFalse(result);
    }
}
