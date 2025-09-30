package microoservicios.service.microo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * DTO for receiving service creation/update requests from external microservices
 * This is what your teammate should use when sending requests to your Service Controller
 */
public class ServiceRequestDto {

    @JsonProperty("title")
    private String title;

    @JsonProperty("description")
    private String description;

    @JsonProperty("price")
    private BigDecimal price;

    @JsonProperty("category_id")
    private UUID categoryId;

    @JsonProperty("status_id")
    private UUID statusId;

    @JsonProperty("country_id")
    private UUID countryId;

    // Average rating (optional, usually calculated)
    @JsonProperty("average_rating")
    private Double averageRating;

    // Constructors
    public ServiceRequestDto() {}

    public ServiceRequestDto(String title, String description, BigDecimal price, 
                           UUID categoryId, UUID statusId, UUID countryId) {
        this.title = title;
        this.description = description;
        this.price = price;
        this.categoryId = categoryId;
        this.statusId = statusId;
        this.countryId = countryId;
    }

    // Getters and Setters
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

    public UUID getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(UUID categoryId) {
        this.categoryId = categoryId;
    }

    public UUID getStatusId() {
        return statusId;
    }

    public void setStatusId(UUID statusId) {
        this.statusId = statusId;
    }

    public UUID getCountryId() {
        return countryId;
    }

    public void setCountryId(UUID countryId) {
        this.countryId = countryId;
    }

    public Double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(Double averageRating) {
        this.averageRating = averageRating;
    }

    @Override
    public String toString() {
        return "ServiceRequestDto{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", categoryId=" + categoryId +
                ", statusId=" + statusId +
                ", countryId=" + countryId +
                ", averageRating=" + averageRating +
                '}';
    }
}