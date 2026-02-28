package com.rev.app.service;

import com.rev.app.dto.ProfileUpdateDTO;
import com.rev.app.dto.RegisterDTO;
import com.rev.app.entity.NotificationPreference;
import com.rev.app.entity.User;
import com.rev.app.exception.ResourceNotFoundException;
import com.rev.app.exception.UserAlreadyExistsException;
import com.rev.app.dto.UserSummaryProjection;
import com.rev.app.repository.NotificationPreferenceRepository;
import com.rev.app.repository.UserRepository;
import com.rev.app.repository.UserSpecification;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class UserService {

    private static final Logger logger = LogManager.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final NotificationPreferenceRepository notificationPreferenceRepository;
    private final PasswordEncoder passwordEncoder;
    private final com.rev.app.mapper.UserMapper userMapper;

    public UserService(UserRepository userRepository,
            NotificationPreferenceRepository notificationPreferenceRepository,
            PasswordEncoder passwordEncoder,
            com.rev.app.mapper.UserMapper userMapper) {
        this.userRepository = userRepository;
        this.notificationPreferenceRepository = notificationPreferenceRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
    }

    public User register(RegisterDTO dto) {
        logger.info("Registering new user: {}", dto.getUsername());
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new UserAlreadyExistsException("Username '" + dto.getUsername() + "' is already taken.");
        }
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new UserAlreadyExistsException("Email '" + dto.getEmail() + "' is already registered.");
        }

        User user = userMapper.toEntity(dto, passwordEncoder);
        User saved = userRepository.save(user);

        // Create default notification preferences
        NotificationPreference prefs = new NotificationPreference(saved);
        notificationPreferenceRepository.save(prefs);

        logger.info("User registered successfully with id: {}", saved.getId());
        return saved;
    }

    @Transactional(readOnly = true)
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
    }

    @Transactional(readOnly = true)
    public User findByUsernameOrEmail(String identifier) {
        return userRepository.findByUsernameOrEmail(identifier, identifier)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + identifier));
    }

    public User updateProfile(Long userId, ProfileUpdateDTO dto) {
        logger.info("Updating profile for user id: {}", userId);
        User user = findById(userId);

        if (dto.getFullName() != null)
            user.setFullName(dto.getFullName());
        if (dto.getBio() != null)
            user.setBio(dto.getBio());
        if (dto.getLocation() != null)
            user.setLocation(dto.getLocation());
        if (dto.getWebsite() != null)
            user.setWebsite(dto.getWebsite());
        if (dto.getCategory() != null)
            user.setCategory(dto.getCategory());
        if (dto.getContactInfo() != null)
            user.setContactInfo(dto.getContactInfo());
        if (dto.getBusinessAddress() != null)
            user.setBusinessAddress(dto.getBusinessAddress());
        if (dto.getBusinessHours() != null)
            user.setBusinessHours(dto.getBusinessHours());
        if (dto.getPrivacySetting() != null) {
            user.setPrivacySetting(User.PrivacySetting.valueOf(dto.getPrivacySetting()));
        }

        return userRepository.save(user);
    }

    public String uploadProfilePicture(Long userId, MultipartFile file, String uploadDir) throws IOException {
        User user = findById(userId);
        String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath))
            Files.createDirectories(uploadPath);
        Files.copy(file.getInputStream(), uploadPath.resolve(filename), StandardCopyOption.REPLACE_EXISTING);
        user.setProfilePicture("/uploads/profile-pictures/" + filename);
        userRepository.save(user);
        return user.getProfilePicture();
    }

    // Search using Specification (composable, type-safe)
    @Transactional(readOnly = true)
    public List<User> searchUsers(String query) {
        logger.debug("Searching users with query: {}", query);
        Specification<User> spec = UserSpecification.searchByNameOrUsername(query);
        return userRepository.findAll(Specification.where(spec));
    }

    @Transactional(readOnly = true)
    public List<UserSummaryProjection> findByRole(User.UserRole role) {
        return userRepository.findByRole(role);
    }

    @Transactional(readOnly = true)
    public long countFollowers(Long userId) {
        return userRepository.countFollowers(userId);
    }

    @Transactional(readOnly = true)
    public long countFollowing(Long userId) {
        return userRepository.countFollowing(userId);
    }

    public void deleteUser(Long userId) {
        User user = findById(userId);
        userRepository.delete(user);
    }
}
