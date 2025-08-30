package org.budgetmanager.backend.service;

import org.budgetmanager.backend.model.Role;
import org.budgetmanager.backend.model.UserInfo;
import org.budgetmanager.backend.repository.RoleRepository;
import org.budgetmanager.backend.repository.UserInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

@Service
@Primary
public class UserInfoService implements UserDetailsService {

    private final UserInfoRepository repository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder encoder;

    @Autowired
    public UserInfoService(UserInfoRepository repository, RoleRepository roleRepository, PasswordEncoder encoder) {
        this.repository = repository;
        this.roleRepository = roleRepository;
        this.encoder = encoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<UserInfo> userInfoOptional = repository.findByEmail(username);

        if (userInfoOptional.isEmpty()) {
            throw new UsernameNotFoundException("User not found with email: " + username);
        }

        UserInfo user = userInfoOptional.get();

        Collection<? extends GrantedAuthority> authorities = Collections.singletonList(
                new SimpleGrantedAuthority(user.getRole().getName())
        );

        return new User(user.getEmail(), user.getPassword(), authorities);
    }

    @Transactional
    public String addUser(UserInfo userInfo) {
        if (repository.existsByEmail(userInfo.getEmail())) {
            throw new IllegalArgumentException("User with this email already exists.");
        }

        if (repository.existsByUsername(userInfo.getUsername())) {
            throw new IllegalArgumentException("User with this username already exists.");
        }

        Role defaultRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new IllegalStateException("Default role 'ROLE_USER' not found in database."));

        userInfo.setRole(defaultRole);
        userInfo.setPassword(encoder.encode(userInfo.getPassword()));
        repository.save(userInfo);
        return "User added successfully!";
    }

    public UserInfo findById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("User ID must not be null");
        }
        return repository.findById(id).orElse(null);
    }

}

