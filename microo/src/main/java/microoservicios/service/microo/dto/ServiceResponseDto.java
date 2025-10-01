package microoservicios.service.microo.dto;

import java.math.BigDecimal;
import java.util.UUID;

public class ServiceResponseDto {
    private UUID id;
    private String title;
    private String description;
    private BigDecimal price;
    private Double averageRating;

    private UUID categoryId;
    private String categoryName;

    private UUID statusId;
    private String statusName;

    private UUID countryId;
    private String countryName;

    // Getters y setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public Double getAverageRating() { return averageRating; }
    public void setAverageRating(Double averageRating) { this.averageRating = averageRating; }

    public UUID getCategoryId() { return categoryId; }
    public void setCategoryId(UUID categoryId) { this.categoryId = categoryId; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public UUID getStatusId() { return statusId; }
    public void setStatusId(UUID statusId) { this.statusId = statusId; }

    public String getStatusName() { return statusName; }
    public void setStatusName(String statusName) { this.statusName = statusName; }

    public UUID getCountryId() { return countryId; }
    public void setCountryId(UUID countryId) { this.countryId = countryId; }

    public String getCountryName() { return countryName; }
    public void setCountryName(String countryName) { this.countryName = countryName; }

    
}
