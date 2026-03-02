package com.rev.app.dto;

import com.rev.app.entity.Post;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

public class PostDTOTest {

    @Test
    public void testGettersAndSetters() {
        PostDTO dto = new PostDTO();
        dto.setContent("Hello World");
        dto.setHashtags("#test");
        dto.setPinned(true);
        dto.setPostType(Post.PostType.PROMOTIONAL);
        LocalDateTime now = LocalDateTime.now();
        dto.setScheduledAt(now);

        assertThat(dto.getContent()).isEqualTo("Hello World");
        assertThat(dto.getHashtags()).isEqualTo("#test");
        assertThat(dto.isPinned()).isTrue();
        assertThat(dto.getPostType()).isEqualTo(Post.PostType.PROMOTIONAL);
        assertThat(dto.getScheduledAt()).isEqualTo(now);
    }
}
