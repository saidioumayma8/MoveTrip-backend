package org.budgetmanager.backend.config;

import org.budgetmanager.backend.model.Caravane;
import org.budgetmanager.backend.model.Reservation;
import org.budgetmanager.backend.model.Role;
import org.budgetmanager.backend.model.UserInfo;
import org.budgetmanager.backend.repository.CaravaneRepository;
import org.budgetmanager.backend.repository.ReservationRepository;
import org.budgetmanager.backend.repository.RoleRepository;
import org.budgetmanager.backend.repository.UserInfoRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner seedInitialData(
            RoleRepository roleRepository,
            UserInfoRepository userRepository,
            CaravaneRepository caravaneRepository,
            ReservationRepository reservationRepository,
            PasswordEncoder passwordEncoder
    ) {
        return args -> {
            // Seed roles
            Role roleAdmin = roleRepository.findByName("ROLE_ADMIN").orElseGet(() -> {
                Role r = new Role();
                r.setName("ROLE_ADMIN");
                return roleRepository.save(r);
            });
            Role roleUser = roleRepository.findByName("ROLE_USER").orElseGet(() -> {
                Role r = new Role();
                r.setName("ROLE_USER");
                return roleRepository.save(r);
            });

            // Seed users if none
            if (userRepository.count() == 0) {
                UserInfo admin = new UserInfo();
                admin.setUsername("admin");
                admin.setEmail("admin@example.com");
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setRole(roleAdmin);
                userRepository.save(admin);

                UserInfo user = new UserInfo();
                user.setUsername("john");
                user.setEmail("john@example.com");
                user.setPassword(passwordEncoder.encode("user123"));
                user.setRole(roleUser);
                userRepository.save(user);
            }

            // Seed caravanes if none
            if (caravaneRepository.count() == 0) {
                UserInfo owner = userRepository.findAll().stream().findFirst().orElse(null);
                if (owner != null) {
                    Caravane c1 = new Caravane();
                    c1.setName("Nomad One");
                    c1.setDescription("Compact caravane perfect for couples");
                    c1.setType("Mini");
                    c1.setCapacity(2);
                    c1.setPricePerDay(new BigDecimal("49.99"));
                    c1.setImageUrls(Collections.singletonList("/assets/img/img.png"));
                    c1.setCity("Marrakech");
                    c1.setAvailable(true);
                    c1.setOwner(owner);
                    c1.setApprovalStatus("APPROVED"); // Auto-approve seeded caravans

                    Caravane c2 = new Caravane();
                    c2.setName("Explorer XL");
                    c2.setDescription("Spacious caravane perfect for families");
                    c2.setType("Family");
                    c2.setCapacity(4);
                    c2.setPricePerDay(new BigDecimal("89.99"));
                    c2.setImageUrls(Collections.singletonList("/assets/img/img_1.png"));
                    c2.setCity("Agadir");
                    c2.setAvailable(true);
                    c2.setOwner(owner);
                    c2.setApprovalStatus("APPROVED"); // Auto-approve seeded caravans

                    caravaneRepository.saveAll(Arrays.asList(c1, c2));
                }
            }

            // Seed reservations if none
            if (reservationRepository.count() == 0) {
                UserInfo anyUser = userRepository.findAll().stream().filter(u -> "ROLE_USER".equals(u.getRole().getName())).findFirst().orElse(null);
                Caravane anyCar = caravaneRepository.findAll().stream().findFirst().orElse(null);
                if (anyUser != null && anyCar != null) {
                    Reservation r = new Reservation();
                    r.setUserInfo(anyUser);
                    r.setCaravane(anyCar);
                    r.setStartDate(LocalDate.now().plusDays(1));
                    r.setEndDate(LocalDate.now().plusDays(4));
                    r.setTotalPrice(new BigDecimal("199.99"));
                    r.setStatus("PENDING");
                    r.setPaymentStatus("UNPAID");
                    reservationRepository.save(r);
                }
            }
        };
    }
}

