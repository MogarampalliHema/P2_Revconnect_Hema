package com.rev.app.controller;

import com.rev.app.entity.User;
import com.rev.app.service.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class ProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private PostService postService;

    @MockBean
    private ConnectionService connectionService;

    @MockBean
    private FollowService followService;

    @MockBean
    private NotificationService notificationService;

    @Test
    @WithMockUser(username = "testuser")
    public void testMyProfile() throws Exception {
        User user = new User("testuser", "test@example.com", "pass");
        user.setId(1L);
        user.setPrivacySetting(User.PrivacySetting.PUBLIC);

        when(userService.findByUsername(anyString())).thenReturn(user);
        when(connectionService.areConnected(anyLong(), anyLong())).thenReturn(true);
        when(followService.isFollowing(anyLong(), anyLong())).thenReturn(false);
        when(connectionService.hasPendingRequest(anyLong(), anyLong())).thenReturn(false);
        when(connectionService.getConnections(any())).thenReturn(Collections.emptyList());
        when(notificationService.getUnreadCount(anyLong())).thenReturn(0L);

        mockMvc.perform(get("/profile"))
                .andExpect(status().isOk())
                .andExpect(view().name("profile"))
                .andExpect(model().attribute("isOwnProfile", true));
    }
}
