package org.budgetmanager.backend.dto;

import org.budgetmanager.backend.model.Reservation;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

public class ReservationResponse {
    private Long id;
    private Long caravaneId;
    private String caravaneName;
    private String caravaneImageUrl;
    private Long userId;
    private String userName;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal totalPrice;
    private Integer numberOfGuests;
    private String status;
    private String paymentStatus;
    private LocalDateTime createdAt;

    public ReservationResponse(Reservation reservation) {
        this.id = reservation.getId();
        this.startDate = reservation.getStartDate();
        this.endDate = reservation.getEndDate();
        this.totalPrice = reservation.getTotalPrice();
        this.numberOfGuests = reservation.getNumberOfGuests();
        this.status = reservation.getStatus();
        this.paymentStatus = reservation.getPaymentStatus();
        this.createdAt = reservation.getCreatedAt();


        Optional.ofNullable(reservation.getCaravane()).ifPresent(caravane -> {
            this.caravaneId = Long.valueOf(caravane.getId());
            this.caravaneName = caravane.getName();
            if (caravane.getImageUrls() != null && !caravane.getImageUrls().isEmpty()) {
                this.caravaneImageUrl = caravane.getImageUrls().get(0);
            }
        });


        Optional.ofNullable(reservation.getUserInfo()).ifPresent(userInfo -> {
            this.userId = userInfo.getId();
            this.userName = userInfo.getUsername();
        });
    }

    public Long getId() {
        return id;
    }

    public Long getCaravaneId() {
        return caravaneId;
    }

    public String getCaravaneName() {
        return caravaneName;
    }

    public String getCaravaneImageUrl() {
        return caravaneImageUrl;
    }

    public Long getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public Integer getNumberOfGuests() {
        return numberOfGuests;
    }

    public String getStatus() {
        return status;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}