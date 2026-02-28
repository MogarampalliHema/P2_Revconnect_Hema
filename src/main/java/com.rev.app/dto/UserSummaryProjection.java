package com.rev.app.dto;

/**
 * Interface-based Spring Data JPA Projection for lightweight user summaries.
 * Used in search results and connection lists to avoid loading full User
 * objects.
 */
public interface UserSummaryProjection {

    Long getId();

    String getUsername();

    String getFullName();

    String getProfilePicture();

    String getRole();

    String getBio();
}
