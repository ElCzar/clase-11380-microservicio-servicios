package microoservicios.service.microo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import java.util.UUID;

public class ServiceEventDto {
    
    private UUID serviceId;
    private String title;
    private String description;
    private Double price;
    private Double averageRating;
    private String eventType; // CREATED, UPDATED, DELETED
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp;
    
    private String userId; // Who performed the action
    
    // Default constructor
    public ServiceEventDto() {
        this.timestamp = LocalDateTime.now();
    }
    
    // Constructor for different event types
    public ServiceEventDto(UUID serviceId, String title, String description, Double price, 
                          Double averageRating, String eventType, String userId) {
        this.serviceId = serviceId;
        this.title = title;
        this.description = description;
        this.price = price;
        this.averageRating = averageRating;
        this.eventType = eventType;
        this.userId = userId;
        this.timestamp = LocalDateTime.now();
    }
    
    // Getters and Setters
    public UUID getServiceId() { return serviceId; }
    public void setServiceId(UUID serviceId) { this.serviceId = serviceId; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }
    
    public Double getAverageRating() { return averageRating; }
    public void setAverageRating(Double averageRating) { this.averageRating = averageRating; }
    
    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }
    
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    
    @Override
    public String toString() {
        return "ServiceEventDto{" +
                "serviceId=" + serviceId +
                ", title='" + title + '\'' +
                ", eventType='" + eventType + '\'' +
                ", timestamp=" + timestamp +
                ", userId='" + userId + '\'' +
                '}';
    }
}