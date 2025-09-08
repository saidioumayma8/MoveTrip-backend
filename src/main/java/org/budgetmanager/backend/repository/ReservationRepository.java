// File: org/budgetmanager/backend/repository/ReservationRepository.java

package org.budgetmanager.backend.repository;

import org.budgetmanager.backend.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    @Query("SELECT r FROM Reservation r JOIN FETCH r.caravane JOIN FETCH r.userInfo")
    List<Reservation> findAllWithCaravaneAndUserInfo();
}