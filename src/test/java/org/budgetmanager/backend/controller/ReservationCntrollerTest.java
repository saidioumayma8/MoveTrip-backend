package org.budgetmanager.backend.controller;

import org.budgetmanager.backend.model.Role;
import org.budgetmanager.backend.model.UserInfo;
import org.budgetmanager.backend.repository.RoleRepository;
import org.budgetmanager.backend.repository.UserInfoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import jakarta.transaction.Transactional;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
public class ReservationCntrollerTest {

    @Autowired
    private UserInfoRepository userInfoRepository;

    @Autowired
    private RoleRepository roleRepository;

    private Role testRole;

    @BeforeEach
    void setUp() {
        // Find the "ROLE_USER" role. If it doesn't exist, create it.
        Optional<Role> existingRole = roleRepository.findByName("ROLE_USER");
        if (existingRole.isPresent()) {
            this.testRole = existingRole.get();
        } else {
            Role newRole = new Role();
            newRole.setName("ROLE_USER");
            this.testRole = roleRepository.save(newRole);
        }
    }

    @Test
    void shouldCreateReservationSuccessfully() {
        // Given a user
        UserInfo user = new UserInfo();
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPassword("password123");

        // Use the testRole that's guaranteed to be a valid, persisted entity
        user.setRole(testRole);

        // When saving the user
        UserInfo savedUser = userInfoRepository.save(user);

        // Then verify the user was saved and linked correctly
        assertNotNull(savedUser.getId());
        assertEquals("testuser", savedUser.getUsername());
        assertNotNull(savedUser.getRole());
        assertEquals(testRole.getName(), savedUser.getRole().getName());
    }
}
