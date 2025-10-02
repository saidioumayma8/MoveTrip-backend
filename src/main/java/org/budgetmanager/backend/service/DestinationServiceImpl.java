package org.budgetmanager.backend.service;

import org.budgetmanager.backend.model.Caravane;
import org.budgetmanager.backend.repository.CaravaneRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DestinationServiceImpl implements DestinationService {
    
    @Autowired
    private CaravaneRepository caravaneRepository;

    @Override
    public List<Caravane> getAllDestinations() {
        return (List<Caravane>) caravaneRepository.findAll();
    }

    @Override
    public List<Caravane> getApprovedDestinations() {
        return caravaneRepository.findByApprovalStatus("APPROVED");
    }

    @Override
    public Optional<Caravane> getDestinationById(Long id) {
        return caravaneRepository.findById(id);
    }

    @Override
    public Caravane createDestination(Caravane destination) {
        return caravaneRepository.save(destination);
    }

    @Override
    public Caravane updateDestination(Long id, Caravane destination) {
        destination.setId(id);
        return caravaneRepository.save(destination);
    }

    @Override
    public void deleteDestination(Long id) {
        caravaneRepository.deleteById(id);
    }

    @Override
    public List<Caravane> getDestinationsByCity(String city) {
        return caravaneRepository.findByCity(city);
    }

    @Override
    public List<Caravane> getDestinationsByOwner(Long ownerId) {
        return caravaneRepository.findByOwner_Id(ownerId);
    }
}