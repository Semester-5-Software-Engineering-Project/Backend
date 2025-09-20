package com.tutoring.Tutorverse.util;

import com.tutoring.Tutorverse.Dto.EnrollCreateDto;
import com.tutoring.Tutorverse.Dto.ModuelsDto;
import com.tutoring.Tutorverse.Dto.UserCreateDto;
import com.tutoring.Tutorverse.Model.*;

import java.util.UUID;

/**
 * Utility class for creating test data objects
 */
public class TestDataFactory {

    public static User createTestUser(String role) {
        User user = new User();
        user.setUserId(UUID.randomUUID());
        user.setEmail("test@example.com");
        user.setName("Test User");
        user.setEmailVerified(true);

        RoleEntity roleEntity = new RoleEntity();
        roleEntity.setName(role);
        user.setRole(roleEntity);

        return user;
    }

    public static StudentEntity createTestStudent() {
        StudentEntity student = new StudentEntity();
        student.setStudentId(UUID.randomUUID());
        return student;
    }

    public static ModuelsEntity createTestModule() {
        ModuelsEntity module = new ModuelsEntity();
        module.setModuleId(UUID.randomUUID());
        module.setTutorId(UUID.randomUUID());
        module.setName("Test Module");
        module.setFee(100.0);
        module.setDuration(30);
        module.setStatus(ModuelsEntity.ModuleStatus.Published);
        module.setAverageRatings(4.5);

        DomainEntity domain = new DomainEntity();
        domain.setDomainId(UUID.randomUUID());
        domain.setName("Test Domain");
        module.setDomain(domain);

        return module;
    }

    public static EnrollmentEntity createTestEnrollment() {
        EnrollmentEntity enrollment = new EnrollmentEntity();
        enrollment.setEnrolmentId(UUID.randomUUID());
        enrollment.setStudent(createTestStudent());
        enrollment.setModule(createTestModule());
        return enrollment;
    }

    public static ModuelsDto createTestModuleDto() {
        return ModuelsDto.builder()
                .moduleId(UUID.randomUUID())
                .tutorId(UUID.randomUUID())
                .name("Test Module")
                .domain("Test Domain")
                .fee(100.0)
                .duration(30)
                .status("Published")
                .averageRatings(4.5)
                .build();
    }

    public static EnrollCreateDto createTestEnrollCreateDto() {
        return EnrollCreateDto.builder()
                .studentId(UUID.randomUUID())
                .moduleId(UUID.randomUUID())
                .build();
    }

    public static UserCreateDto createTestUserCreateDto(String role) {
        return UserCreateDto.builder()
                .email("test@example.com")
                .name("Test User")
                .password("password123")
                .emailVerified(true)
                .providerId("test-provider")
                .role(role)
                .build();
    }
}
