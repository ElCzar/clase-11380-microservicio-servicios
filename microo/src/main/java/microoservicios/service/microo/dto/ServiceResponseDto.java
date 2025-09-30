package microoservicios.service.microo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * DTO for sending service responses to external microservices
 * This is what your teammate should expect to receive from your Service Controller
 */
public class ServiceResponseDto {

    @JsonProperty("id")
    private UUID id;

    @JsonProperty("title")
    private String title;

    @JsonProperty("description")
    private String description;

    @JsonProperty("price")
    private BigDecimal price;

    @JsonProperty("average_rating")
    private Double averageRating;

    // Category information
    @JsonProperty("category")
    private CategoryDto category;

    // Status information
    @JsonProperty("status")
    private StatusDto status;

    // Country information
    @JsonProperty("country")
    private CountryDto country;

    // Constructors
    public ServiceResponseDto() {}

    public ServiceResponseDto(UUID id, String title, String description, BigDecimal price, 
                            Double averageRating) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.price = price;
        this.averageRating = averageRating;
    }

    // Getters and Setters
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

    public CategoryDto getCategory() {
        return category;
    }

    public void setCategory(CategoryDto category) {
        this.category = category;
    }

    public StatusDto getStatus() {
        return status;
    }

    public void setStatus(StatusDto status) {
        this.status = status;
    }

    public CountryDto getCountry() {
        return country;
    }

    public void setCountry(CountryDto country) {
        this.country = country;
    }

    @Override
    public String toString() {
        return "ServiceResponseDto{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", averageRating=" + averageRating +
                ", category=" + category +
                ", status=" + status +
                ", country=" + country +
                '}';
    }

    // Nested DTOs for related entities
    public static class CategoryDto {
        @JsonProperty("id")
        private UUID id;
        
        @JsonProperty("name")
        private String name;

        public CategoryDto() {}

        public CategoryDto(UUID id, String name) {
            this.id = id;
            this.name = name;
        }

        public UUID getId() { return id; }
        public void setId(UUID id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        @Override
        public String toString() {
            return "CategoryDto{id=" + id + ", name='" + name + "'}";
        }
    }

    public static class StatusDto {
        @JsonProperty("id")
        private UUID id;
        
        @JsonProperty("name")
        private String name;

        public StatusDto() {}

        public StatusDto(UUID id, String name) {
            this.id = id;
            this.name = name;
        }

        public UUID getId() { return id; }
        public void setId(UUID id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        @Override
        public String toString() {
            return "StatusDto{id=" + id + ", name='" + name + "'}";
        }
    }

    public static class CountryDto {
        @JsonProperty("id")
        private UUID id;
        
        @JsonProperty("name")
        private String name;

        public CountryDto() {}

        public CountryDto(UUID id, String name) {
            this.id = id;
            this.name = name;
        }

        public UUID getId() { return id; }
        public void setId(UUID id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        @Override
        public String toString() {
            return "CountryDto{id=" + id + ", name='" + name + "'}";
        }
    }
}