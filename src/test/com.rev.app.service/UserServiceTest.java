package com.rev.app.service;

import com.rev.app.dto.RegisterDTO;
import com.rev.app.entity.NotificationPreference;
import com.rev.app.entity.User;
import com.rev.app.exception.UserAlreadyExistsException;
import com.rev.app.repository.NotificationPreferenceRepository;
import com.rev.app.repository.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private NotificationPreferenceRepository notificationPreferenceRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private com.rev.app.mapper.UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    private RegisterDTO registerDTO;
    private User testUser;

    @Before
    public void setUp() {
        registerDTO = new RegisterDTO();
        registerDTO.setUsername("testuser");
        registerDTO.setEmail("test@example.com");
        registerDTO.setPassword("password123");
        registerDTO.setFullName("Test User");
        registerDTO.setRole(User.UserRole.PERSONAL);

        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("encoded_password");
        testUser.setRole(User.UserRole.PERSONAL);
    }

    @Test
    public void testRegisterNewUser_Success() {
        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(userMapper.toEntity(any(RegisterDTO.class), any(PasswordEncoder.class))).thenReturn(testUser);
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(notificationPreferenceRepository.save(any(NotificationPreference.class)))
                .thenReturn(new NotificationPreference());

        User result = userService.register(registerDTO);

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        verify(userRepository, times(1)).save(any(User.class));
        verify(notificationPreferenceRepository, times(1)).save(any(NotificationPreference.class));
    }

    @Test(expected = UserAlreadyExistsException.class)
    public void testRegisterNewUser_DuplicateUsername() {
        when(userRepository.existsByUsername("testuser")).thenReturn(true);
        userService.register(registerDTO);
    }

    @Test(expected = UserAlreadyExistsException.class)
    public void testRegisterNewUser_DuplicateEmail() {
        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);
        userService.register(registerDTO);
    }

    @Test
    public void testFindByUsername_Found() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        User result = userService.findByUsername("testuser");

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
    }

    @Test
    public void testFindByUsername_NotFound() {
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        try {
            userService.findByUsername("unknown");
            fail("Expected ResourceNotFoundException");
        } catch (Exception e) {
            assertTrue(e.getMessage().contains("unknown"));
        }
    }

    @Test
    public void testRegisterUser_PasswordIsEncoded() {
        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(userMapper.toEntity(any(RegisterDTO.class), any(PasswordEncoder.class))).thenReturn(testUser);
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(notificationPreferenceRepository.save(any())).thenReturn(null);

        userService.register(registerDTO);
        verify(userMapper, times(1)).toEntity(any(RegisterDTO.class), any(PasswordEncoder.class));
    }

    @Test
    public void testRegisterUser_DefaultRoleIsPersonal() {
        registerDTO.setRole(null);
        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);

        User userCapture = new User();
        userCapture.setId(1L);
        userCapture.setRole(User.UserRole.PERSONAL);
        when(userMapper.toEntity(any(RegisterDTO.class), any(PasswordEncoder.class))).thenReturn(userCapture);
        when(userRepository.save(any(User.class))).thenReturn(userCapture);
        when(notificationPreferenceRepository.save(any())).thenReturn(null);

        User result = userService.register(registerDTO);
        assertEquals(User.UserRole.PERSONAL, result.getRole());
    }
}
