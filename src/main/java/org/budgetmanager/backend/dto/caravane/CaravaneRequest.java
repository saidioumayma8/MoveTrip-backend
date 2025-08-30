package org.budgetmanager.backend.dto.caravane;

import java.math.BigDecimal;
import java.util.List;

public class CaravaneRequest {
    private String name;
    private String description;
    private String type;
    private int capacity;
    private BigDecimal pricePerDay;
    private List<String> imageUrls;
    private String city;
    private boolean isAvailable;
    private Long ownerId; // ID du propriétaire

    // Getters et Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }

    public BigDecimal getPricePerDay() { return pricePerDay; }
    public void setPricePerDay(BigDecimal pricePerDay) { this.pricePerDay = pricePerDay; }

    public List<String> getImageUrls() { return imageUrls; }
    public void setImageUrls(List<String> imageUrls) { this.imageUrls = imageUrls; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public boolean isAvailable() { return isAvailable; }
    public void setAvailable(boolean available) { isAvailable = available; }

    public Long getOwnerId() { return ownerId; }
    public void setOwnerId(Long ownerId) { this.ownerId = ownerId; }
}
