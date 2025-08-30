package org.budgetmanager.backend.repository;

import org.budgetmanager.backend.model.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LocationRepository extends JpaRepository<Location, Long> {
    List<Location> findByType(String type); // e.g., "Camping", "Spot Sauvage"
    List<Location> findByCity(String city);
    List<Location> findByRegion(String region);
}