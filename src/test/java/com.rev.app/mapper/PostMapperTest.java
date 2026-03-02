package com.rev.app.mapper;

import com.rev.app.dto.PostDTO;
import com.rev.app.entity.Post;
import com.rev.app.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class PostMapperTest {

    private PostMapper postMapper;
    private User author;

    @BeforeEach
    void setUp() {
        postMapper = new PostMapper();
        author = new User();
        author.setId(1L);
        author.setUsername("testuser");
    }

    @Test
    void toEntity_ShouldMapFieldsCorrectly() {
        PostDTO dto = new PostDTO();
        dto.setContent("Test content #test");
        dto.setHashtags("tag1 tag2");
        dto.setPostType(Post.PostType.REGULAR);
        dto.setCtaLabel("Click Me");
        dto.setCtaUrl("http://example.com");
        dto.setPinned(true);

        Post post = postMapper.toEntity(dto, author);

        assertEquals(author, post.getAuthor());
        assertEquals(dto.getContent(), post.getContent());
        assertEquals("tag1,tag2", post.getHashtags());
        assertEquals(Post.PostType.REGULAR, post.getPostType());
        assertEquals(dto.getCtaLabel(), post.getCtaLabel());
        assertEquals(dto.getCtaUrl(), post.getCtaUrl());
        assertTrue(post.isPinned());
        assertTrue(post.isPublished());
    }

    @Test
    void toEntity_WithScheduledDate_ShouldNotBePublished() {
        PostDTO dto = new PostDTO();
        dto.setContent("Scheduled post");
        dto.setScheduledAt(LocalDateTime.now().plusDays(1));

        Post post = postMapper.toEntity(dto, author);

        assertFalse(post.isPublished());
        assertNotNull(post.getScheduledAt());
    }

    @Test
    void toEntity_WithNullHashtags_ShouldHandleNull() {
        PostDTO dto = new PostDTO();
        dto.setContent("No tags");
        dto.setHashtags(null);

        Post post = postMapper.toEntity(dto, author);

        assertNull(post.getHashtags());
    }

    @Test
    void toEntity_WithMultipleSpacesInHashtags_ShouldNormalize() {
        PostDTO dto = new PostDTO();
        dto.setHashtags("tag1   tag2  tag3");

        Post post = postMapper.toEntity(dto, author);

        assertEquals("tag1,tag2,tag3", post.getHashtags());
    }
}
