package com.tutoring.Tutorverse.Services;

import com.tutoring.Tutorverse.Dto.UserCreateDto;
import com.tutoring.Tutorverse.Model.RoleEntity;
import com.tutoring.Tutorverse.Model.User;
import com.tutoring.Tutorverse.Repository.RoleRepository;
import com.tutoring.Tutorverse.Repository.userRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private userRepository userRepo;

    @Mock
    private RoleRepository roleRepo;

    @Mock
    private RoleService roleService;

    @Mock
    private JwtServices jwtServices;

    @Mock
    private HttpServletRequest httpServletRequest;

    @InjectMocks
    private UserService userService;

    private UUID userId;
    private User user;
    private RoleEntity studentRole;
    private RoleEntity tutorRole;
    private UserCreateDto userCreateDto;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();

        studentRole = new RoleEntity();
        studentRole.setName("STUDENT");

        tutorRole = new RoleEntity();
        tutorRole.setName("TUTOR");

        user = new User();
        user.setUserId(userId);
        user.setEmail("test@example.com");
        user.setName("Test User");
        user.setRole(studentRole);

        userCreateDto = UserCreateDto.builder()
                .email("test@example.com")
                .name("Test User")
                .password("password123")
                .emailVerified(true)
                .providerId("google123")
                .role("STUDENT")
                .build();
    }

    @Test
    void testAddUser_NewUser_Success() {
        // Given
        when(userRepo.existsByEmail("test@example.com")).thenReturn(false);
        when(roleService.findByName("STUDENT")).thenReturn(studentRole);
        when(userRepo.save(any(User.class))).thenReturn(user);

        // When
        Optional<User> result = userService.addUser(userCreateDto);

        // Then
        assertTrue(result.isPresent());
        assertEquals("test@example.com", result.get().getEmail());
        verify(userRepo, times(1)).save(any(User.class));
    }

    @Test
    void testAddUser_ExistingUser_ReturnsExisting() {
        // Given
        when(userRepo.existsByEmail("test@example.com")).thenReturn(true);
        when(userRepo.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        // When
        Optional<User> result = userService.addUser(userCreateDto);

        // Then
        assertTrue(result.isPresent());
        assertEquals("test@example.com", result.get().getEmail());
        verify(userRepo, never()).save(any(User.class));
    }

    @Test
    void testIsTutor_ValidTutor_ReturnsTrue() {
        // Given
        user.setRole(tutorRole);
        when(userRepo.findById(userId)).thenReturn(Optional.of(user));

        // When
        boolean result = userService.isTutor(userId);

        // Then
        assertTrue(result);
    }

    @Test
    void testIsTutor_StudentUser_ReturnsFalse() {
        // Given
        when(userRepo.findById(userId)).thenReturn(Optional.of(user));

        // When
        boolean result = userService.isTutor(userId);

        // Then
        assertFalse(result);
    }

    @Test
    void testIsTutor_UserNotFound_ReturnsFalse() {
        // Given
        when(userRepo.findById(userId)).thenReturn(Optional.empty());

        // When
        boolean result = userService.isTutor(userId);

        // Then
        assertFalse(result);
    }

    @Test
    void testIsStudent_ValidStudent_ReturnsTrue() {
        // Given
        when(userRepo.findById(userId)).thenReturn(Optional.of(user));

        // When
        boolean result = userService.isStudent(userId);

        // Then
        assertTrue(result);
    }

    @Test
    void testIsStudent_TutorUser_ReturnsFalse() {
        // Given
        user.setRole(tutorRole);
        when(userRepo.findById(userId)).thenReturn(Optional.of(user));

        // When
        boolean result = userService.isStudent(userId);

        // Then
        assertFalse(result);
    }

    @Test
    void testGetUserIdFromRequest_ValidToken_ReturnsUserId() {
        // Given
        Cookie[] cookies = {new Cookie("jwt_token", "valid_token")};
        when(httpServletRequest.getCookies()).thenReturn(cookies);
        when(jwtServices.validateJwtToken("valid_token")).thenReturn(true);
        when(jwtServices.getUserIdFromJwtToken("valid_token")).thenReturn(userId);

        // When
        UUID result = userService.getUserIdFromRequest(httpServletRequest);

        // Then
        assertEquals(userId, result);
    }

    @Test
    void testGetUserIdFromRequest_InvalidToken_ReturnsNull() {
        // Given
        Cookie[] cookies = {new Cookie("jwt_token", "invalid_token")};
        when(httpServletRequest.getCookies()).thenReturn(cookies);
        when(jwtServices.validateJwtToken("invalid_token")).thenReturn(false);

        // When
        UUID result = userService.getUserIdFromRequest(httpServletRequest);

        // Then
        assertNull(result);
    }

    @Test
    void testGetUserIdFromRequest_NoCookies_ReturnsNull() {
        // Given
        when(httpServletRequest.getCookies()).thenReturn(null);

        // When
        UUID result = userService.getUserIdFromRequest(httpServletRequest);

        // Then
        assertNull(result);
    }

    @Test
    void testGetUserIdFromRequest_NoJwtCookie_ReturnsNull() {
        // Given
        Cookie[] cookies = {new Cookie("other_cookie", "value")};
        when(httpServletRequest.getCookies()).thenReturn(cookies);

        // When
        UUID result = userService.getUserIdFromRequest(httpServletRequest);

        // Then
        assertNull(result);
    }
}
