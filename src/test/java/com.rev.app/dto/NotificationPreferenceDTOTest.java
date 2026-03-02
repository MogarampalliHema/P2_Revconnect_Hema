package com.rev.app.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class NotificationPreferenceDTOTest {
    @Test
    void testGettersAndSetters() {
        NotificationPreferenceDTO dto = new NotificationPreferenceDTO();
        dto.setConnectionAccepted(false);
        dto.setConnectionRequests(false);
        dto.setNewFollowers(false);
        dto.setPostComments(false);
        dto.setPostLikes(false);
        dto.setPostShares(false);

        assertFalse(dto.isConnectionAccepted());
        assertFalse(dto.isConnectionRequests());
        assertFalse(dto.isNewFollowers());
        assertFalse(dto.isPostComments());
        assertFalse(dto.isPostLikes());
        assertFalse(dto.isPostShares());
    }
}
