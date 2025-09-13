package org.budgetmanager.backend.service;

// FIX: Change this import
// import com.ey.springboot3security.entity.UserInfo;
import org.budgetmanager.backend.model.UserInfo; // <-- CORRECTED IMPORT

import org.budgetmanager.backend.repository.UserInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
    private final PasswordEncoder encoder;

    @Autowired
    public UserInfoService(UserInfoRepository repository, PasswordEncoder encoder) {
        this.repository = repository;
        this.encoder = encoder;
    }

    // Method to load user details by username (email)
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Fetch user from the database by email (username)
        Optional<UserInfo> userInfo = repository.findByEmail(username);

        if (userInfo.isEmpty()) {
            throw new UsernameNotFoundException("User not found with email: " + username);
        }

        // Convert UserInfo to UserDetails (UserInfoDetails) - You have two options here:
        // Option A: If UserInfoDetails is needed for Spring Security context
        // UserInfo user = userInfo.get();
        // return new UserInfoDetails(user); // <-- This is likely what you want if you use UserInfoDetails for auth

        // Option B: If you just want Spring Security's default User object (as currently written)
        UserInfo user = userInfo.get();
        // Note: The User constructor takes username, password, and Collection<? extends GrantedAuthority>
        // You're passing a String for roles, which might cause a type error if roles is not converted.
        // It's better to convert roles to SimpleGrantedAuthority list as done in UserInfoDetails.
        // For now, let's stick to what you have, but be aware of potential runtime issues if roles is not a SimpleGrantedAuthority list.
        return new User(user.getEmail(), user.getPassword(),
                user.getRoles().equals("ROLE_ADMIN") ? List.of(new SimpleGrantedAuthority("ROLE_ADMIN")) : List.of(new SimpleGrantedAuthority("ROLE_USER")));
        // OR if roles can be multiple comma-separated:
        // List.of(user.getRoles().split(",")).stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList())
    }

    // Add any additional methods for registering or managing users
    public String addUser(UserInfo userInfo) {
        // Encrypt password before saving
        userInfo.setPassword(encoder.encode(userInfo.getPassword()));
        repository.save(userInfo);
        return "User added successfully!";
    }
}