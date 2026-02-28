package com.rev.app.dto;

import com.rev.app.entity.Post;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

public class PostDTO {

    @NotBlank(message = "Post content cannot be empty")
    private String content;

    private String hashtags;

    private Post.PostType postType = Post.PostType.REGULAR;

    private String ctaLabel;

    private String ctaUrl;

    private LocalDateTime scheduledAt;

    private boolean pinned = false;

    // Getters and Setters
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getHashtags() {
        return hashtags;
    }

    public void setHashtags(String hashtags) {
        this.hashtags = hashtags;
    }

    public Post.PostType getPostType() {
        return postType;
    }

    public void setPostType(Post.PostType postType) {
        this.postType = postType;
    }

    public String getCtaLabel() {
        return ctaLabel;
    }

    public void setCtaLabel(String ctaLabel) {
        this.ctaLabel = ctaLabel;
    }

    public String getCtaUrl() {
        return ctaUrl;
    }

    public void setCtaUrl(String ctaUrl) {
        this.ctaUrl = ctaUrl;
    }

    public LocalDateTime getScheduledAt() {
        return scheduledAt;
    }

    public void setScheduledAt(LocalDateTime scheduledAt) {
        this.scheduledAt = scheduledAt;
    }

    public boolean isPinned() {
        return pinned;
    }

    public void setPinned(boolean pinned) {
        this.pinned = pinned;
    }
}
