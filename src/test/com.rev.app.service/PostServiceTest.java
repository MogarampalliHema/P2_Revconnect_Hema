package com.rev.app.service;

import com.rev.app.dto.PostDTO;
import com.rev.app.entity.Post;
import com.rev.app.entity.User;
import com.rev.app.exception.AccessDeniedException;
import com.rev.app.repository.PostRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private NotificationService notificationService;

    @Mock
    private com.rev.app.mapper.PostMapper postMapper;

    @InjectMocks
    private PostService postService;

    private User author;
    private Post testPost;
    private PostDTO postDTO;

    @Before
    public void setUp() {
        author = new User();
        author.setId(1L);
        author.setUsername("testuser");
        author.setRole(User.UserRole.PERSONAL);

        postDTO = new PostDTO();
        postDTO.setContent("Hello World #test");
        postDTO.setHashtags("#test");

        testPost = new Post();
        testPost.setId(10L);
        testPost.setAuthor(author);
        testPost.setContent("Hello World #test");
        testPost.setPostType(Post.PostType.REGULAR);
        testPost.setPublished(true);
    }

    @Test
    public void testCreatePost_Regular() {
        when(postMapper.toEntity(any(PostDTO.class), any(User.class))).thenReturn(testPost);
        when(postRepository.save(any(Post.class))).thenReturn(testPost);

        Post result = postService.createPost(author, postDTO);

        assertNotNull(result);
        assertEquals(Post.PostType.REGULAR, result.getPostType());
        assertTrue(result.isPublished());
        verify(postRepository, times(1)).save(any(Post.class));
    }

    @Test
    public void testCreatePost_WithHashtags() {
        postDTO.setHashtags("#spring, #java");
        when(postMapper.toEntity(any(PostDTO.class), any(User.class))).thenReturn(testPost);
        when(postRepository.save(any(Post.class))).thenReturn(testPost);

        Post result = postService.createPost(author, postDTO);

        assertNotNull(result);
        verify(postRepository).save(any(Post.class));
    }

    @Test
    public void testDeletePost_OwnPost() {
        when(postRepository.findById(10L)).thenReturn(Optional.of(testPost));
        doNothing().when(postRepository).delete(testPost);

        postService.deletePost(10L, 1L);

        verify(postRepository, times(1)).delete(testPost);
    }

    @Test(expected = AccessDeniedException.class)
    public void testDeletePost_OtherUserPost() {
        when(postRepository.findById(10L)).thenReturn(Optional.of(testPost));

        // User with different ID tries to delete
        postService.deletePost(10L, 999L);
    }

    @Test
    public void testUpdatePost_Success() {
        PostDTO updateDTO = new PostDTO();
        updateDTO.setContent("Updated content");
        updateDTO.setHashtags("#updated");

        when(postRepository.findById(10L)).thenReturn(Optional.of(testPost));
        when(postRepository.save(any(Post.class))).thenReturn(testPost);

        Post result = postService.updatePost(10L, 1L, updateDTO);

        assertNotNull(result);
        verify(postRepository, times(1)).save(any(Post.class));
    }

    @Test(expected = AccessDeniedException.class)
    public void testUpdatePost_NotOwner() {
        PostDTO updateDTO = new PostDTO();
        updateDTO.setContent("Updated content");

        when(postRepository.findById(10L)).thenReturn(Optional.of(testPost));

        postService.updatePost(10L, 999L, updateDTO);
    }

    @Test
    public void testGetFeed_ReturnsPaginatedPosts() {
        List<Post> posts = Arrays.asList(testPost);
        Page<Post> postPage = new PageImpl<>(posts, PageRequest.of(0, 10), 1);
        when(postRepository.findFeedPostsByUserIds(any(), any())).thenReturn(postPage);

        Page<Post> result = postService.getFeed(Arrays.asList(1L), 0, 10);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    public void testSharePost_CreatesRepost() {
        Post repost = new Post();
        repost.setId(20L);
        repost.setPostType(Post.PostType.REPOST);
        repost.setPublished(true);

        when(postRepository.findById(10L)).thenReturn(Optional.of(testPost));
        when(postRepository.save(any(Post.class))).thenReturn(repost);

        Post result = postService.sharePost(10L, author);

        assertEquals(Post.PostType.REPOST, result.getPostType());
        verify(notificationService).notifyPostShared(any(), any(), any());
    }
}
