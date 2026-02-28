package com.rev.app.service;

import com.rev.app.entity.Follow;
import com.rev.app.entity.User;
import com.rev.app.repository.FollowRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class FollowService {

    private static final Logger logger = LogManager.getLogger(FollowService.class);

    private final FollowRepository followRepository;
    private final NotificationService notificationService;

    public FollowService(FollowRepository followRepository,
            NotificationService notificationService) {
        this.followRepository = followRepository;
        this.notificationService = notificationService;
    }

    public Follow follow(User follower, User followed) {
        if (followRepository.existsByFollowerIdAndFollowedId(follower.getId(), followed.getId())) {
            throw new IllegalStateException("Already following this user.");
        }
        Follow follow = new Follow(follower, followed);
        Follow saved = followRepository.save(follow);
        notificationService.notifyNewFollower(followed, follower);
        logger.info("{} followed {}", follower.getUsername(), followed.getUsername());
        return saved;
    }

    public void unfollow(User follower, User followed) {
        followRepository.findByFollowerAndFollowed(follower, followed)
                .ifPresentOrElse(f -> {
                    followRepository.delete(f);
                    logger.info("{} unfollowed {}", follower.getUsername(), followed.getUsername());
                }, () -> {
                    throw new IllegalStateException("Not following this user.");
                });
    }

    @Transactional(readOnly = true)
    public boolean isFollowing(Long followerId, Long followedId) {
        return followRepository.existsByFollowerIdAndFollowedId(followerId, followedId);
    }

    @Transactional(readOnly = true)
    public List<User> getFollowers(Long userId) {
        return followRepository.findByFollowedId(userId).stream()
                .map(Follow::getFollower).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<User> getFollowing(Long userId) {
        return followRepository.findByFollowerId(userId).stream()
                .map(Follow::getFollowed).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<Long> getFollowedIds(Long userId) {
        return followRepository.findFollowedUserIdsByFollower(userId);
    }

    @Transactional(readOnly = true)
    public long countFollowers(Long userId) {
        return followRepository.countByFollowedId(userId);
    }

    @Transactional(readOnly = true)
    public long countFollowing(Long userId) {
        return followRepository.countByFollowerId(userId);
    }
}
