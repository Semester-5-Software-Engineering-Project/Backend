package com.tutoring.Tutorverse.Services;

import com.tutoring.Tutorverse.Dto.EnrollCreateDto;
import com.tutoring.Tutorverse.Dto.ModuelsDto;
import com.tutoring.Tutorverse.Model.*;
import com.tutoring.Tutorverse.Repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EnrollmentServiceTest {

    @Mock
    private EnrollRepository enrollRepository;

    @Mock
    private StudentProfileRepository studentProfileRepository;

    @Mock
    private ModulesRepository moduleRepository;

    @Mock
    private userRepository userRepo;

    @Mock
    private EmailRepository emailRepository;

    @InjectMocks
    private EnrollmentService enrollmentService;

    private UUID studentId;
    private UUID moduleId;
    private UUID enrollmentId;
    private StudentEntity studentEntity;
    private ModuelsEntity moduleEntity;
    private EnrollmentEntity enrollmentEntity;
    private User userEntity;

    @BeforeEach
    void setUp() {
        studentId = UUID.randomUUID();
        moduleId = UUID.randomUUID();
        enrollmentId = UUID.randomUUID();

        // Create mock entities
        studentEntity = new StudentEntity();
        studentEntity.setStudentId(studentId);

        moduleEntity = new ModuelsEntity();
        moduleEntity.setModuleId(moduleId);
        moduleEntity.setName("Java Programming");
        moduleEntity.setFee(BigDecimal.valueOf(100));
        moduleEntity.setDuration(Duration.ofDays(30));

        enrollmentEntity = new EnrollmentEntity();
        enrollmentEntity.setEnrolmentId(enrollmentId);
        enrollmentEntity.setStudent(studentEntity);
        enrollmentEntity.setModule(moduleEntity);

        userEntity = new User();
        userEntity.setUserId(studentId);
        Role role = new Role();
        role.setName("STUDENT");
        userEntity.setRole(role);
    }

    @Test
    void testEnrollToModule_Success() {
        // Given
        EnrollCreateDto enrollCreateDto = EnrollCreateDto.builder()
                .studentId(studentId)
                .moduleId(moduleId)
                .build();

        when(studentProfileRepository.findById(studentId)).thenReturn(Optional.of(studentEntity));
        when(moduleRepository.findById(moduleId)).thenReturn(Optional.of(moduleEntity));
        when(enrollRepository.save(any(EnrollmentEntity.class))).thenReturn(enrollmentEntity);

        // When
        String result = enrollmentService.EnrollTOModule(enrollCreateDto);

        // Then
        assertNotNull(result);
        assertTrue(result.contains("Enrolled Successfully"));
        verify(enrollRepository, times(1)).save(any(EnrollmentEntity.class));
    }

    @Test
    void testEnrollToModule_StudentNotFound() {
        // Given
        EnrollCreateDto enrollCreateDto = EnrollCreateDto.builder()
                .studentId(studentId)
                .moduleId(moduleId)
                .build();

        when(studentProfileRepository.findById(studentId)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> enrollmentService.EnrollTOModule(enrollCreateDto));
        assertEquals("Student not found", exception.getMessage());
    }

    @Test
    void testEnrollToModule_ModuleNotFound() {
        // Given
        EnrollCreateDto enrollCreateDto = EnrollCreateDto.builder()
                .studentId(studentId)
                .moduleId(moduleId)
                .build();

        when(studentProfileRepository.findById(studentId)).thenReturn(Optional.of(studentEntity));
        when(moduleRepository.findById(moduleId)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> enrollmentService.EnrollTOModule(enrollCreateDto));
        assertEquals("Module not found", exception.getMessage());
    }

    @Test
    void testGetEnrollmentByStudentId_Success() {
        // Given
        when(userRepo.findById(studentId)).thenReturn(Optional.of(userEntity));
        when(enrollRepository.findByStudentStudentId(studentId))
                .thenReturn(Arrays.asList(enrollmentEntity));

        // When
        List<ModuelsDto> result = enrollmentService.getEnrollmentByStudentId(studentId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Java Programming", result.get(0).getName());
        verify(enrollRepository, times(1)).findByStudentStudentId(studentId);
    }

    @Test
    void testGetEnrollmentByStudentId_UserNotStudent() {
        // Given
        Role tutorRole = new Role();
        tutorRole.setName("TUTOR");
        userEntity.setRole(tutorRole);

        when(userRepo.findById(studentId)).thenReturn(Optional.of(userEntity));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> enrollmentService.getEnrollmentByStudentId(studentId));
        assertEquals("User is not a student", exception.getMessage());
    }

    @Test
    void testUnenrollFromModule_Success() {
        // When
        enrollmentService.unenrollFromModule(enrollmentId);

        // Then
        verify(enrollRepository, times(1)).deleteById(enrollmentId);
    }

    @Test
    void testIsStudent_ValidStudent() {
        // Given
        when(userRepo.findById(studentId)).thenReturn(Optional.of(userEntity));

        // When
        boolean result = enrollmentService.isStudent(studentId);

        // Then
        assertTrue(result);
    }

    @Test
    void testIsStudent_NotStudent() {
        // Given
        Role tutorRole = new Role();
        tutorRole.setName("TUTOR");
        userEntity.setRole(tutorRole);
        when(userRepo.findById(studentId)).thenReturn(Optional.of(userEntity));

        // When
        boolean result = enrollmentService.isStudent(studentId);

        // Then
        assertFalse(result);
    }

    @Test
    void testIsStudent_UserNotFound() {
        // Given
        when(userRepo.findById(studentId)).thenReturn(Optional.empty());

        // When
        boolean result = enrollmentService.isStudent(studentId);

        // Then
        assertFalse(result);
    }

    @Test
    void testGetStudentEmailsByModuleId_Success() {
        // Given
        List<String> expectedEmails = Arrays.asList("student1@example.com", "student2@example.com");
        when(emailRepository.findEmailsByModuleId(moduleId)).thenReturn(expectedEmails);

        // When
        List<String> result = enrollmentService.getStudentEmailsByModuleId(moduleId);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(expectedEmails, result);
        verify(emailRepository, times(1)).findEmailsByModuleId(moduleId);
    }
}
