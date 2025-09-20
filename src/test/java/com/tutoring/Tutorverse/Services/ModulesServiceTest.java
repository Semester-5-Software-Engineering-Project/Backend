package com.tutoring.Tutorverse.Services;

import com.tutoring.Tutorverse.Dto.ModuelsDto;
import com.tutoring.Tutorverse.Model.DomainEntity;
import com.tutoring.Tutorverse.Model.ModuelsEntity;
import com.tutoring.Tutorverse.Model.ModuelsEntity.ModuleStatus;
import com.tutoring.Tutorverse.Repository.DomainRepository;
import com.tutoring.Tutorverse.Repository.ModulesRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ModulesServiceTest {

    @Mock
    private ModulesRepository modulesRepository;

    @Mock
    private DomainRepository domainRepository;

    @InjectMocks
    private ModulesService modulesService;

    private UUID moduleId;
    private UUID tutorId;
    private ModuelsEntity moduleEntity;
    private ModuelsDto moduleDto;
    private DomainEntity domainEntity;

    @BeforeEach
    void setUp() {
        moduleId = UUID.randomUUID();
        tutorId = UUID.randomUUID();

        domainEntity = DomainEntity.builder()
                .domainId(UUID.randomUUID())
                .name("Programming")
                .build();

        moduleEntity = new ModuelsEntity();
        moduleEntity.setModuleId(moduleId);
        moduleEntity.setTutorId(tutorId);
        moduleEntity.setName("Java Programming");
        moduleEntity.setDomain(domainEntity);
        moduleEntity.setFee(100.0);
        moduleEntity.setDuration(30);
        moduleEntity.setStatus(ModuleStatus.Published);
        moduleEntity.setAverageRatings(4.5);

        moduleDto = ModuelsDto.builder()
                .moduleId(moduleId)
                .tutorId(tutorId)
                .name("Java Programming")
                .domain("Programming")
                .fee(100.0)
                .duration(30)
                .status("Published")
                .averageRatings(4.5)
                .build();
    }

    @Test
    void testGetAllModules_Success() {
        // Given
        when(modulesRepository.findAll()).thenReturn(Arrays.asList(moduleEntity));

        // When
        List<ModuelsDto> result = modulesService.getAllModules();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Java Programming", result.get(0).getName());
        assertEquals("Programming", result.get(0).getDomain());
        verify(modulesRepository, times(1)).findAll();
    }

    @Test
    void testGetAllModules_Exception_ReturnsEmptyList() {
        // Given
        when(modulesRepository.findAll()).thenThrow(new RuntimeException("Database error"));

        // When
        List<ModuelsDto> result = modulesService.getAllModules();

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testCreateModule_Success() {
        // Given
        when(domainRepository.findAll()).thenReturn(Arrays.asList(domainEntity));
        when(modulesRepository.save(any(ModuelsEntity.class))).thenReturn(moduleEntity);

        // When & Then
        assertDoesNotThrow(() -> modulesService.createModule(moduleDto));
        verify(modulesRepository, times(1)).save(any(ModuelsEntity.class));
    }

    @Test
    void testCreateModule_BlankName_ThrowsException() {
        // Given
        moduleDto.setName("");

        // When & Then
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> modulesService.createModule(moduleDto));
        assertEquals("Module name required", exception.getReason());
    }

    @Test
    void testCreateModule_NullFee_ThrowsException() {
        // Given
        moduleDto.setFee(null);

        // When & Then
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> modulesService.createModule(moduleDto));
        assertEquals("Fee required", exception.getReason());
    }

    @Test
    void testCreateModule_NewDomain_CreatesNewDomain() {
        // Given
        when(domainRepository.findAll()).thenReturn(Arrays.asList());
        when(domainRepository.save(any(DomainEntity.class))).thenReturn(domainEntity);
        when(modulesRepository.save(any(ModuelsEntity.class))).thenReturn(moduleEntity);

        // When
        modulesService.createModule(moduleDto);

        // Then
        verify(domainRepository, times(1)).save(any(DomainEntity.class));
        verify(modulesRepository, times(1)).save(any(ModuelsEntity.class));
    }

    @Test
    void testCreateModule_InvalidStatus_ThrowsException() {
        // Given
        moduleDto.setStatus("INVALID_STATUS");
        when(domainRepository.findAll()).thenReturn(Arrays.asList(domainEntity));

        // When & Then
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> modulesService.createModule(moduleDto));
        assertEquals("Invalid status value", exception.getReason());
    }

    @Test
    void testCreateModule_NullStatus_SetsDefaultDraft() {
        // Given
        moduleDto.setStatus(null);
        when(domainRepository.findAll()).thenReturn(Arrays.asList(domainEntity));
        when(modulesRepository.save(any(ModuelsEntity.class))).thenReturn(moduleEntity);

        // When
        modulesService.createModule(moduleDto);

        // Then
        verify(modulesRepository, times(1)).save(any(ModuelsEntity.class));
    }

    @Test
    void testDeleteModule_Success() {
        // When
        modulesService.deleteModule(moduleId);

        // Then
        verify(modulesRepository, times(1)).deleteById(moduleId);
    }

    @Test
    void testSearchModules_Success() {
        // Given
        String query = "Java";
        when(modulesRepository.findByNameContainingIgnoreCase(query))
                .thenReturn(Arrays.asList(moduleEntity));

        // When
        List<ModuelsDto> result = modulesService.searchModules(query);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Java Programming", result.get(0).getName());
        verify(modulesRepository, times(1)).findByNameContainingIgnoreCase(query);
    }

    @Test
    void testSearchModules_Exception_ReturnsEmptyList() {
        // Given
        String query = "Java";
        when(modulesRepository.findByNameContainingIgnoreCase(query))
                .thenThrow(new RuntimeException("Database error"));

        // When
        List<ModuelsDto> result = modulesService.searchModules(query);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
