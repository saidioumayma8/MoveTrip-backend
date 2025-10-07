package org.budgetmanager.backend.dto.reservation;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;

// You may need to create a simple CaravaneInfoDTO if you want to nest the caravan data
// But based on your current API structure, we will flatten the fields.
public class ReservationResponse {
    private Long id;
    private Long caravaneId;
    private String caravaneName;
    private String caravaneImageUrl; // 🔑 The field used in your current API response
    private Long userId;
    private String userName; // Assuming this is email/username
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal totalPrice;
    private Integer numberOfGuests;
    private String status;
    private String paymentStatus;
    private LocalDateTime createdAt;

    // --- New/Corrected Fields for Frontend ---
    private Integer numberOfDays;

    // Getters and Setters for all fields (generate them in your IDE)

    // Example Getters/Setters:
    public String getCaravaneImageUrl() { return caravaneImageUrl; }
    public void setCaravaneImageUrl(String caravaneImageUrl) { this.caravaneImageUrl = caravaneImageUrl; }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getCaravaneId() {
        return caravaneId;
    }

    public void setCaravaneId(Long caravaneId) {
        this.caravaneId = caravaneId;
    }

    public String getCaravaneName() {
        return caravaneName;
    }

    public void setCaravaneName(String caravaneName) {
        this.caravaneName = caravaneName;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public Integer getNumberOfGuests() {
        return numberOfGuests;
    }

    public void setNumberOfGuests(Integer numberOfGuests) {
        this.numberOfGuests = numberOfGuests;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Integer getNumberOfDays() {
        if (startDate != null && endDate != null) {
            return (int) (endDate.toEpochDay() - startDate.toEpochDay());
        }
        return numberOfDays;
    }
    public void setNumberOfDays(Integer numberOfDays) { this.numberOfDays = numberOfDays; }
}
