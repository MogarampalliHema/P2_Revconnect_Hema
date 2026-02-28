package com.rev.app.dto;

import jakarta.validation.constraints.NotBlank;

public class CommentDTO {

    @NotBlank(message = "Comment cannot be empty")
    private String content;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
