package org.budgetmanager.backend.repository;

import org.budgetmanager.backend.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByUserInfo_Id(Long userId); // Get all reservations for a specific user
    List<Reservation> findByCaravane_Id(Long caravaneId); // Get all reservations for a specific caravane
    List<Reservation> findByStatus(String status); // Find reservations by status
    List<Reservation> findByCaravane_IdAndEndDateGreaterThanEqualAndStartDateLessThanEqual(Long caravaneId, LocalDate startDate, LocalDate endDate);
}