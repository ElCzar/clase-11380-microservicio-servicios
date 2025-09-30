# 📋 DTO Guide for Service Controller Integration

## 🎯 Overview
This guide shows how to integrate with the Service Controller using the correct DTOs.

## 📁 DTO Files Created
- `ServiceRequestDto.java` - For sending requests TO the Service Controller
- `ServiceResponseDto.java` - For receiving responses FROM the Service Controller  
- `ServiceDtoExamples.java` - Complete usage examples

## 🚀 Quick Start for Your Teammate

### 1. **Creating a New Service (POST)**

**Endpoint:** `POST /api/v1/services`

**Request Body (JSON):**
```json
{
  "title": "Web Development Service",
  "description": "Professional web development service including frontend and backend development",
  "price": 1500.00,
  "category_id": "550e8400-e29b-41d4-a716-446655440001",
  "status_id": "550e8400-e29b-41d4-a716-446655440002",
  "country_id": "550e8400-e29b-41d4-a716-446655440003"
}
```

**Expected Response:**
```json
{
  "id": "123e4567-e89b-12d3-a456-426614174000",
  "title": "Web Development Service",
  "description": "Professional web development service including frontend and backend development",
  "price": 1500.00,
  "average_rating": null,
  "category": {
    "id": "550e8400-e29b-41d4-a716-446655440001",
    "name": "Technology"
  },
  "status": {
    "id": "550e8400-e29b-41d4-a716-446655440002",
    "name": "Active"
  },
  "country": {
    "id": "550e8400-e29b-41d4-a716-446655440003",
    "name": "Colombia"
  }
}
```

### 2. **Updating a Service (PUT)**

**Endpoint:** `PUT /api/v1/services/{serviceId}`

**Request Body:** Same structure as POST but with updated values.

### 3. **Getting Services (GET)**

**Get All:** `GET /api/v1/services`
**Get By ID:** `GET /api/v1/services/{serviceId}`

Both return `ServiceResponseDto` structure.

## 🔐 Authentication Requirements

**For POST, PUT, DELETE operations:**
- Requires JWT token with `PROVIDER` or `ADMIN` role
- Add header: `Authorization: Bearer {jwt-token}`

**For GET operations:**
- Public access (no authentication required)

## 🛠️ Java Implementation Example

```java
@Service
public class ServiceIntegrationService {
    
    private final RestTemplate restTemplate;
    private final String serviceUrl = "http://localhost:8080/api/v1/services";
    
    public ServiceIntegrationService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
    
    public ServiceResponseDto createService(ServiceRequestDto request) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth("your-jwt-token");
        
        HttpEntity<ServiceRequestDto> entity = new HttpEntity<>(request, headers);
        
        return restTemplate.postForObject(serviceUrl, entity, ServiceResponseDto.class);
    }
    
    public ServiceResponseDto getServiceById(UUID serviceId) {
        return restTemplate.getForObject(serviceUrl + "/" + serviceId, ServiceResponseDto.class);
    }
    
    public ServiceResponseDto[] getAllServices() {
        return restTemplate.getForObject(serviceUrl, ServiceResponseDto[].class);
    }
}
```

## 📡 Kafka Events Integration

When services are created, updated, or deleted, your Service Controller publishes Kafka events to these topics:

- `marketplace.service.created` - When a service is created
- `marketplace.service.updated` - When a service is updated  
- `marketplace.service.deleted` - When a service is deleted

**Event Structure:**
```json
{
  "serviceId": "123e4567-e89b-12d3-a456-426614174000",
  "eventType": "CREATED",
  "timestamp": "2025-09-29T23:30:00Z",
  "userId": "user123",
  "serviceData": {
    // ServiceResponseDto structure
  }
}
```

## ❗ Important Notes

1. **UUIDs:** All IDs use UUID format, not integers
2. **Price:** Use `BigDecimal` for precise monetary values  
3. **Validation:** Title and description are required
4. **Security:** POST/PUT/DELETE require authentication
5. **Relationships:** category_id, status_id, country_id should exist in your database

## 🧪 Testing

You can test the endpoints using:
- Postman
- curl commands
- Your microservice's integration tests

**Health Check:** `GET /api/v1/services/health` (always returns "Marketplace Service is running")

## 🔗 Related Files

- `/entity/ServiceEntity.java` - The actual database entity
- `/controller/ServiceController.java` - The REST controller
- `/kafka/ServiceKafkaProducer.java` - Kafka event publisher