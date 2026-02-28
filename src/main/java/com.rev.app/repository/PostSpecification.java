package com.rev.app.repository;

import com.rev.app.entity.Post;
import org.springframework.data.jpa.domain.Specification;

/**
 * JPA Specifications for dynamic Post queries.
 * Enables feed filtering by post type, hashtag, etc.
 */
public class PostSpecification {

    public static Specification<Post> isPublished() {
        return (root, query, cb) -> cb.equal(root.get("published"), true);
    }

    public static Specification<Post> hasPostType(Post.PostType type) {
        return (root, query, cb) -> cb.equal(root.get("postType"), type);
    }

    public static Specification<Post> containsHashtag(String hashtag) {
        return (root, query, cb) -> cb.like(cb.lower(root.get("hashtags")), "%" + hashtag.toLowerCase() + "%");
    }

    public static Specification<Post> byAuthor(Long authorId) {
        return (root, query, cb) -> cb.equal(root.get("author").get("id"), authorId);
    }

    public static Specification<Post> byAuthorRole(com.rev.app.entity.User.UserRole role) {
        return (root, query, cb) -> cb.equal(root.get("author").get("role"), role);
    }
}
