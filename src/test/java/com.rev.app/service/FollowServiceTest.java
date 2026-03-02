package com.rev.app.service;


import com.rev.app.entity.Follow;
import com.rev.app.entity.User;
import com.rev.app.repository.FollowRepository;
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
public class FollowServiceTest {

    @Mock
    private FollowRepository followRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private FollowService followService;

    private User follower;
    private User followed;

    @Before
    public void setUp() {
        follower = new User();
        follower.setId(1L);
        follower.setUsername("follower");

        followed = new User();
        followed.setId(2L);
        followed.setUsername("followed");
    }

    @Test
    public void testFollow_Success() {
        when(followRepository.existsByFollowerIdAndFollowedId(1L, 2L)).thenReturn(false);
        when(followRepository.save(any(Follow.class))).thenReturn(new Follow(follower, followed));

        Follow result = followService.follow(follower, followed);

        assertNotNull(result);
        verify(followRepository, times(1)).save(any(Follow.class));
        verify(notificationService, times(1)).notifyNewFollower(followed, follower);
    }

    @Test(expected = IllegalStateException.class)
    public void testFollow_AlreadyFollowing() {
        when(followRepository.existsByFollowerIdAndFollowedId(1L, 2L)).thenReturn(true);
        followService.follow(follower, followed);
    }

    @Test
    public void testUnfollow_Success() {
        Follow follow = new Follow(follower, followed);
        when(followRepository.findByFollowerAndFollowed(follower, followed)).thenReturn(Optional.of(follow));

        followService.unfollow(follower, followed);

        verify(followRepository, times(1)).delete(follow);
    }

    @Test(expected = IllegalStateException.class)
    public void testUnfollow_NotFollowing() {
        when(followRepository.findByFollowerAndFollowed(follower, followed)).thenReturn(Optional.empty());
        followService.unfollow(follower, followed);
    }

    @Test
    public void testIsFollowing() {
        when(followRepository.existsByFollowerIdAndFollowedId(1L, 2L)).thenReturn(true);
        assertTrue(followService.isFollowing(1L, 2L));
    }

    @Test
    public void testGetFollowers() {
        Follow f = new Follow(follower, followed);
        when(followRepository.findByFollowedId(2L)).thenReturn(Arrays.asList(f));

        List<User> followers = followService.getFollowers(2L);

        assertEquals(1, followers.size());
        assertEquals("follower", followers.get(0).getUsername());
    }
}
