package com.rev.app.repository;

import com.rev.app.entity.User;
import org.springframework.data.jpa.domain.Specification;

/**
 * JPA Specifications for dynamic User queries.
 * Enables composable, type-safe filtering for user search.
 */
public class UserSpecification {

    public static Specification<User> hasUsername(String username) {
        return (root, query, cb) -> cb.like(cb.lower(root.get("username")), "%" + username.toLowerCase() + "%");
    }

    public static Specification<User> hasFullName(String name) {
        return (root, query, cb) -> cb.like(cb.lower(root.get("fullName")), "%" + name.toLowerCase() + "%");
    }

    public static Specification<User> hasRole(User.UserRole role) {
        return (root, query, cb) -> cb.equal(root.get("role"), role);
    }

    public static Specification<User> isActive() {
        return (root, query, cb) -> cb.equal(root.get("active"), true);
    }

    public static Specification<User> isPublic() {
        return (root, query, cb) -> cb.equal(root.get("privacySetting"), User.PrivacySetting.PUBLIC);
    }

    /**
     * Composes a search by name or username.
     */
    public static Specification<User> searchByNameOrUsername(String query) {
        return Specification
                .where(isActive())
                .and(hasUsername(query).or(hasFullName(query)));
    }
}
