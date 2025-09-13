package org.budgetmanager.backend.dto.reservation;

public class UpdateReservationRequest {
    private String startDate; // ISO yyyy-MM-dd
    private Integer numberOfGuests;
    private Integer numberOfDays;

    public String getStartDate() { return startDate; }
    public void setStartDate(String startDate) { this.startDate = startDate; }

    public Integer getNumberOfGuests() { return numberOfGuests; }
    public void setNumberOfGuests(Integer numberOfGuests) { this.numberOfGuests = numberOfGuests; }

    public Integer getNumberOfDays() { return numberOfDays; }
    public void setNumberOfDays(Integer numberOfDays) { this.numberOfDays = numberOfDays; }
}


