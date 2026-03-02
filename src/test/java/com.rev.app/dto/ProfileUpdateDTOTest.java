package com.rev.app.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ProfileUpdateDTOTest {
    @Test
    void testGettersAndSetters() {
        ProfileUpdateDTO dto = new ProfileUpdateDTO();
        dto.setFullName("Full Name");
        dto.setBio("Bio");
        dto.setLocation("Location");
        dto.setWebsite("Website");
        dto.setCategory("Category");
        dto.setContactInfo("Contact");
        dto.setBusinessAddress("Address");
        dto.setBusinessHours("Hours");
        dto.setPrivacySetting("PRIVATE");

        assertEquals("Full Name", dto.getFullName());
        assertEquals("Bio", dto.getBio());
        assertEquals("Location", dto.getLocation());
        assertEquals("Website", dto.getWebsite());
        assertEquals("Category", dto.getCategory());
        assertEquals("Contact", dto.getContactInfo());
        assertEquals("Address", dto.getBusinessAddress());
        assertEquals("Hours", dto.getBusinessHours());
        assertEquals("PRIVATE", dto.getPrivacySetting());
    }
}
