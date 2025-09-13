package org.budgetmanager.backend.controller;

import org.budgetmanager.backend.model.AuthRequest;
import org.budgetmanager.backend.model.UserInfo;
import org.budgetmanager.backend.model.Role;
import org.budgetmanager.backend.repository.UserInfoRepository;
import org.budgetmanager.backend.repository.CaravaneRepository;
import org.budgetmanager.backend.repository.ReservationRepository;
import org.budgetmanager.backend.service.UserInfoService;
import org.budgetmanager.backend.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserInfoService userInfoService;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserInfoRepository userInfoRepository;

    @Mock
    private CaravaneRepository caravaneRepository;

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private UserController userController;

    private Role userRole;
    private Role adminRole;
    private UserInfo testUser;
    private UserInfo testAdmin;
    private AuthRequest authRequest;

    @BeforeEach
    void setUp() {
        userRole = new Role("ROLE_USER");
        adminRole = new Role("ROLE_ADMIN");

        testUser = new UserInfo();
        testUser.setId(1L);
        testUser.setEmail("user@example.com");
        testUser.setUsername("testuser");
        testUser.setRole(userRole);

        testAdmin = new UserInfo();
        testAdmin.setId(2L);
        testAdmin.setEmail("admin@example.com");
        testAdmin.setUsername("admin");
        testAdmin.setRole(adminRole);

        authRequest = new AuthRequest();
        authRequest.setUsername("user@example.com");
        authRequest.setPassword("password123");

        // Mock security context
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
    }

    @Test
    void shouldReturnWelcomeMessage() {
        // When
        String result = userController.welcome();

        // Then
        assertEquals("Welcome this endpoint is not secure", result);
    }

    @Test
    void shouldAddNewUser() {
        // Given
        when(userInfoService.addUser(any(UserInfo.class))).thenReturn("User added successfully!");

        // When
        String result = userController.addNewUser(testUser);

        // Then
        assertEquals("User added successfully!", result);
        verify(userInfoService).addUser(testUser);
    }

    @Test
    void shouldGenerateTokenForValidUser() {
        // Given
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(jwtService.generateToken("user@example.com")).thenReturn("jwt-token");

        // When
        String result = userController.authenticateAndGetToken(authRequest);

        // Then
        assertEquals("jwt-token", result);
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtService).generateToken("user@example.com");
    }

    @Test
    void shouldThrowExceptionForInvalidCredentials() {
        // Given
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(false);

        // When & Then
        assertThrows(UsernameNotFoundException.class, () -> {
            userController.authenticateAndGetToken(authRequest);
        });
    }

    @Test
    void shouldAuthenticateAndGetRoleForUser() {
        // Given
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(jwtService.generateToken("user@example.com")).thenReturn("jwt-token");
        when(userInfoRepository.findByEmail("user@example.com")).thenReturn(Optional.of(testUser));

        // When
        Map<String, Object> result = userController.authenticateAndGetRole(authRequest);

        // Then
        assertEquals("jwt-token", result.get("token"));
        assertEquals("ROLE_USER", result.get("role"));
        assertEquals(1L, result.get("userId"));
    }

    @Test
    void shouldAuthenticateAndGetRoleForAdmin() {
        // Given
        authRequest.setUsername("admin@example.com");
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(jwtService.generateToken("admin@example.com")).thenReturn("admin-jwt-token");
        when(userInfoRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(testAdmin));

        // When
        Map<String, Object> result = userController.authenticateAndGetRole(authRequest);

        // Then
        assertEquals("admin-jwt-token", result.get("token"));
        assertEquals("ROLE_ADMIN", result.get("role"));
        assertEquals(2L, result.get("userId"));
    }

    @Test
    void shouldThrowExceptionWhenUserNotFoundInDatabase() {
        // Given
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(jwtService.generateToken("user@example.com")).thenReturn("jwt-token");
        when(userInfoRepository.findByEmail("user@example.com")).thenReturn(Optional.empty());

        // When & Then
        assertThrows(UsernameNotFoundException.class, () -> {
            userController.authenticateAndGetRole(authRequest);
        });
    }

    @Test
    void shouldGetStatsForAdmin() {
        // Given
        when(userInfoRepository.count()).thenReturn(10L);
        when(caravaneRepository.count()).thenReturn(5L);
        when(reservationRepository.count()).thenReturn(15L);

        // When
        Map<String, Object> result = userController.getStats();

        // Then
        assertEquals(10L, result.get("users"));
        assertEquals(5L, result.get("caravanes"));
        assertEquals(15L, result.get("reservations"));
    }

    @Test
    void shouldDebugAuthentication() {
        // Given
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn("admin@example.com");
        when(authentication.getName()).thenReturn("admin@example.com");
        when(authentication.getAuthorities()).thenReturn(
                (Collection<? extends GrantedAuthority>) List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
        );

        // When
        Map<String, Object> result = userController.debugAuth();

        // Then
        assertTrue((Boolean) result.get("authenticated"));
        assertEquals("admin@example.com", result.get("principal"));
        assertEquals("[ROLE_ADMIN]", result.get("authorities"));
        assertEquals("admin@example.com", result.get("name"));
        assertTrue((Boolean) result.get("hasAdminRole"));
    }

    @Test
    void shouldDebugAuthenticationWithNullAuth() {
        // Given
        when(securityContext.getAuthentication()).thenReturn(null);

        // When
        Map<String, Object> result = userController.debugAuth();

        // Then
        assertEquals("null", result.get("authentication"));
    }

    @Test
    void shouldDebugAuthenticationWithUserRole() {
        // Given
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn("user@example.com");
        when(authentication.getName()).thenReturn("user@example.com");
        when(authentication.getAuthorities()).thenReturn(
                (Collection<? extends GrantedAuthority>) List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );

        // When
        Map<String, Object> result = userController.debugAuth();

        // Then
        assertTrue((Boolean) result.get("authenticated"));
        assertFalse((Boolean) result.get("hasAdminRole"));
    }

    @Test
    void shouldHandleAuthenticationFailure() {
        // Given
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new RuntimeException("Authentication failed"));

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            userController.authenticateAndGetRole(authRequest);
        });
    }
}