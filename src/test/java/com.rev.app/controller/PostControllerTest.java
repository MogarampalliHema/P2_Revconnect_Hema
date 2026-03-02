package com.rev.app.controller;

import com.rev.app.entity.Post;
import com.rev.app.entity.User;
import com.rev.app.service.InteractionService;
import com.rev.app.service.NotificationService;
import com.rev.app.service.PostService;
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

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PostService postService;

    @MockBean
    private UserService userService;

    @MockBean
    private InteractionService interactionService;

    @MockBean
    private NotificationService notificationService;

    @Test
    @WithMockUser(username = "testuser")
    public void testViewPost() throws Exception {
        User user = new User("testuser", "test@example.com", "pass");
        user.setId(1L);
        Post post = new Post();
        post.setId(1L);
        post.setAuthor(user);
        post.setContent("Test Content");

        when(userService.findByUsername(anyString())).thenReturn(user);
        when(postService.findById(anyLong())).thenReturn(post);
        when(interactionService.getComments(anyLong())).thenReturn(Collections.emptyList());
        when(interactionService.isLiked(anyLong(), anyLong())).thenReturn(false);
        when(interactionService.getLikeCount(anyLong())).thenReturn(0L);
        when(notificationService.getUnreadCount(anyLong())).thenReturn(0L);

        mockMvc.perform(get("/posts/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("post-detail"))
                .andExpect(model().attributeExists("post", "currentUser"));
    }
}
