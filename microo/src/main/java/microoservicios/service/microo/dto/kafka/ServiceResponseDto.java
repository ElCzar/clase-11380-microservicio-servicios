package microoservicios.service.microo.dto.kafka;

import com.fasterxml.jackson.annotation.JsonProperty;
import microoservicios.service.microo.entity.ServiceEntity;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * DTO for sending service responses TO orden-pago microservice via Kafka
 * This matches the ServiceResponseDTO expected by orden-pago
 */
public class ServiceResponseDto {
    
    // Fields for message correlation
    @JsonProperty("requestId")
    private String requestId;
    
    @JsonProperty("errorMessage")
    private String errorMessage;
    
    // Service data fields
    @JsonProperty("id")
    private UUID id;
    
    @JsonProperty("title")
    private String title;
    
    @JsonProperty("description")
    private String description;
    
    @JsonProperty("price")
    private BigDecimal price;
    
    @JsonProperty("averageRating")
    private Double averageRating;
    
    @JsonProperty("categoryName")
    private String categoryName;
    
    @JsonProperty("isActive")
    private Boolean isActive;
    
    @JsonProperty("countryName")
    private String countryName;
    
    @JsonProperty("countryCode")
    private String countryCode;
    
    @JsonProperty("primaryImageUrl")
    private String primaryImageUrl;
    
    // Constructors
    public ServiceResponseDto() {}
    
    public ServiceResponseDto(String requestId) {
        this.requestId = requestId;
    }
    
    /**
     * Creates a ServiceResponseDto from a ServiceEntity
     */
    public static ServiceResponseDto fromServiceEntity(ServiceEntity service, String requestId) {
        ServiceResponseDto dto = new ServiceResponseDto();
        dto.setRequestId(requestId);
        dto.setId(service.getId());
        dto.setTitle(service.getTitle());
        dto.setDescription(service.getDescription());
        dto.setPrice(service.getPrice());
        dto.setAverageRating(service.getAverageRating());
        
        // Extract category name if exists
        if (service.getCategory() != null) {
            dto.setCategoryName(service.getCategory().getName());
        }
        
        // Extract status (assume active if status exists)
        if (service.getStatus() != null) {
            dto.setIsActive("ACTIVE".equalsIgnoreCase(service.getStatus().getName()));
        }
        
        // Extract country info if exists
        if (service.getCountry() != null) {
            dto.setCountryName(service.getCountry().getName());
            // Country entity doesn't have code field, using name as fallback
            dto.setCountryCode(service.getCountry().getName().substring(0, Math.min(3, service.getCountry().getName().length())).toUpperCase());
        }
        
        // Extract primary image if exists
        if (service.getImages() != null && !service.getImages().isEmpty()) {
            dto.setPrimaryImageUrl(service.getImages().get(0).getUrl());
        }
        
        return dto;
    }
    
    /**
     * Creates an error response
     */
    public static ServiceResponseDto error(String requestId, String errorMessage) {
        ServiceResponseDto dto = new ServiceResponseDto();
        dto.setRequestId(requestId);
        dto.setErrorMessage(errorMessage);
        return dto;
    }
    
    // Convenience methods (matching orden-pago expectations)
    public Boolean isAvailable() {
        return isActive != null && isActive;
    }
    
    public UUID getServiceId() {
        return id;
    }
    
    public String getName() {
        return title;
    }
    
    public Boolean getAvailable() {
        return isAvailable();
    }
    
    // Getters and Setters
    public String getRequestId() {
        return requestId;
    }
    
    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }
    
    public String getErrorMessage() {
        return errorMessage;
    }
    
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
    
    public UUID getId() {
        return id;
    }
    
    public void setId(UUID id) {
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
    
    public BigDecimal getPrice() {
        return price;
    }
    
    public void setPrice(BigDecimal price) {
        this.price = price;
    }
    
    public Double getAverageRating() {
        return averageRating;
    }
    
    public void setAverageRating(Double averageRating) {
        this.averageRating = averageRating;
    }
    
    public String getCategoryName() {
        return categoryName;
    }
    
    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
    
    public Boolean getIsActive() {
        return isActive;
    }
    
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
    
    public String getCountryName() {
        return countryName;
    }
    
    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }
    
    public String getCountryCode() {
        return countryCode;
    }
    
    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }
    
    public String getPrimaryImageUrl() {
        return primaryImageUrl;
    }
    
    public void setPrimaryImageUrl(String primaryImageUrl) {
        this.primaryImageUrl = primaryImageUrl;
    }
    
    @Override
    public String toString() {
        return "ServiceResponseDto{" +
                "requestId='" + requestId + '\'' +
                ", errorMessage='" + errorMessage + '\'' +
                ", id=" + id +
                ", title='" + title + '\'' +
                ", price=" + price +
                ", isActive=" + isActive +
                '}';
    }
}