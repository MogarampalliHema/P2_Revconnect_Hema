package com.rev.app.controller;

import com.rev.app.entity.User;
import com.rev.app.service.ConnectionService;
import com.rev.app.service.NotificationService;
import com.rev.app.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class ConnectionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ConnectionService connectionService;

    @MockBean
    private UserService userService;

    @MockBean
    private NotificationService notificationService;

    @Test
    @WithMockUser(username = "testuser")
    public void testConnectionsPage() throws Exception {
        User user = new User("testuser", "test@example.com", "pass");
        user.setId(1L);
        when(userService.findByUsername(anyString())).thenReturn(user);
        when(connectionService.getConnections(any())).thenReturn(Collections.emptyList());
        when(connectionService.getPendingReceived(any())).thenReturn(Collections.emptyList());
        when(connectionService.getPendingSent(any())).thenReturn(Collections.emptyList());
        when(notificationService.getUnreadCount(anyLong())).thenReturn(0L);

        mockMvc.perform(get("/connections"))
                .andExpect(status().isOk())
                .andExpect(view().name("connections"));
    }
}
