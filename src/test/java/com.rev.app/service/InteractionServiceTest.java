package com.rev.app.service;


import com.rev.app.dto.CommentDTO;
import com.rev.app.entity.Comment;
import com.rev.app.entity.Like;
import com.rev.app.entity.Post;
import com.rev.app.entity.User;
import com.rev.app.repository.CommentRepository;
import com.rev.app.repository.LikeRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class InteractionServiceTest {

    @Mock
    private LikeRepository likeRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private InteractionService interactionService;

    private User user;
    private Post post;

    @Before
    public void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        post = new Post();
        post.setId(5L);
        post.setAuthor(user);
        post.setPostType(Post.PostType.REGULAR);
        post.setPublished(true);
    }

    @Test
    public void testToggleLike_AddLike() {
        when(likeRepository.existsByUserIdAndPostId(1L, 5L)).thenReturn(false);
        when(likeRepository.save(any(Like.class))).thenReturn(new Like());

        boolean result = interactionService.toggleLike(post, user);

        assertTrue(result);
        verify(likeRepository, times(1)).save(any(Like.class));
        verify(notificationService).notifyPostLiked(any(), any(), any());
    }

    @Test
    public void testToggleLike_RemoveLike() {
        // When like already exists, service calls deleteByUserIdAndPostId
        when(likeRepository.existsByUserIdAndPostId(1L, 5L)).thenReturn(true);
        doNothing().when(likeRepository).deleteByUserIdAndPostId(1L, 5L);

        boolean result = interactionService.toggleLike(post, user);

        assertFalse(result);
        verify(likeRepository, times(1)).deleteByUserIdAndPostId(1L, 5L);
        verify(notificationService, never()).notifyPostLiked(any(), any(), any());
    }

    @Test
    public void testIsLiked_True() {
        when(likeRepository.existsByUserIdAndPostId(1L, 5L)).thenReturn(true);

        boolean result = interactionService.isLiked(1L, 5L);
        assertTrue(result);
    }

    @Test
    public void testIsLiked_False() {
        when(likeRepository.existsByUserIdAndPostId(1L, 5L)).thenReturn(false);

        boolean result = interactionService.isLiked(1L, 5L);
        assertFalse(result);
    }

    @Test
    public void testAddComment_Success() {
        CommentDTO dto = new CommentDTO();
        dto.setContent("Great post!");

        Comment saved = new Comment();
        saved.setId(1L);
        saved.setContent("Great post!");
        saved.setAuthor(user);
        saved.setPost(post);

        when(commentRepository.save(any(Comment.class))).thenReturn(saved);

        Comment result = interactionService.addComment(post, user, dto);

        assertNotNull(result);
        assertEquals("Great post!", result.getContent());
        verify(commentRepository, times(1)).save(any(Comment.class));
        verify(notificationService).notifyPostCommented(any(), any(), any());
    }

    @Test
    public void testGetComments_ReturnsList() {
        Comment c1 = new Comment();
        c1.setContent("Nice!");
        Comment c2 = new Comment();
        c2.setContent("Awesome!");
        when(commentRepository.findByPostIdOrderByCreatedAtAsc(5L))
                .thenReturn(Arrays.asList(c1, c2));

        List<Comment> comments = interactionService.getComments(5L);

        assertEquals(2, comments.size());
    }

    @Test
    public void testGetLikeCount() {
        when(likeRepository.countByPostId(5L)).thenReturn(7L);

        long count = interactionService.getLikeCount(5L);

        assertEquals(7L, count);
    }
}
