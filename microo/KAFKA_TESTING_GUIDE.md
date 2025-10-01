# Kafka Events Testing Guide for OrdenPago Integration

## Overview
This guide helps you test that your `microservicio-servicios` is correctly publishing Kafka events that the `ordenpago` microservice can consume.

## Prerequisites
1. Docker and Docker Compose running
2. Your servicios microservice configured with Spring Cloud Stream
3. Kafka UI accessible at http://localhost:8090

## Testing Methods

### Method 1: Automated Testing Script
Run the PowerShell script for comprehensive testing:
```powershell
cd "d:\Martin\Programas\Web\microoservicio_servicios\main\clase-11380-microservicio-servicios\microo"
.\test-kafka-events.ps1
```

### Method 2: Manual API Testing with Kafka Monitoring

#### Step 1: Start Services
```bash
# Start Kafka
docker-compose -f docker-compose-kafka.yml up -d

# Start your microservice
mvnw.cmd spring-boot:run
```

#### Step 2: Monitor Events in Real-time
Open a separate terminal and run:
```bash
.\monitor-kafka-events.bat
```

#### Step 3: Test API Endpoints
In another terminal, test these API calls:

**Create Service:**
```powershell
$headers = @{'Content-Type' = 'application/json'}
$body = @{
    title = 'Test Service'
    description = 'Testing Kafka events'
    price = 99.99
    dtype = 'ServiceEntity'
} | ConvertTo-Json

Invoke-RestMethod -Uri 'http://localhost:8080/api/services' -Method POST -Headers $headers -Body $body
```

**Update Service:**
```powershell
# Get service ID from previous response, then:
$updateBody = @{
    id = "your-service-id"
    title = 'Updated Test Service'
    description = 'Updated for Kafka testing'
    price = 149.99
    dtype = 'ServiceEntity'
} | ConvertTo-Json

Invoke-RestMethod -Uri 'http://localhost:8080/api/services/your-service-id' -Method PUT -Headers $headers -Body $updateBody
```

**Delete Service:**
```powershell
Invoke-RestMethod -Uri 'http://localhost:8080/api/services/your-service-id' -Method DELETE
```

## Verification Points

### 1. Kafka UI Verification
- Open: http://localhost:8090/ui/clusters/local/topics/marketplace.service.events
- Check for messages after each API call
- Verify message structure contains `ServiceEventDto`

### 2. Expected Message Structure
Each message should look like:
```json
{
  "serviceId": "uuid-here",
  "eventType": "SERVICE_CREATED", // or SERVICE_UPDATED, SERVICE_DELETED
  "timestamp": "2025-09-30T18:30:00Z",
  "service": {
    "id": "uuid-here",
    "title": "Service Title",
    "description": "Service Description",
    "price": 99.99,
    "dtype": "ServiceEntity"
  }
}
```

### 3. Event Types to Verify
- ✅ `SERVICE_CREATED` - When POST /api/services
- ✅ `SERVICE_UPDATED` - When PUT /api/services/{id}  
- ✅ `SERVICE_DELETED` - When DELETE /api/services/{id}

## OrdenPago Integration Requirements

For the ordenpago microservice to consume your events, it needs:

### 1. Consumer Bean
```java
@Bean
public Consumer<ServiceEventDto> handleMarketplaceEvents() {
    return event -> {
        log.info("Received marketplace event: {}", event);
        // Process the service event
        switch (event.getEventType()) {
            case "SERVICE_CREATED":
                handleServiceCreated(event.getService());
                break;
            case "SERVICE_UPDATED":
                handleServiceUpdated(event.getService());
                break;
            case "SERVICE_DELETED":
                handleServiceDeleted(event.getServiceId());
                break;
        }
    };
}
```

### 2. Binding Configuration (ordenpago's application.yml)
```yaml
spring:
  cloud:
    stream:
      bindings:
        handleMarketplaceEvents-in-0:
          destination: marketplace.service.events
          content-type: application/json
          group: ordenpago-consumer-group
```

### 3. DTO Class (ordenpago needs this)
```java
public class ServiceEventDto {
    private String serviceId;
    private String eventType;
    private LocalDateTime timestamp;
    private ServiceEntity service;
    // constructors, getters, setters
}
```

## Troubleshooting

### No Events in Kafka UI
1. Check microservice logs for errors
2. Verify Spring Cloud Stream binding is active
3. Ensure Kafka is running on localhost:9092

### Events Not Formatted Correctly
1. Check `ServiceEventPublisher.java` implementation
2. Verify `ServiceEventDto` structure
3. Check message serialization in logs

### Consumer Not Receiving Events
1. Verify topic name matches exactly: `marketplace.service.events`
2. Check consumer group configuration
3. Ensure JSON deserialization is configured

## Success Criteria
✅ Events appear in marketplace.service.events topic
✅ Message structure matches ServiceEventDto format  
✅ All three event types (CREATE, UPDATE, DELETE) work
✅ Messages are valid JSON
✅ Events are published in real-time with API calls