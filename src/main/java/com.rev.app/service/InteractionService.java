package com.rev.app.service;

import com.rev.app.dto.CommentDTO;
import com.rev.app.entity.Comment;
import com.rev.app.entity.Like;
import com.rev.app.entity.Post;
import com.rev.app.entity.User;
import com.rev.app.exception.AccessDeniedException;
import com.rev.app.exception.ResourceNotFoundException;
import com.rev.app.repository.CommentRepository;
import com.rev.app.repository.LikeRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class InteractionService {

    private static final Logger logger = LogManager.getLogger(InteractionService.class);

    private final LikeRepository likeRepository;
    private final CommentRepository commentRepository;
    private final NotificationService notificationService;

    public InteractionService(LikeRepository likeRepository,
                              CommentRepository commentRepository,
                              NotificationService notificationService) {
        this.likeRepository = likeRepository;
        this.commentRepository = commentRepository;
        this.notificationService = notificationService;
    }

    // ========== LIKES ==========

    public boolean toggleLike(Post post, User user) {
        if (likeRepository.existsByUserIdAndPostId(user.getId(), post.getId())) {
            likeRepository.deleteByUserIdAndPostId(user.getId(), post.getId());
            logger.debug("{} unliked post {}", user.getUsername(), post.getId());
            return false;
        } else {
            likeRepository.save(new Like(user, post));
            notificationService.notifyPostLiked(post.getAuthor(), user, post.getId());
            logger.debug("{} liked post {}", user.getUsername(), post.getId());
            return true;
        }
    }

    @Transactional(readOnly = true)
    public boolean isLiked(Long userId, Long postId) {
        return likeRepository.existsByUserIdAndPostId(userId, postId);
    }

    @Transactional(readOnly = true)
    public long getLikeCount(Long postId) {
        return likeRepository.countByPostId(postId);
    }

    // ========== COMMENTS ==========

    public Comment addComment(Post post, User author, CommentDTO dto) {
        Comment comment = new Comment(post, author, dto.getContent());
        Comment saved = commentRepository.save(comment);
        notificationService.notifyPostCommented(post.getAuthor(), author, post.getId());
        return saved;
    }

    public void deleteComment(Long commentId, Long currentUserId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found: " + commentId));

        boolean isCommentAuthor = comment.getAuthor().getId().equals(currentUserId);
        boolean isPostAuthor = comment.getPost().getAuthor().getId().equals(currentUserId);

        if (!isCommentAuthor && !isPostAuthor) {
            throw new AccessDeniedException("You are not authorized to delete this comment.");
        }
        commentRepository.delete(comment);
    }

    @Transactional(readOnly = true)
    public List<Comment> getComments(Long postId) {
        return commentRepository.findByPostIdOrderByCreatedAtAsc(postId);
    }
}
