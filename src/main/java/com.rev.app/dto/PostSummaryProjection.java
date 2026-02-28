package com.rev.app.dto;

import java.time.LocalDateTime;

/**
 * Interface-based Spring Data JPA Projection for lightweight post summaries.
 * Used in feeds and profile views to avoid loading full Post objects with all
 * lazy relations.
 */
public interface PostSummaryProjection {

    Long getId();

    String getContent();

    String getHashtags();

    String getPostType();

    LocalDateTime getCreatedAt();

    String getImageUrl();

    // Nested projection for author
    AuthorSummary getAuthor();

    interface AuthorSummary {
        Long getId();

        String getUsername();

        String getFullName();

        String getProfilePicture();
    }
}
