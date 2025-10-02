package org.budgetmanager.backend.controller;
import org.budgetmanager.backend.model.AuthRequest;
import org.budgetmanager.backend.model.UserInfo; // <-- ENSURE THIS IS PRESENT AND CORRECT
import org.budgetmanager.backend.repository.UserInfoRepository;
import org.budgetmanager.backend.repository.CaravaneRepository;
import org.budgetmanager.backend.repository.ReservationRepository;
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
import java.util.Map;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/auth")
public class UserController {

    private final UserInfoService service;
    private final AuthenticationManager authenticationManager;
    private final UserInfoRepository userInfoRepository;
    private final CaravaneRepository caravaneRepository;
    private final ReservationRepository reservationRepository;
    private final JwtService jwtService;

    @Autowired
    public UserController(UserInfoService service,
                          AuthenticationManager authenticationManager,
                          UserInfoRepository userInfoRepository,
                          CaravaneRepository caravaneRepository,
                          ReservationRepository reservationRepository,
                          JwtService jwtService) {
        this.service = service;
        this.authenticationManager = authenticationManager;
        this.userInfoRepository = userInfoRepository;
        this.caravaneRepository = caravaneRepository;
        this.reservationRepository = reservationRepository;
        this.jwtService = jwtService;
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
            UserInfo user = userInfoRepository.findByEmail(authentication.getName())
                    .orElse(null);
            
            if (user != null) {
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

                UserInfo user = userInfoRepository.findByEmail(authRequest.getUsername())
                        .orElseThrow(() -> new UsernameNotFoundException("User not found"));

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
}