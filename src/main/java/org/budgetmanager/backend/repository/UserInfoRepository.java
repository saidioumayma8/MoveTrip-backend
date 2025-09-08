package org.budgetmanager.backend.repository;

// FIX: Change this import
// import com.ey.springboot3security.entity.UserInfo;
import org.apache.catalina.User;
import org.budgetmanager.backend.model.UserInfo; // <-- CORRECTED IMPORT

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserInfoRepository extends JpaRepository<UserInfo, Long> {
    Optional<UserInfo> findByEmail(String email);
    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);
}
