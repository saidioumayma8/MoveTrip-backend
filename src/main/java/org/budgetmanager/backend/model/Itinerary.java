package org.budgetmanager.backend.model;
import org.budgetmanager.backend.model.UserInfo;
import jakarta.persistence.*; // Or javax.persistence
import org.apache.catalina.User;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "itineraries")
public class Itinerary implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(length = 50)
    private String duration; // e.g., "5 days", "1 week"

    @Column(length = 50)
    private String distance; // e.g., "500 km"

    @ElementCollection
    @CollectionTable(name = "itinerary_tags", joinColumns = @JoinColumn(name = "itinerary_id"))
    @Column(name = "tag")
    private Set<String> tags; // e.g., "Nature", "Mountain", "Beach", "City"

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_user_id") // Optional: If itineraries can be user-created
    private UserInfo createdBy;

    // Ordered list of locations in the itinerary
    @ManyToMany // Many itineraries can use many locations, and vice versa
    @JoinTable(
            name = "itinerary_locations",
            joinColumns = @JoinColumn(name = "itinerary_id"),
            inverseJoinColumns = @JoinColumn(name = "location_id")
    )
    @OrderBy("id") // Or order by a specific 'order_in_itinerary' column if you add one to join table
    private List<Location> locations;

    @Column(name = "image_url", length = 255) // Main image for the itinerary
    private String imageUrl;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Constructors
    public Itinerary() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public Set<String> getTags() {
        return tags;
    }

    public void setTags(Set<String> tags) {
        this.tags = tags;
    }

    public UserInfo getCreatedBy() { // Change the return type to UserInfo
        return createdBy;
    }

    public void setCreatedBy(UserInfo createdBy) { // Change the parameter type to UserInfo
        this.createdBy = createdBy;
    }

    public List<Location> getLocations() {
        return locations;
    }

    public void setLocations(List<Location> locations) {
        this.locations = locations;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
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