package org.budgetmanager.backend.service;

import org.budgetmanager.backend.model.Caravane;
import java.util.List;
import java.util.Optional;

public interface DestinationService {
    List<Caravane> getAllDestinations();
    List<Caravane> getApprovedDestinations();
    Optional<Caravane> getDestinationById(Long id);
    Caravane createDestination(Caravane destination);
    Caravane updateDestination(Long id, Caravane destination);
    void deleteDestination(Long id);
    List<Caravane> getDestinationsByCity(String city);
    List<Caravane> getDestinationsByOwner(Long ownerId);
}