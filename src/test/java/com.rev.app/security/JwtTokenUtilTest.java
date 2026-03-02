package com.rev.app.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

public class JwtTokenUtilTest {

    private JwtTokenUtil jwtTokenUtil;
    private UserDetails userDetails;

    @BeforeEach
    public void setUp() {
        jwtTokenUtil = new JwtTokenUtil();
        userDetails = new User("testuser", "password", Collections.emptyList());
    }

    @Test
    public void testGenerateAndValidateToken() {
        String token = jwtTokenUtil.generateToken(userDetails);
        assertThat(token).isNotNull();

        assertThat(jwtTokenUtil.getUsernameFromToken(token)).isEqualTo("testuser");
        assertThat(jwtTokenUtil.validateToken(token, userDetails)).isTrue();
    }

    @Test
    public void testTokenExpiration() {
        String token = jwtTokenUtil.generateToken(userDetails);
        assertThat(jwtTokenUtil.getExpirationDateFromToken(token)).isAfter(new java.util.Date());
    }
}
