package com.tutoring.Tutorverse.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tutoring.Tutorverse.Dto.EnrollRequestDto;
import com.tutoring.Tutorverse.Dto.ModuelsDto;
import com.tutoring.Tutorverse.Services.EnrollmentService;
import com.tutoring.Tutorverse.Services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(EnrollmentController.class)
class EnrollmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EnrollmentService enrollmentService;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private UUID studentId;
    private UUID moduleId;
    private ModuelsDto moduleDto;
    private EnrollRequestDto enrollRequestDto;

    @BeforeEach
    void setUp() {
        studentId = UUID.randomUUID();
        moduleId = UUID.randomUUID();

        moduleDto = ModuelsDto.builder()
                .moduleId(moduleId)
                .name("Java Programming")
                .fee(100.0)
                .duration(30)
                .build();

        enrollRequestDto = EnrollRequestDto.builder()
                .moduleId(moduleId)
                .build();
    }

    @Test
    void testGetEnrollments_Success() throws Exception {
        // Given
        List<ModuelsDto> enrollments = Arrays.asList(moduleDto);
        when(userService.getUserIdFromRequest(any())).thenReturn(studentId);
        when(enrollmentService.getEnrollmentByStudentId(studentId)).thenReturn(enrollments);

        // When & Then
        mockMvc.perform(get("/api/enrollment/get-enrollments"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("Java Programming"))
                .andExpect(jsonPath("$[0].fee").value(100.0));
    }

    @Test
    void testGetEnrollments_Exception() throws Exception {
        // Given
        when(userService.getUserIdFromRequest(any())).thenReturn(studentId);
        when(enrollmentService.getEnrollmentByStudentId(studentId))
                .thenThrow(new RuntimeException("Database error"));

        // When & Then
        mockMvc.perform(get("/api/enrollment/get-enrollments"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void testEnrollStudent_Success() throws Exception {
        // Given
        when(userService.getUserIdFromRequest(any())).thenReturn(studentId);
        when(userService.isStudent(studentId)).thenReturn(true);
        when(enrollmentService.EnrollTOModule(any())).thenReturn("Enrolled Successfully");

        // When & Then
        mockMvc.perform(post("/api/enrollment/enroll")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(enrollRequestDto)))
                .andExpect(status().isOk())
                .andExpect(content().string("Enrolled Successfully"));
    }

    @Test
    void testEnrollStudent_Unauthorized() throws Exception {
        // Given
        when(userService.getUserIdFromRequest(any())).thenReturn(null);

        // When & Then
        mockMvc.perform(post("/api/enrollment/enroll")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(enrollRequestDto)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Unauthorized or Invalid User"));
    }

    @Test
    void testEnrollStudent_NotStudent() throws Exception {
        // Given
        when(userService.getUserIdFromRequest(any())).thenReturn(studentId);
        when(userService.isStudent(studentId)).thenReturn(false);

        // When & Then
        mockMvc.perform(post("/api/enrollment/enroll")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(enrollRequestDto)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Unauthorized or Invalid User"));
    }

    @Test
    void testEnrollStudent_Exception() throws Exception {
        // Given
        when(userService.getUserIdFromRequest(any())).thenReturn(studentId);
        when(userService.isStudent(studentId)).thenReturn(true);
        when(enrollmentService.EnrollTOModule(any()))
                .thenThrow(new RuntimeException("Enrollment failed"));

        // When & Then
        mockMvc.perform(post("/api/enrollment/enroll")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(enrollRequestDto)))
                .andExpect(status().isInternalServerError());
    }
}
