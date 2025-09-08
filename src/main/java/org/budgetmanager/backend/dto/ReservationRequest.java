package org.budgetmanager.backend.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ReservationRequest {
    private String caravaneId;
    private LocalDate startDate;
    private int numberOfGuests;
    private int numberOfDays;
    private BigDecimal totalPrice;

    // Getters and Setters
    public String getCaravaneId() {
        return caravaneId;
    }
    public void setCaravaneId(String caravaneId) {
        this.caravaneId = caravaneId;
    }
    public LocalDate getStartDate() {
        return startDate;
    }
    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }
    public int getNumberOfGuests() {
        return numberOfGuests;
    }
    public void setNumberOfGuests(int numberOfGuests) {
        this.numberOfGuests = numberOfGuests;
    }
    public int getNumberOfDays() {
        return numberOfDays;
    }
    public void setNumberOfDays(int numberOfDays) {
        this.numberOfDays = numberOfDays;
    }
    public BigDecimal getTotalPrice() {
        return totalPrice;
    }
    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }
}