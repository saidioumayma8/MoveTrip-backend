package org.budgetmanager.backend.repository;

import org.budgetmanager.backend.model.Caravane;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CaravaneRepository extends JpaRepository<Caravane, Long> {
    List<Caravane> findByIsAvailableTrue(); // Find all available caravanes
    List<Caravane> findByOwnerId(Long ownerId); // Find caravanes by owner
    List<Caravane> findByCityAndIsAvailableTrue(String city); // Search by city
}