package org.budgetmanager.backend.service;


import org.budgetmanager.backend.model.UserInfo;
import org.budgetmanager.backend.model.Role;
import org.budgetmanager.backend.repository.UserInfoRepository;
import org.budgetmanager.backend.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserInfoService implements UserDetailsService {

    private final UserInfoRepository repository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder encoder;

    @Autowired
    public UserInfoService(UserInfoRepository repository, 
                          RoleRepository roleRepository, 
                          @Lazy PasswordEncoder encoder) {
        this.repository = repository;
        this.roleRepository = roleRepository;
        this.encoder = encoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Since the frontend sends email as username, we should look up by email
        Optional<UserInfo> userInfo = repository.findByEmail(username);

        if (userInfo.isEmpty()) {
            throw new UsernameNotFoundException("User not found with email: " + username);
        }

        UserInfo user = userInfo.get();
        // Return a Spring Security User with the email as username, password, and role
        return new User(user.getEmail(), user.getPassword(),
                user.getRoles().equals("ROLE_ADMIN") ? List.of(new SimpleGrantedAuthority("ROLE_ADMIN")) : List.of(new SimpleGrantedAuthority("ROLE_USER")));
    }

    public String addUser(UserInfo userInfo) {
        // Check if email already exists
        if (repository.findByEmail(userInfo.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already exists: " + userInfo.getEmail());
        }
        
        // Hash the password
        userInfo.setPassword(encoder.encode(userInfo.getPassword()));
        
        // Set the username to email for simplicity if username is null
        if (userInfo.getUsername() == null || userInfo.getUsername().trim().isEmpty()) {
            userInfo.setUsername(userInfo.getEmail());
        }
        
        // Set the default role if not already set
        if (userInfo.getRole() == null) {
            Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("Default role ROLE_USER not found"));
            userInfo.setRole(userRole);
        }
        
        repository.save(userInfo);
        return "User added successfully!";
    }
}