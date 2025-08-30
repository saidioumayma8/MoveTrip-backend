package org.budgetmanager.backend.repository;

import org.budgetmanager.backend.model.Itinerary;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ItineraryRepository extends JpaRepository<Itinerary, Long> {
    List<Itinerary> findByTitleContainingIgnoreCase(String title);
    List<Itinerary> findByTagsContaining(String tag); // Search by tag
    List<Itinerary> findByCreatedBy_Id(Long userId); // Find itineraries created by a specific user
}