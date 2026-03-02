package com.rev.app.controller;

import com.rev.app.entity.Post;
import com.rev.app.entity.User;
import com.rev.app.service.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
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
public class FeedControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PostService postService;

    @MockBean
    private UserService userService;

    @MockBean
    private ConnectionService connectionService;

    @MockBean
    private FollowService followService;

    @MockBean
    private NotificationService notificationService;

    @Test
    @WithMockUser(username = "testuser")
    public void testFeedPage() throws Exception {
        User user = new User("testuser", "test@example.com", "pass");
        user.setId(1L);
        when(userService.findByUsername(anyString())).thenReturn(user);
        when(connectionService.getConnectionIds(any())).thenReturn(Collections.emptyList());
        when(connectionService.getConnections(any())).thenReturn(Collections.emptyList());
        when(followService.getFollowedIds(anyLong())).thenReturn(Collections.emptyList());

        Page<Post> emptyPage = new PageImpl<>(Collections.emptyList());
        when(postService.getFeed(anyList(), anyInt(), anyInt())).thenReturn(emptyPage);
        when(postService.getTrendingPosts(anyInt())).thenReturn(emptyPage);
        when(notificationService.getUnreadCount(anyLong())).thenReturn(0L);

        mockMvc.perform(get("/feed"))
                .andExpect(status().isOk())
                .andExpect(view().name("feed"))
                .andExpect(model().attributeExists("posts", "currentUser", "newPost"));
    }
}
