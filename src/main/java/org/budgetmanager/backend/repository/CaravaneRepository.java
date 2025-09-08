package org.budgetmanager.backend.repository;

import org.budgetmanager.backend.model.Caravane;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CaravaneRepository extends JpaRepository<Caravane, Long> {
    List<Caravane> findByIsAvailableTrue();
    List<Caravane> findByOwnerId(Long ownerId);
    List<Caravane> findByCityAndIsAvailableTrue(String city);
}