package org.budgetmanager.backend.controller;

import org.budgetmanager.backend.model.Caravane;
import org.budgetmanager.backend.model.UserInfo;
import org.budgetmanager.backend.service.CaravaneService;
import org.budgetmanager.backend.repository.UserInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Optional;

@RestController
@RequestMapping("/api/caravanes")
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:4201"})
public class CaravaneController {
    @Autowired
    private CaravaneService service;

    @Autowired
    private UserInfoRepository userInfoRepository;

    @GetMapping
    public List<Caravane> getAll() {

        List<Caravane> approved = service.findByApprovalStatus("APPROVED");
        System.out.println("Public caravan request - Found " + approved.size() + " approved caravanes");


        List<Caravane> allCaravanes = (List<Caravane>) service.findAll();
        System.out.println("Debug: Total caravanes in database: " + allCaravanes.size());

        return approved;
    }

    @PostMapping("/approve-all-existing")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Map<String, Object>> approveAllExistingCaravanes() {
        List<Caravane> allCaravanes = (List<Caravane>) service.findAll();
        int approvedCount = 0;

        for (Caravane caravane : allCaravanes) {
            if (!"APPROVED".equals(caravane.getApprovalStatus())) {
                caravane.setApprovalStatus("APPROVED");
                caravane.setAvailable(true);
                service.save(caravane);
                approvedCount++;
                System.out.println("Approved caravan: " + caravane.getName());
            }
        }

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Approved all existing caravanes");
        response.put("approvedCount", approvedCount);

        System.out.println("Approved " + approvedCount + " existing caravanes");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/pending")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public List<Caravane> getPendingCaravanes() {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("Admin endpoint - Authentication: " + auth);
        System.out.println("Admin endpoint - Principal: " + auth.getPrincipal());
        System.out.println("Admin endpoint - Authorities: " + auth.getAuthorities());

        List<Caravane> pending = service.findByApprovalStatus("PENDING");
        System.out.println("Admin requested pending caravanes. Found: " + pending.size() + " items");
        for (Caravane c : pending) {
            System.out.println("  - ID: " + c.getId() + ", Name: " + c.getName() + ", Status: " + c.getApprovalStatus());
        }
        return pending;
    }

    @GetMapping("/all")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public List<Caravane> getAllCaravanes() {
        return (List<Caravane>) service.findAll();
    }

    @GetMapping("/{id}")
    public Caravane getById(@PathVariable Long id) {
        return service.findById(id);
    }

    @GetMapping("/owner/{ownerId}")
    public List<Caravane> getCaravanesByOwner(@PathVariable Long ownerId) {
        return service.findByOwnerId(ownerId);
    }

    @GetMapping("/my-caravanes")
    // FIX: Changed hasRole('ROLE_USER') to hasAuthority('ROLE_USER')
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public List<Caravane> getMyCaravanes() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalEmail = authentication.getName();

        Optional<UserInfo> userOptional = userInfoRepository.findByEmail(currentPrincipalEmail);
        if (userOptional.isPresent()) {
            UserInfo user = userOptional.get();
            return service.findByOwnerId(user.getId());
        } else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found.");
        }
    }

    @PostMapping
    // FIX: Changed hasRole('ROLE_USER') to hasAuthority('ROLE_USER')
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<Caravane> create(@RequestBody Caravane caravane) {
        System.out.println("Creating new caravane: " + caravane.getName());
        System.out.println("Initial approval status: " + caravane.getApprovalStatus());


        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalEmail = authentication.getName();
        System.out.println("Authenticated user: " + currentPrincipalEmail);


        Optional<UserInfo> ownerOptional = userInfoRepository.findByEmail(currentPrincipalEmail);

        if (ownerOptional.isPresent()) {
            UserInfo owner = ownerOptional.get();

            caravane.setOwner(owner);


            caravane.setApprovalStatus("PENDING");
            caravane.setAvailable(false);

            Caravane savedCaravane = service.save(caravane);
            System.out.println("Caravane saved with ID: " + savedCaravane.getId() + ", Status: " + savedCaravane.getApprovalStatus());

            return new ResponseEntity<>(savedCaravane, HttpStatus.CREATED);
        } else {

            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authenticated user not found.");
        }
    }

    @PutMapping("/{id}")
    // FIX: Changed hasRole('ROLE_USER') to hasAuthority('ROLE_USER')
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public Caravane update(@PathVariable Long id, @RequestBody Caravane c) {
        c.setId(id);
        return service.save(c);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Map<String, String>> delete(@PathVariable Long id) {
        Caravane caravane = service.findById(id);
        if (caravane == null) {
            return ResponseEntity.notFound().build();
        }

        System.out.println("Admin deleting caravan: " + caravane.getName() + " (ID: " + id + ")");
        service.delete(id);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Caravan deleted successfully");
        response.put("deletedCaravan", caravane.getName());

        return ResponseEntity.ok(response);
    }

    // Admin approval endpoints
    @PatchMapping("/{id}/approve")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Caravane> approveCaravane(@PathVariable Long id) {
        Caravane caravane = service.findById(id);
        if (caravane == null) {
            return ResponseEntity.notFound().build();
        }
        caravane.setApprovalStatus("APPROVED");
        caravane.setAvailable(true);
        Caravane updated = service.save(caravane);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/debug-all")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Map<String, Object>> debugAllCaravanes() {
        List<Caravane> allCaravanes = (List<Caravane>) service.findAll();
        Map<String, Object> debug = new HashMap<>();

        debug.put("total", allCaravanes.size());
        debug.put("approved", service.findByApprovalStatus("APPROVED").size());
        debug.put("pending", service.findByApprovalStatus("PENDING").size());
        debug.put("rejected", service.findByApprovalStatus("REJECTED").size());

        List<Map<String, Object>> details = new ArrayList<>();
        for (Caravane c : allCaravanes) {
            Map<String, Object> detail = new HashMap<>();
            detail.put("id", c.getId());
            detail.put("name", c.getName());
            detail.put("status", c.getApprovalStatus());
            detail.put("available", c.isAvailable());
            details.add(detail);
        }
        debug.put("details", details);

        System.out.println("Debug: Total caravanes: " + allCaravanes.size());
        return ResponseEntity.ok(debug);
    }

    @PatchMapping("/approve-all-pending")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Map<String, Object>> approveAllPendingCaravanes() {
        List<Caravane> pendingCaravanes = service.findByApprovalStatus("PENDING");
        int approvedCount = 0;

        for (Caravane caravane : pendingCaravanes) {
            caravane.setApprovalStatus("APPROVED");
            caravane.setAvailable(true);
            service.save(caravane);
            approvedCount++;
        }

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Successfully approved all pending caravanes");
        response.put("approvedCount", approvedCount);

        return ResponseEntity.ok(response);
    }


    @PatchMapping("/{id}/reject")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Caravane> rejectCaravane(@PathVariable Long id) {
        Caravane caravane = service.findById(id);
        if (caravane == null) {
            return ResponseEntity.notFound().build();
        }
        caravane.setApprovalStatus("REJECTED");
        caravane.setAvailable(false);
        Caravane updated = service.save(caravane);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/debug-all-no-auth")
    public List<Caravane> debugAllCaravanesNoAuth() {
        List<Caravane> all = (List<Caravane>) service.findAll();
        System.out.println("DEBUG: Found " + all.size() + " total caravanes");
        for (Caravane c : all) {
            System.out.println("  - ID: " + c.getId() + ", Name: " + c.getName() + ", Status: " + c.getApprovalStatus());
        }
        return all;
    }

    @PostMapping("/approve-all-simple")
    public ResponseEntity<String> approveAllSimple() {
        List<Caravane> allCaravanes = (List<Caravane>) service.findAll();
        int approvedCount = 0;

        for (Caravane caravane : allCaravanes) {
            if (caravane.getApprovalStatus() == null ||
                    caravane.getApprovalStatus().trim().isEmpty() ||
                    "PENDING".equals(caravane.getApprovalStatus())) {
                caravane.setApprovalStatus("APPROVED");
                caravane.setAvailable(true);
                service.save(caravane);
                approvedCount++;
            }
        }

        return ResponseEntity.ok("Approved " + approvedCount + " caravanes successfully!");
    }
}