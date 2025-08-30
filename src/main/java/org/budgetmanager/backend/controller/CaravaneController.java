package org.budgetmanager.backend.controller;

import org.budgetmanager.backend.model.Caravane;
import org.budgetmanager.backend.model.UserInfo;
import org.budgetmanager.backend.service.CaravaneService;
import org.budgetmanager.backend.service.UserInfoService;
import org.budgetmanager.backend.dto.caravane.CaravaneRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/caravanes")
@CrossOrigin(origins = "http://localhost:4200")
public class CaravaneController {

    @Autowired
    private CaravaneService service;

    @Autowired
    private UserInfoService userInfoService;

    @GetMapping
    public List<Caravane> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public Caravane getById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping
    public Caravane create(@RequestBody CaravaneRequest request) {
        // 🔹 Vérifier que l'ID du propriétaire est présent
        if (request.getOwnerId() == null) {
            throw new IllegalArgumentException("Owner ID must not be null");
        }

        // 🔹 Récupérer le propriétaire
        UserInfo owner = userInfoService.findById(request.getOwnerId());
        if (owner == null) {
            throw new RuntimeException("Owner not found with id: " + request.getOwnerId());
        }

        // 🔹 Créer la caravane
        Caravane c = new Caravane();
        c.setName(request.getName());
        c.setDescription(request.getDescription());
        c.setType(request.getType());
        c.setCapacity(request.getCapacity());
        c.setPricePerDay(request.getPricePerDay());
        c.setImageUrls(request.getImageUrls());
        c.setCity(request.getCity());
        c.setAvailable(request.isAvailable());
        c.setOwner(owner);

        return service.save(c);
    }


    @PutMapping("/{id}")
    public Caravane update(@PathVariable Long id, @RequestBody CaravaneRequest request) {
        Caravane c = service.findById(id);
        if (c == null) {
            throw new RuntimeException("Caravane not found with id: " + id);
        }

        UserInfo owner = userInfoService.findById(request.getOwnerId());
        if (owner == null) {
            throw new RuntimeException("Owner not found with id: " + request.getOwnerId());
        }

        c.setName(request.getName());
        c.setDescription(request.getDescription());
        c.setType(request.getType());
        c.setCapacity(request.getCapacity());
        c.setPricePerDay(request.getPricePerDay());
        c.setImageUrls(request.getImageUrls());
        c.setCity(request.getCity());
        c.setAvailable(request.isAvailable());
        c.setOwner(owner);

        return service.save(c);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
