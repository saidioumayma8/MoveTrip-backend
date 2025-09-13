package org.budgetmanager.backend.controller;

import org.budgetmanager.backend.model.Caravane;
import org.budgetmanager.backend.model.UserInfo;
import org.budgetmanager.backend.service.CaravaneService;
import org.budgetmanager.backend.repository.UserInfoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CaravaneControllerTest {

    @Mock
    private CaravaneService caravaneService;

    @Mock
    private UserInfoRepository userInfoRepository;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private CaravaneController caravaneController;

    private Caravane testCaravane;
    private UserInfo testUser;

    @BeforeEach
    void setUp() {
        testUser = new UserInfo();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        testUser.setUsername("testuser");

        testCaravane = new Caravane();
        testCaravane.setId(1L);
        testCaravane.setName("Test Caravan");
        testCaravane.setDescription("Test Description");
        testCaravane.setType("Camping-car");
        testCaravane.setCapacity(4);
        testCaravane.setPricePerDay(new BigDecimal("100.0"));
        testCaravane.setCity("Casablanca");
        testCaravane.setAvailable(true);
        testCaravane.setApprovalStatus("APPROVED");
        testCaravane.setOwner(testUser);

        // Mock security context
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("test@example.com");
    }

    @Test
    void shouldGetAllApprovedCaravanes() {
        // Given
        List<Caravane> approvedCaravanes = Arrays.asList(testCaravane);
        when(caravaneService.findByApprovalStatus("APPROVED")).thenReturn(approvedCaravanes);

        // When
        List<Caravane> result = caravaneController.getAll();

        // Then
        assertEquals(1, result.size());
        assertEquals("Test Caravan", result.get(0).getName());
        verify(caravaneService).findByApprovalStatus("APPROVED");
    }

    @Test
    void shouldGetPendingCaravanes() {
        // Given
        List<Caravane> pendingCaravanes = Arrays.asList(testCaravane);
        when(caravaneService.findByApprovalStatus("PENDING")).thenReturn(pendingCaravanes);

        // When
        List<Caravane> result = caravaneController.getPendingCaravanes();

        // Then
        assertEquals(1, result.size());
        verify(caravaneService).findByApprovalStatus("PENDING");
    }

    @Test
    void shouldGetAllCaravanesForAdmin() {
        // Given
        List<Caravane> allCaravanes = Arrays.asList(testCaravane);
        when(caravaneService.findAll()).thenReturn(allCaravanes);

        // When
        List<Caravane> result = caravaneController.getAllCaravanes();

        // Then
        assertEquals(1, result.size());
        verify(caravaneService).findAll();
    }

    @Test
    void shouldGetCaravaneById() {
        // Given
        when(caravaneService.findById(1L)).thenReturn(testCaravane);

        // When
        Caravane result = caravaneController.getById(1L);

        // Then
        assertEquals("Test Caravan", result.getName());
        verify(caravaneService).findById(1L);
    }

    @Test
    void shouldGetCaravanesByOwner() {
        // Given
        List<Caravane> ownerCaravanes = Arrays.asList(testCaravane);
        when(caravaneService.findByOwnerId(1L)).thenReturn(ownerCaravanes);

        // When
        List<Caravane> result = caravaneController.getCaravanesByOwner(1L);

        // Then
        assertEquals(1, result.size());
        verify(caravaneService).findByOwnerId(1L);
    }

    @Test
    void shouldCreateCaravaneSuccessfully() {
        // Given
        when(userInfoRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(caravaneService.save(any(Caravane.class))).thenReturn(testCaravane);

        Caravane newCaravane = new Caravane();
        newCaravane.setName("New Caravan");
        newCaravane.setDescription("New Description");

        // When
        ResponseEntity<Caravane> result = caravaneController.create(newCaravane);

        // Then
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        verify(caravaneService).save(any(Caravane.class));
    }

    @Test
    void shouldFailToCreateCaravaneWhenUserNotFound() {
        // Given
        when(userInfoRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());

        Caravane newCaravane = new Caravane();
        newCaravane.setName("New Caravan");

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            caravaneController.create(newCaravane);
        });
    }

    @Test
    void shouldUpdateCaravane() {
        // Given
        when(caravaneService.save(any(Caravane.class))).thenReturn(testCaravane);

        Caravane updatedCaravane = new Caravane();
        updatedCaravane.setName("Updated Caravan");

        // When
        Caravane result = caravaneController.update(1L, updatedCaravane);

        // Then
        assertEquals(1L, result.getId());
        verify(caravaneService).save(updatedCaravane);
    }

    @Test
    void shouldDeleteCaravane() {
        // Given
        when(caravaneService.findById(1L)).thenReturn(testCaravane);
        doNothing().when(caravaneService).delete(1L);

        // When
        ResponseEntity<Map<String, String>> result = caravaneController.delete(1L);

        // Then
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("Caravan deleted successfully", result.getBody().get("message"));
        verify(caravaneService).delete(1L);
    }

    @Test
    void shouldReturnNotFoundWhenDeletingNonExistentCaravane() {
        // Given
        when(caravaneService.findById(1L)).thenReturn(null);

        // When
        ResponseEntity<Map<String, String>> result = caravaneController.delete(1L);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        verify(caravaneService, never()).delete(1L);
    }

    @Test
    void shouldApproveCaravane() {
        // Given
        testCaravane.setApprovalStatus("PENDING");
        when(caravaneService.findById(1L)).thenReturn(testCaravane);
        when(caravaneService.save(any(Caravane.class))).thenReturn(testCaravane);

        // When
        ResponseEntity<Caravane> result = caravaneController.approveCaravane(1L);

        // Then
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("APPROVED", result.getBody().getApprovalStatus());
        assertTrue(result.getBody().isAvailable());
        verify(caravaneService).save(testCaravane);
    }

    @Test
    void shouldReturnNotFoundWhenApprovingNonExistentCaravane() {
        // Given
        when(caravaneService.findById(1L)).thenReturn(null);

        // When
        ResponseEntity<Caravane> result = caravaneController.approveCaravane(1L);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
    }

    @Test
    void shouldDebugAllCaravanes() {
        // Given
        List<Caravane> allCaravanes = Arrays.asList(testCaravane);
        when(caravaneService.findAll()).thenReturn(allCaravanes);
        when(caravaneService.findByApprovalStatus("APPROVED")).thenReturn(Arrays.asList(testCaravane));
        when(caravaneService.findByApprovalStatus("PENDING")).thenReturn(Arrays.asList());
        when(caravaneService.findByApprovalStatus("REJECTED")).thenReturn(Arrays.asList());

        // When
        ResponseEntity<Map<String, Object>> result = caravaneController.debugAllCaravanes();

        // Then
        assertEquals(HttpStatus.OK, result.getStatusCode());
        Map<String, Object> debug = result.getBody();
        assertEquals(1, debug.get("total"));
        assertEquals(1, debug.get("approved"));
        assertEquals(0, debug.get("pending"));
        assertEquals(0, debug.get("rejected"));
    }

    @Test
    void shouldApproveAllPendingCaravanes() {
        // Given
        Caravane pendingCaravane = new Caravane();
        pendingCaravane.setId(2L);
        pendingCaravane.setApprovalStatus("PENDING");
        
        List<Caravane> pendingCaravanes = Arrays.asList(pendingCaravane);
        when(caravaneService.findByApprovalStatus("PENDING")).thenReturn(pendingCaravanes);
        when(caravaneService.save(any(Caravane.class))).thenReturn(pendingCaravane);

        // When
        ResponseEntity<Map<String, Object>> result = caravaneController.approveAllPendingCaravanes();

        // Then
        assertEquals(HttpStatus.OK, result.getStatusCode());
        Map<String, Object> response = result.getBody();
        assertEquals("Successfully approved all pending caravanes", response.get("message"));
        assertEquals(1, response.get("approvedCount"));
        verify(caravaneService).save(pendingCaravane);
    }

    @Test
    void shouldReturnDebugAllCaravanesWithoutAuth() {
        // Given
        List<Caravane> allCaravanes = Arrays.asList(testCaravane);
        when(caravaneService.findAll()).thenReturn(allCaravanes);

        // When
        List<Caravane> result = caravaneController.debugAllCaravanesNoAuth();

        // Then
        assertEquals(1, result.size());
        assertEquals("Test Caravan", result.get(0).getName());
        verify(caravaneService).findAll();
    }
}