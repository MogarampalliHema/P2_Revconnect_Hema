package com.rev.app.service;

import com.rev.app.entity.Post;
import com.rev.app.entity.User;
import com.rev.app.repository.PostRepository;
import com.rev.app.repository.LikeRepository;
import com.rev.app.repository.CommentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class AnalyticsService {

    private final PostRepository postRepository;
    private final LikeRepository likeRepository;
    private final CommentRepository commentRepository;
    private final FollowService followService;
    private final UserService userService;
    private final com.rev.app.repository.ConnectionRepository connectionRepository;

    public AnalyticsService(PostRepository postRepository,
            LikeRepository likeRepository,
            CommentRepository commentRepository,
            FollowService followService,
            UserService userService,
            com.rev.app.repository.ConnectionRepository connectionRepository) {
        this.postRepository = postRepository;
        this.likeRepository = likeRepository;
        this.commentRepository = commentRepository;
        this.followService = followService;
        this.userService = userService;
        this.connectionRepository = connectionRepository;
    }

    /**
     * Returns analytics for each published post of the given user.
     */
    public List<Map<String, Object>> getPostAnalytics(Long authorId) {
        List<Post> posts = postRepository.findByAuthorIdAndPublishedTrueOrderByPinnedDescCreatedAtDesc(authorId);
        List<Map<String, Object>> result = new ArrayList<>();
        for (Post post : posts) {
            Map<String, Object> analytics = new HashMap<>();
            analytics.put("postId", post.getId());
            analytics.put("content", post.getContent().length() > 80
                    ? post.getContent().substring(0, 80) + "..."
                    : post.getContent());
            analytics.put("createdAt", post.getCreatedAt());
            analytics.put("likes", likeRepository.countByPostId(post.getId()));
            analytics.put("comments", commentRepository.countByPostId(post.getId()));
            analytics.put("shares", post.getShares() != null ? post.getShares().size() : 0);
            result.add(analytics);
        }
        return result;
    }

    /**
     * Returns summary metrics for the user's account.
     */
    public Map<String, Object> getAccountMetrics(Long userId) {
        User user = userService.findById(userId);
        Map<String, Object> metrics = new HashMap<>();
        metrics.put("totalPosts", postRepository.countPublishedPostsByAuthor(userId));
        metrics.put("totalFollowers", followService.countFollowers(userId));
        metrics.put("totalFollowing", followService.countFollowing(userId));
        metrics.put("totalConnections", connectionRepository.countConnections(user));
        return metrics;
    }
}
