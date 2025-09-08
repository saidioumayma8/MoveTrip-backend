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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/reservations")
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

        UserInfo userInfo = new UserInfo();
        userInfo.setId(1L);

        Reservation reservation = new Reservation();
        reservation.setCaravane(caravane);
        reservation.setUserInfo(userInfo);
        reservation.setStartDate(reservationRequest.getStartDate());
        reservation.setEndDate(reservationRequest.getStartDate().plusDays(reservationRequest.getNumberOfDays()));
        reservation.setTotalPrice(reservationRequest.getTotalPrice());
        reservation.setStatus("PENDING");
        reservation.setPaymentStatus("UNPAID");

        Reservation savedReservation = reservationRepository.save(reservation);

        return ResponseEntity.ok(savedReservation);
    }

    // This method is for GETTING a list of reservations
    @GetMapping
    public ResponseEntity<List<ReservationResponse>> getAllReservations() {
        List<Reservation> reservations = reservationRepository.findAllWithCaravaneAndUserInfo();
        List<ReservationResponse> responseList = reservations.stream()
                .map(ReservationResponse::new)
                .collect(Collectors.toList());

        return ResponseEntity.ok(responseList);
    }
}