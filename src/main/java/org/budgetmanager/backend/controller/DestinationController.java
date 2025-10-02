package org.budgetmanager.backend.controller;

import org.budgetmanager.backend.model.Caravane;
import org.budgetmanager.backend.service.DestinationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/destinations")
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:4201"})
public class DestinationController {
    
    @Autowired
    private DestinationService destinationService;

    @GetMapping
    public List<Caravane> getAllDestinations() {
        return destinationService.getApprovedDestinations();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Caravane> getDestinationById(@PathVariable Long id) {
        Optional<Caravane> destination = destinationService.getDestinationById(id);
        if (destination.isPresent()) {
            return new ResponseEntity<>(destination.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/city/{city}")
    public List<Caravane> getDestinationsByCity(@PathVariable String city) {
        return destinationService.getDestinationsByCity(city);
    }

    @GetMapping("/owner/{ownerId}")
    public List<Caravane> getDestinationsByOwner(@PathVariable Long ownerId) {
        return destinationService.getDestinationsByOwner(ownerId);
    }
}