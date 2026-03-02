package com.rev.app.service;


import com.rev.app.entity.Post;
import com.rev.app.entity.User;
import com.rev.app.repository.CommentRepository;
import com.rev.app.repository.LikeRepository;
import com.rev.app.repository.PostRepository;
import com.rev.app.repository.ConnectionRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AnalyticsServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private LikeRepository likeRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private FollowService followService;

    @Mock
    private UserService userService;

    @Mock
    private ConnectionRepository connectionRepository;

    @InjectMocks
    private AnalyticsService analyticsService;

    private User testUser;
    private Post testPost;

    @Before
    public void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");

        testPost = new Post(testUser,
                "This is a test post that should be truncated if it is very long but this one is medium.");
        testPost.setId(10L);
        testPost.setPublished(true);
    }

    @Test
    public void testGetPostAnalytics() {
        when(postRepository.findByAuthorIdAndPublishedTrueOrderByPinnedDescCreatedAtDesc(1L))
                .thenReturn(Arrays.asList(testPost));
        when(likeRepository.countByPostId(10L)).thenReturn(5L);
        when(commentRepository.countByPostId(10L)).thenReturn(3L);

        List<Map<String, Object>> result = analyticsService.getPostAnalytics(1L);

        assertEquals(1, result.size());
        Map<String, Object> analytics = result.get(0);
        assertEquals(10L, analytics.get("postId"));
        assertEquals(5L, analytics.get("likes"));
        assertEquals(3L, analytics.get("comments"));
    }

    @Test
    public void testGetAccountMetrics() {
        when(userService.findById(1L)).thenReturn(testUser);
        when(postRepository.countPublishedPostsByAuthor(1L)).thenReturn(10L);
        when(followService.countFollowers(1L)).thenReturn(100L);
        when(followService.countFollowing(1L)).thenReturn(50L);
        when(connectionRepository.countConnections(1L)).thenReturn(100L);

        Map<String, Object> metrics = analyticsService.getAccountMetrics(1L);

        assertEquals(10L, metrics.get("totalPosts"));
        assertEquals(100L, metrics.get("totalFollowers"));
        assertEquals(50L, metrics.get("totalFollowing"));
        assertEquals(100L, metrics.get("totalConnections"));
    }
}
