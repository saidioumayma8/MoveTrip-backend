// File: org/budgetmanager/backend/service/ReservationService.java

package org.budgetmanager.backend.service;

import org.budgetmanager.backend.model.Reservation;
import org.budgetmanager.backend.repository.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;

    public List<Reservation> getAllReservationsWithDetails() {
        return reservationRepository.findAllWithCaravaneAndUserInfo();
    }
}