package org.budgetmanager.backend.model;

import jakarta.persistence.*; // Or javax.persistence

import java.io.Serializable;
import java.math.BigDecimal; // For precise currency values
import java.time.LocalDateTime;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(name = "caravanes")
public class Caravane implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name; // e.g., "Family Camper Van", "Luxury Motorhome"

    @Column(nullable = false, length = 255)
    private String description;

    @Column(nullable = false)
    private String type; // e.g., "Van Aménagé", "Caravane", "Camping-car"

    @Column(nullable = false)
    private int capacity; // Number of people

    @Column(nullable = false)
    private BigDecimal pricePerDay; // Using BigDecimal for currency

    @ElementCollection // For storing a collection of strings (e.g., image URLs)
    @CollectionTable(name = "caravane_images", joinColumns = @JoinColumn(name = "caravane_id"))
    @Column(name = "image_url", columnDefinition = "LONGTEXT")
    private List<String> imageUrls; // List of image URLs

    @Column(nullable = false)
    private String city; // Location where the caravane is based

    @Column(name = "available", nullable = false)
    private boolean isAvailable; // Whether it's currently available for booking

    @Column(name = "approval_status", nullable = false, length = 50)
    private String approvalStatus; // "PENDING", "APPROVED", "REJECTED"

    @ManyToOne(fetch = FetchType.LAZY) // Lazy fetch as we might not always need the owner details
    @JoinColumn(name = "owner_id", nullable = false) // Link to the user who owns/lists this caravane
    @JsonIgnore
    private UserInfo owner; // The user (LOUER) who owns this caravane

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Constructors
    public Caravane() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.approvalStatus = "PENDING"; // Default to pending approval
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Getters and Setters (generate all of them)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public BigDecimal getPricePerDay() {
        return pricePerDay;
    }

    public void setPricePerDay(BigDecimal pricePerDay) {
        this.pricePerDay = pricePerDay;
    }

    public List<String> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    public String getApprovalStatus() {
        return approvalStatus;
    }

    public void setApprovalStatus(String approvalStatus) {
        this.approvalStatus = approvalStatus;
    }

    public UserInfo getOwner() {
        return owner;
    }

    public void setOwner(UserInfo owner) {
        this.owner = owner;
    }

    // Expose ownerId for frontend without serializing the lazy proxy
    @JsonProperty("ownerId")
    public Long getOwnerId() {
        return owner != null ? owner.getId() : null;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}