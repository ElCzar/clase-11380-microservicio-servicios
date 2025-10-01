package microoservicios.service.microo.dto;

import java.math.BigDecimal;
import java.util.UUID;

public class ServiceRequestDto {
    private String title;
    private String description;
    private BigDecimal price;
    private Double averageRating;

    // Solo mandas los IDs de las relaciones
    private UUID categoryId;
    private UUID statusId;
    private UUID countryId;

    // Getters y setters
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

    public UUID getStatusId() { return statusId; }
    public void setStatusId(UUID statusId) { this.statusId = statusId; }

    public UUID getCountryId() { return countryId; }
    public void setCountryId(UUID countryId) { this.countryId = countryId; }
}
