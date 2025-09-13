package org.budgetmanager.backend.repository;

import org.budgetmanager.backend.model.UserInfo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserInfoRepository extends JpaRepository<UserInfo, Long> {
    Optional<UserInfo> findByEmail(String email); // Use 'email' if that is the correct field for login
    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);
}
