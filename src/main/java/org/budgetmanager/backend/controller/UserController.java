package org.budgetmanager.backend.controller;
import org.budgetmanager.backend.model.AuthRequest;
import org.budgetmanager.backend.model.UserInfo; // <-- ENSURE THIS IS PRESENT AND CORRECT
import org.budgetmanager.backend.repository.UserInfoRepository;
import org.budgetmanager.backend.repository.CaravaneRepository;
import org.budgetmanager.backend.repository.ReservationRepository;
import org.budgetmanager.backend.service.UserInfoService;
import org.budgetmanager.backend.service.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken; // <-- FIX THIS TYPO
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/auth")
public class UserController {

    private final UserInfoService service;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserInfoRepository userInfoRepository;
    private final CaravaneRepository caravaneRepository;
    private final ReservationRepository reservationRepository;

    @Autowired
    public UserController(UserInfoService service,
                          JwtService jwtService,
                          AuthenticationManager authenticationManager,
                          UserInfoRepository userInfoRepository,
                          CaravaneRepository caravaneRepository,
                          ReservationRepository reservationRepository) {
        this.service = service;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.userInfoRepository = userInfoRepository;
        this.caravaneRepository = caravaneRepository;
        this.reservationRepository = reservationRepository;
    }

    @GetMapping("/welcome")
    public String welcome() {
        return "Welcome this endpoint is not secure";
    }

    @PostMapping("/addNewUser")
    public String addNewUser(@RequestBody UserInfo userInfo) {
        return service.addUser(userInfo);
    }

    @PostMapping("/generateToken")
    public String authenticateAndGetToken(@RequestBody AuthRequest authRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
        );
        if (authentication.isAuthenticated()) {
            return jwtService.generateToken(authRequest.getUsername());
        } else {
            throw new UsernameNotFoundException("Invalid user request!");
        }
    }

    // Matches frontend expectation: returns token, role, and userId
    @PostMapping("/authenticateAndGetRole")
    public java.util.Map<String, Object> authenticateAndGetRole(@RequestBody AuthRequest authRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
        );
        if (!authentication.isAuthenticated()) {
            throw new UsernameNotFoundException("Invalid user request!");
        }

        String token = jwtService.generateToken(authRequest.getUsername());
        UserInfo user = userInfoRepository.findByEmail(authRequest.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        java.util.Map<String, Object> response = new java.util.HashMap<>();
        response.put("token", token);
        response.put("role", user.getRoles());
        response.put("userId", user.getId());
        return response;
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

    @GetMapping("/debug-auth")
    public Map<String, Object> debugAuth() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Map<String, Object> debug = new HashMap<>();
        
        if (auth != null) {
            debug.put("authenticated", auth.isAuthenticated());
            debug.put("principal", auth.getPrincipal().toString());
            debug.put("authorities", auth.getAuthorities().toString());
            debug.put("name", auth.getName());
            
            // Check if user has admin role specifically
            boolean hasAdminRole = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
            debug.put("hasAdminRole", hasAdminRole);
        } else {
            debug.put("authentication", "null");
        }
        
        return debug;
    }
}