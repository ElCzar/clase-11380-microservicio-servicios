package microoservicios.service.microo.dto.examples;

import microoservicios.service.microo.dto.ServiceRequestDto;
import microoservicios.service.microo.dto.ServiceResponseDto;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * DEMO EXAMPLES FOR YOUR TEAMMATE
 * 
 * This class shows how to use the DTOs when communicating with your Service Controller
 * Your teammate should use these patterns in their microservice
 */
public class ServiceDtoExamples {

    /**
     * EXAMPLE 1: How to create a ServiceRequestDto for POST /api/v1/services
     * Your teammate should use this structure when creating new services
     */
    public static ServiceRequestDto createServiceRequestExample() {
        ServiceRequestDto request = new ServiceRequestDto();
        
        // Required fields
        request.setTitle("Web Development Service");
        request.setDescription("Professional web development service including frontend and backend development");
        request.setPrice(new BigDecimal("1500.00"));
        
        // Optional fields (UUIDs should come from your teammate's database)
        request.setCategoryId(UUID.fromString("550e8400-e29b-41d4-a716-446655440001")); // Technology category
        request.setStatusId(UUID.fromString("550e8400-e29b-41d4-a716-446655440002"));   // Active status
        request.setCountryId(UUID.fromString("550e8400-e29b-41d4-a716-446655440003"));  // Country ID
        
        // Average rating is usually calculated, not set directly
        // request.setAverageRating(4.5);
        
        return request;
    }

    /**
     * EXAMPLE 2: How to create a ServiceRequestDto for PUT /api/v1/services/{id}
     * Your teammate should use this structure when updating existing services
     */
    public static ServiceRequestDto updateServiceRequestExample() {
        ServiceRequestDto request = new ServiceRequestDto();
        
        // Update specific fields
        request.setTitle("Updated Web Development Service");
        request.setDescription("Updated professional web development service with new features");
        request.setPrice(new BigDecimal("1800.00")); // Price increase
        
        // Keep existing relationships or update them
        request.setCategoryId(UUID.fromString("550e8400-e29b-41d4-a716-446655440001"));
        request.setStatusId(UUID.fromString("550e8400-e29b-41d4-a716-446655440002"));
        request.setCountryId(UUID.fromString("550e8400-e29b-41d4-a716-446655440003"));
        
        return request;
    }

    /**
     * EXAMPLE 3: What your teammate should expect to receive as a response
     * This is the structure of ServiceResponseDto that your controller returns
     */
    public static ServiceResponseDto expectedResponseExample() {
        ServiceResponseDto response = new ServiceResponseDto();
        
        // System-generated fields
        response.setId(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"));
        response.setTitle("Web Development Service");
        response.setDescription("Professional web development service including frontend and backend development");
        response.setPrice(new BigDecimal("1500.00"));
        response.setAverageRating(4.5);
        
        // Related entity information
        ServiceResponseDto.CategoryDto category = new ServiceResponseDto.CategoryDto(
            UUID.fromString("550e8400-e29b-41d4-a716-446655440001"), 
            "Technology"
        );
        response.setCategory(category);
        
        ServiceResponseDto.StatusDto status = new ServiceResponseDto.StatusDto(
            UUID.fromString("550e8400-e29b-41d4-a716-446655440002"), 
            "Active"
        );
        response.setStatus(status);
        
        ServiceResponseDto.CountryDto country = new ServiceResponseDto.CountryDto(
            UUID.fromString("550e8400-e29b-41d4-a716-446655440003"), 
            "Colombia"
        );
        response.setCountry(country);
        
        return response;
    }

    /**
     * EXAMPLE 4: JSON representation that your teammate will work with
     * 
     * FOR CREATING A SERVICE (POST request body):
     * {
     *   "title": "Web Development Service",
     *   "description": "Professional web development service including frontend and backend development",
     *   "price": 1500.00,
     *   "category_id": "550e8400-e29b-41d4-a716-446655440001",
     *   "status_id": "550e8400-e29b-41d4-a716-446655440002",
     *   "country_id": "550e8400-e29b-41d4-a716-446655440003"
     * }
     * 
     * RESPONSE YOUR TEAMMATE WILL RECEIVE:
     * {
     *   "id": "123e4567-e89b-12d3-a456-426614174000",
     *   "title": "Web Development Service",
     *   "description": "Professional web development service including frontend and backend development",
     *   "price": 1500.00,
     *   "average_rating": 4.5,
     *   "category": {
     *     "id": "550e8400-e29b-41d4-a716-446655440001",
     *     "name": "Technology"
     *   },
     *   "status": {
     *     "id": "550e8400-e29b-41d4-a716-446655440002",
     *     "name": "Active"
     *   },
     *   "country": {
     *     "id": "550e8400-e29b-41d4-a716-446655440003",
     *     "name": "Colombia"
     *   }
     * }
     */

    /**
     * EXAMPLE 5: HTTP Client Usage (using RestTemplate or WebClient)
     * Your teammate can use this pattern in their microservice
     */
    public static void httpClientExample() {
        /*
        // Using RestTemplate (Spring Boot)
        RestTemplate restTemplate = new RestTemplate();
        
        // Create a new service
        ServiceRequestDto request = createServiceRequestExample();
        ServiceResponseDto response = restTemplate.postForObject(
            "http://your-service-url/api/v1/services", 
            request, 
            ServiceResponseDto.class
        );
        
        // Update an existing service
        ServiceRequestDto updateRequest = updateServiceRequestExample();
        restTemplate.put(
            "http://your-service-url/api/v1/services/{id}", 
            updateRequest, 
            response.getId()
        );
        
        // Get a service by ID
        ServiceResponseDto getResponse = restTemplate.getForObject(
            "http://your-service-url/api/v1/services/{id}", 
            ServiceResponseDto.class, 
            response.getId()
        );
        
        // Get all services
        ServiceResponseDto[] allServices = restTemplate.getForObject(
            "http://your-service-url/api/v1/services", 
            ServiceResponseDto[].class
        );
        */
    }
}