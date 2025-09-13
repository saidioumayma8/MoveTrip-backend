// File: org/budgetmanager/backend/controller/ReservationCntroller.java

package org.budgetmanager.backend.controller;

import org.budgetmanager.backend.dto.ReservationResponse;
import org.budgetmanager.backend.dto.ReservationRequest; // Make sure this is imported
import org.budgetmanager.backend.model.Caravane;
import org.budgetmanager.backend.model.Reservation;
import org.budgetmanager.backend.model.UserInfo;
import org.budgetmanager.backend.repository.CaravaneRepository;
import org.budgetmanager.backend.repository.ReservationRepository;
import org.budgetmanager.backend.repository.UserInfoRepository;
import org.budgetmanager.backend.service.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.budgetmanager.backend.dto.reservation.UpdateReservationRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/reservations")
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:4201"})
public class ReservationCntroller {

    @Autowired
    private CaravaneRepository caravaneRepository;

    @Autowired
    private UserInfoRepository userInfoRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private ReservationService reservationService;

    // This method is for CREATING a reservation via a POST request
    @PostMapping
    public ResponseEntity<Reservation> createReservation(@RequestBody ReservationRequest reservationRequest) {
        Caravane caravane = caravaneRepository.findById(Long.parseLong(reservationRequest.getCaravaneId()))
                .orElseThrow(() -> new RuntimeException("Caravane not found with ID: " + reservationRequest.getCaravaneId()));

        Long userId = reservationRequest.getUserId();
        if (userId == null) {
            return ResponseEntity.badRequest().build();
        }
        UserInfo userInfo = userInfoRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        // Prevent owner from reserving their own caravane
        if (caravane.getOwner() != null && caravane.getOwner().getId().equals(userInfo.getId())) {
            return ResponseEntity.badRequest().build();
        }

        Reservation reservation = new Reservation();
        reservation.setCaravane(caravane);
        reservation.setUserInfo(userInfo);
        java.time.LocalDate start = java.time.LocalDate.parse(reservationRequest.getStartDate());
        reservation.setStartDate(start);
        reservation.setEndDate(start.plusDays(reservationRequest.getNumberOfDays()));
        reservation.setTotalPrice(reservationRequest.getTotalPrice());
        reservation.setStatus("PENDING");
        reservation.setPaymentStatus("UNPAID");

        Reservation savedReservation = reservationRepository.save(reservation);

        return ResponseEntity.ok(savedReservation);
    }

    // This method is for GETTING a list of reservations
    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<ReservationResponse>> getAllReservations() {
        List<Reservation> reservations = reservationRepository.findAllWithCaravaneAndUserInfo();
        List<ReservationResponse> responseList = reservations.stream()
                .map(ReservationResponse::new)
                .collect(Collectors.toList());

        return ResponseEntity.ok(responseList);
    }

    @GetMapping("/my-reservations")
    @PreAuthorize("hasAuthority('ROLE_USER') or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<ReservationResponse>> getMyReservations() {
        // Get authenticated user's email from security context
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalEmail = authentication.getName();
        
        System.out.println("User " + currentPrincipalEmail + " requesting their reservations");
        
        // Find the user entity from the database using their email
        Optional<UserInfo> userOptional = userInfoRepository.findByEmail(currentPrincipalEmail);
        
        if (userOptional.isPresent()) {
            UserInfo user = userOptional.get();
            List<Reservation> userReservations = reservationRepository.findByUserInfo_Id(user.getId());
            
            System.out.println("Found " + userReservations.size() + " reservations for user " + user.getId());
            
            List<ReservationResponse> responseList = userReservations.stream()
                    .map(ReservationResponse::new)
                    .collect(Collectors.toList());
            
            return ResponseEntity.ok(responseList);
        } else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authenticated user not found.");
        }
    }

    @GetMapping("/pending")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<ReservationResponse>> getPendingReservations() {
        List<Reservation> reservations = reservationRepository.findByStatus("PENDING");
        List<ReservationResponse> responseList = reservations.stream()
                .map(ReservationResponse::new)
                .collect(Collectors.toList());

        return ResponseEntity.ok(responseList);
    }

    @GetMapping("/count")
    public long countAll() {
        return reservationRepository.count();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteReservation(@PathVariable Long id) {
        reservationRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> updateStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String status = body.get("status");
        Reservation r = reservationRepository.findById(id).orElse(null);
        if (r == null || status == null) {
            return ResponseEntity.badRequest().build();
        }
        r.setStatus(status);
        reservationRepository.save(r);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/approve")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> approveReservation(@PathVariable Long id) {
        Reservation r = reservationRepository.findById(id).orElse(null);
        if (r == null) {
            return ResponseEntity.notFound().build();
        }
        r.setStatus("CONFIRMED");
        reservationRepository.save(r);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/reject")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> rejectReservation(@PathVariable Long id) {
        Reservation r = reservationRepository.findById(id).orElse(null);
        if (r == null) {
            return ResponseEntity.notFound().build();
        }
        r.setStatus("CANCELLED");
        reservationRepository.save(r);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Reservation> updateReservation(@PathVariable Long id, @RequestBody UpdateReservationRequest body) {
        Reservation r = reservationRepository.findById(id).orElse(null);
        if (r == null) {
            return ResponseEntity.notFound().build();
        }
        if (body.getStartDate() != null) {
            java.time.LocalDate start = java.time.LocalDate.parse(body.getStartDate());
            r.setStartDate(start);
            if (body.getNumberOfDays() != null) {
                r.setEndDate(start.plusDays(body.getNumberOfDays()));
            }
        }
        if (body.getNumberOfDays() != null && body.getStartDate() == null) {
            r.setEndDate(r.getStartDate().plusDays(body.getNumberOfDays()));
        }
        if (body.getNumberOfGuests() != null) {
            // If you store guests, set it here (not in current model). Skipped.
        }
        reservationRepository.save(r);
        return ResponseEntity.ok(r);
    }
}