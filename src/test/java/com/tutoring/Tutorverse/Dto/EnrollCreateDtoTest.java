package com.tutoring.Tutorverse.Dto;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class EnrollCreateDtoTest {

    @Test
    void testEnrollCreateDtoBuilder() {
        // Given
        UUID studentId = UUID.randomUUID();
        UUID moduleId = UUID.randomUUID();

        // When
        EnrollCreateDto dto = EnrollCreateDto.builder()
                .studentId(studentId)
                .moduleId(moduleId)
                .build();

        // Then
        assertNotNull(dto);
        assertEquals(studentId, dto.getStudentId());
        assertEquals(moduleId, dto.getModuleId());
    }

    @Test
    void testEnrollCreateDtoEqualsAndHashCode() {
        // Given
        UUID studentId = UUID.randomUUID();
        UUID moduleId = UUID.randomUUID();

        EnrollCreateDto dto1 = EnrollCreateDto.builder()
                .studentId(studentId)
                .moduleId(moduleId)
                .build();

        EnrollCreateDto dto2 = EnrollCreateDto.builder()
                .studentId(studentId)
                .moduleId(moduleId)
                .build();

        // Then
        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    void testEnrollCreateDtoToString() {
        // Given
        UUID studentId = UUID.randomUUID();
        UUID moduleId = UUID.randomUUID();

        EnrollCreateDto dto = EnrollCreateDto.builder()
                .studentId(studentId)
                .moduleId(moduleId)
                .build();

        // When
        String toString = dto.toString();

        // Then
        assertNotNull(toString);
        assertTrue(toString.contains("EnrollCreateDto"));
        assertTrue(toString.contains(studentId.toString()));
        assertTrue(toString.contains(moduleId.toString()));
    }

    @Test
    void testEnrollCreateDtoGettersAndSetters() {
        // Given
        UUID studentId = UUID.randomUUID();
        UUID moduleId = UUID.randomUUID();
        EnrollCreateDto dto = new EnrollCreateDto();

        // When
        dto.setStudentId(studentId);
        dto.setModuleId(moduleId);

        // Then
        assertEquals(studentId, dto.getStudentId());
        assertEquals(moduleId, dto.getModuleId());
    }
}
