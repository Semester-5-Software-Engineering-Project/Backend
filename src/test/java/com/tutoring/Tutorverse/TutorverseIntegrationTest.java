package com.tutoring.Tutorverse;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tutoring.Tutorverse.Dto.EnrollRequestDto;
import com.tutoring.Tutorverse.Dto.ModuelsDto;
import com.tutoring.Tutorverse.Services.EnrollmentService;
import com.tutoring.Tutorverse.Services.ModulesService;
import com.tutoring.Tutorverse.Services.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureTestMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureTestMockMvc
@ActiveProfiles("test")
@Transactional
class TutorverseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @MockBean
    private ModulesService modulesService;

    @MockBean
    private EnrollmentService enrollmentService;

    @Test
    void testFullEnrollmentWorkflow() throws Exception {
        // Given
        UUID studentId = UUID.randomUUID();
        UUID moduleId = UUID.randomUUID();

        ModuelsDto moduleDto = ModuelsDto.builder()
                .moduleId(moduleId)
                .name("Integration Test Module")
                .fee(150.0)
                .duration(45)
                .build();

        EnrollRequestDto enrollRequest = EnrollRequestDto.builder()
                .moduleId(moduleId)
                .build();

        // Mock the services
        when(modulesService.getAllModules()).thenReturn(Arrays.asList(moduleDto));
        when(userService.getUserIdFromRequest(any())).thenReturn(studentId);
        when(userService.isStudent(studentId)).thenReturn(true);
        when(enrollmentService.EnrollTOModule(any())).thenReturn("Enrolled Successfully");
        when(enrollmentService.getEnrollmentByStudentId(studentId)).thenReturn(Arrays.asList(moduleDto));

        // Test 1: Get all modules
        mockMvc.perform(get("/api/modules/get-all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Integration Test Module"));

        // Test 2: Enroll in module
        mockMvc.perform(post("/api/enrollment/enroll")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(enrollRequest)))
                .andExpect(status().isOk())
                .andExpect(content().string("Enrolled Successfully"));

        // Test 3: Get student enrollments
        mockMvc.perform(get("/api/enrollment/get-enrollments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Integration Test Module"));
    }

    @Test
    void testApplicationHealthCheck() throws Exception {
        // Test that the application context loads properly
        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isOk());
    }
}
