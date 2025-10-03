package org.budgetmanager.backend.controller;
import org.budgetmanager.backend.model.AuthRequest;
import org.budgetmanager.backend.model.UserInfo;
import org.budgetmanager.backend.model.Role;
import org.budgetmanager.backend.repository.UserInfoRepository;
import org.budgetmanager.backend.repository.CaravaneRepository;
import org.budgetmanager.backend.repository.ReservationRepository;
import org.budgetmanager.backend.repository.RoleRepository;
import org.budgetmanager.backend.service.UserInfoService;
import org.budgetmanager.backend.service.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class UserController {

    private final UserInfoService service;
    private final AuthenticationManager authenticationManager;
    private final UserInfoRepository userInfoRepository;
    private final CaravaneRepository caravaneRepository;
    private final ReservationRepository reservationRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    @Autowired
    public UserController(UserInfoService service,
                          AuthenticationManager authenticationManager,
                          UserInfoRepository userInfoRepository,
                          CaravaneRepository caravaneRepository,
                          ReservationRepository reservationRepository,
                          JwtService jwtService,
                          PasswordEncoder passwordEncoder,
                          RoleRepository roleRepository) {
        this.service = service;
        this.authenticationManager = authenticationManager;
        this.userInfoRepository = userInfoRepository;
        this.caravaneRepository = caravaneRepository;
        this.reservationRepository = reservationRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
    }

    @GetMapping("/welcome")
    public String welcome() {
        return "Welcome this endpoint is not secure";
    }

    @GetMapping("/check-auth")
    public ResponseEntity<Map<String, Object>> checkAuth() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Map<String, Object> response = new HashMap<>();
        
        if (authentication != null && authentication.isAuthenticated() && 
            !"anonymousUser".equals(authentication.getPrincipal())) {
            Optional<UserInfo> userOptional = userInfoRepository.findByEmail(authentication.getName());
            
            if (userOptional.isPresent()) {
                UserInfo user = userOptional.get();
                response.put("authenticated", true);
                response.put("role", user.getRoles());
                response.put("userId", user.getId());
                response.put("username", user.getUsername());
                return ResponseEntity.ok(response);
            }
        }
        
        response.put("authenticated", false);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody UserInfo userInfo) {
        try {
            String result = service.addUser(userInfo);
            Map<String, Object> response = new HashMap<>();
            response.put("message", result);
            response.put("success", true);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            // Email already exists
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            errorResponse.put("success", false);
            return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
        } catch (Exception e) {
            // Other errors
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Registration failed: " + e.getMessage());
            errorResponse.put("success", false);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody AuthRequest authRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
            );
            
            if (authentication.isAuthenticated()) {

                System.out.println("helloooo");

                Optional<UserInfo> userOptional = userInfoRepository.findByEmail(authRequest.getUsername());
                if (userOptional.isEmpty()) {
                    throw new UsernameNotFoundException("User not found");
                }
                UserInfo user = userOptional.get();

                String jwtToken = jwtService.generateToken(user);

                Map<String, Object> response = new HashMap<>();
                response.put("message", "Login successful");
                response.put("role", user.getRoles());
                response.put("userId", user.getId());
                response.put("username", user.getUsername());
                response.put("token", jwtToken);
                return ResponseEntity.ok(response);
            } else {
                throw new UsernameNotFoundException("Invalid user request!");
            }
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Invalid email or password");
            errorResponse.put("success", false);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
    }

    @GetMapping("/stats")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public Map<String, Object> getStats() {
        long users = userInfoRepository.count();
        long caravanes = caravaneRepository.count();
        long reservations = reservationRepository.count();
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("users", users);
        stats.put("caravanes", caravanes);
        stats.put("reservations", reservations);
        return stats;
    }

    @GetMapping("/debug-users")
    public List<Map<String, Object>> debugUsers() {
        List<UserInfo> users = userInfoRepository.findAll();
        return users.stream().map(user -> {
            Map<String, Object> userMap = new HashMap<>();
            userMap.put("id", user.getId());
            userMap.put("username", user.getUsername());
            userMap.put("email", user.getEmail());
            userMap.put("role", user.getRole() != null ? user.getRole().getName() : "No role");
            return userMap;
        }).collect(java.util.stream.Collectors.toList());
    }

    @PostMapping("/reset-test-users")
    @PreAuthorize("permitAll()")
    public ResponseEntity<Map<String, Object>> resetTestUsers() {
        try {
            // Reset password for admin user
            Optional<UserInfo> adminUser = userInfoRepository.findByEmail("admin@example.com");
            if (adminUser.isPresent()) {
                UserInfo admin = adminUser.get();
                admin.setPassword(passwordEncoder.encode("admin123"));
                userInfoRepository.save(admin);
                System.out.println("Reset password for admin user");
            }
            
            // Reset password for regular user
            Optional<UserInfo> regularUser = userInfoRepository.findByEmail("john@example.com");
            if (regularUser.isPresent()) {
                UserInfo user = regularUser.get();
                user.setPassword(passwordEncoder.encode("user123"));
                userInfoRepository.save(user);
                System.out.println("Reset password for regular user");
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Test user passwords reset successfully");
            response.put("success", true);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Failed to reset test user passwords: " + e.getMessage());
            errorResponse.put("success", false);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @PostMapping("/make-admin")
    @PreAuthorize("permitAll()")
    public ResponseEntity<Map<String, Object>> makeAdmin(@RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");
            if (email == null || email.isEmpty()) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("message", "Email is required");
                errorResponse.put("success", false);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            }
            
            Optional<UserInfo> userOptional = userInfoRepository.findByEmail(email);
            if (userOptional.isEmpty()) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("message", "User not found with email: " + email);
                errorResponse.put("success", false);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            }
            
            UserInfo user = userOptional.get();
            
            // Find the ROLE_ADMIN role
            Optional<Role> adminRoleOptional = roleRepository.findByName("ROLE_ADMIN");
            if (adminRoleOptional.isEmpty()) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("message", "ROLE_ADMIN not found in database");
                errorResponse.put("success", false);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
            }
            
            Role adminRole = adminRoleOptional.get();
            user.setRole(adminRole);
            userInfoRepository.save(user);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "User " + email + " has been granted ROLE_ADMIN");
            response.put("success", true);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Failed to make user admin: " + e.getMessage());
            errorResponse.put("success", false);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

}