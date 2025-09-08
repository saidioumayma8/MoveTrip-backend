package org.budgetmanager.backend.controller;

import org.budgetmanager.backend.model.AuthRequest;
import org.budgetmanager.backend.model.UserInfo;
import org.budgetmanager.backend.service.UserInfoService;
import org.budgetmanager.backend.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:4200")
public class UserController {

    @Autowired
    private UserInfoService service;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private PasswordEncoder encoder;

    public UserController(UserInfoService service, JwtService jwtService, AuthenticationManager authenticationManager) {
        this.service = service;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/addNewUser")
    public ResponseEntity<Void> addNewUser(@RequestBody UserInfo userInfo) {
        try {
            // Encode the password before saving the user
            userInfo.setPassword(encoder.encode(userInfo.getPassword()));
            service.addUser(userInfo);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PostMapping("/authenticateAndGetRole")
    public ResponseEntity<?> authenticateAndGetRole(@RequestBody AuthRequest authRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
        );
        if (authentication.isAuthenticated()) {
            String role = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .findFirst()
                    .orElse("ROLE_USER");

            // Pass the user's role to the JWT service to include it in the token
            String token = jwtService.generateToken(authRequest.getUsername(), role);

            return ResponseEntity.ok(new AuthResponse(token, role));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid user credentials");
        }
    }

    // THIS IS TEMPORARY CODE TO GENERATE A VALID PASSWORD HASH.
    // YOU MUST DELETE THIS METHOD AFTER YOU COMPLETE STEP 3.
    @GetMapping("/generate-hash")
    public String generateHash() {
        String password = "admin123";
        String hash = encoder.encode(password);
        System.out.println("Generated BCrypt Hash for 'admin123': " + hash);
        return "Hash generated. Check console.";
    }

    static class AuthResponse {
        public String token;
        public String role;

        public AuthResponse(String token, String role) {
            this.token = token;
            this.role = role;
        }
    }
}
