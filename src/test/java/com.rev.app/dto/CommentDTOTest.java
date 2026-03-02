package com.rev.app.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CommentDTOTest {
    @Test
    void testGettersAndSetters() {
        CommentDTO dto = new CommentDTO();
        dto.setContent("Test comment");
        assertEquals("Test comment", dto.getContent());
    }
}
