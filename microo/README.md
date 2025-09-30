# 🛍️ Marketplace Services Microservice

## 📋 Overview

The **Services Microservice** is the core service management component of the marketplace ecosystem. It handles CRUD operations for marketplace services and acts as the **Kafka event producer** for the entire microservices architecture.

## 🏗️ Architecture

```
Services Microservice (Producer + Kafka Server)
    ├── REST API (CRUD operations)
    ├── Kafka Event Publisher
    ├── GraphQL API
    ├── Keycloak Security
    └── H2 Database
    
    ↓ Publishes Events
    
Kafka Topics:
    ├── marketplace.service.created
    ├── marketplace.service.updated
    └── marketplace.service.deleted
    
    ↓ Consumed By
    
Other Microservices (orden-pago, perfil, etc.)
```

## 🚀 Quick Start

### Prerequisites

- **Java 21** or higher
- **Maven 3.6+**
- **Docker Desktop** (for Kafka infrastructure)
- **Git**

### 1. Clone and Setup

```bash
git clone [repository-url]
cd microoservicio_servicios/main/clase-11380-microservicio-servicios/microo
```

### 2. Start Kafka Infrastructure (Required First!)

```bash
# Start Kafka + Zookeeper + Kafka UI
docker-compose -f docker-compose-kafka.yml up -d

# Verify services are running
docker-compose -f docker-compose-kafka.yml ps
```

**Expected Output:**
```
NAME                    IMAGE                          STATUS
kafka                   confluentinc/cp-kafka:latest   Up
kafka-ui                provectuslabs/kafka-ui:latest  Up  
zookeeper               confluentinc/cp-zookeeper:latest Up
```

### 3. Start the Microservice

```bash
# Build the project
mvn clean install

# Run the application
mvn spring-boot:run
```

**Application will start on:** `http://localhost:8080`

## 🔧 Configuration

### Kafka Configuration

The service is configured to run Kafka on the default ports:
- **Kafka Broker**: `localhost:9092`
- **Zookeeper**: `localhost:2181` 
- **Kafka UI**: `http://localhost:8090`

### Security Configuration

- **Keycloak Realm**: `microservices-ecosystem`
- **Keycloak Server**: `http://localhost:8180`
- **Required Roles**: `USER`, `PROVIDER`, `ADMIN`

### Database

- **H2 In-Memory Database**
- **Console**: `http://localhost:8080/h2-console`
- **URL**: `jdbc:h2:mem:marketplace-db`
- **Username**: `sa` / **Password**: `password`

## 📡 API Endpoints

### REST API

| Method | Endpoint | Description | Security |
|--------|----------|-------------|----------|
| `GET` | `/api/v1/services/health` | Health check | Public |
| `GET` | `/api/v1/services` | List all services | Public |
| `GET` | `/api/v1/services/{id}` | Get service by ID | Public |
| `POST` | `/api/v1/services` | Create new service | `PROVIDER` or `ADMIN` |
| `PUT` | `/api/v1/services/{id}` | Update service | `PROVIDER` or `ADMIN` |
| `DELETE` | `/api/v1/services/{id}` | Delete service | `PROVIDER` or `ADMIN` |

### GraphQL API

- **Endpoint**: `http://localhost:8080/graphql`
- **Playground**: `http://localhost:8080/graphiql` (if enabled)

**Example Query:**
```graphql
query {
  services(title: null, minPrice: null, maxPrice: null) {
    id
    title
    description
    price
    averageRating
  }
}
```

## 🎯 Kafka Integration

### Published Events

When services are created, updated, or deleted, the following events are published:

| Topic | Event Type | Trigger |
|-------|------------|---------|
| `marketplace.service.created` | ServiceEventDto | POST `/api/v1/services` |
| `marketplace.service.updated` | ServiceEventDto | PUT `/api/v1/services/{id}` |
| `marketplace.service.deleted` | ServiceEventDto | DELETE `/api/v1/services/{id}` |

### Event Structure (ServiceEventDto)

```json
{
  "serviceId": "123e4567-e89b-12d3-a456-426614174000",
  "title": "Web Development Service",
  "description": "Professional web development service...",
  "price": 1500.00,
  "averageRating": 4.5,
  "eventType": "CREATED",
  "timestamp": "2023-10-26T14:30:00",
  "userId": "user123"
}
```

## 🤝 Integration Guide for Other Microservices

### For Consumer Microservices (orden-pago, etc.)

#### 1. Add Kafka Dependency

```xml
<dependency>
    <groupId>org.springframework.kafka</groupId>
    <artifactId>spring-kafka</artifactId>
</dependency>
```

#### 2. Configure Kafka Consumer

```yaml
# application.yml
spring:
  kafka:
    bootstrap-servers: localhost:9092  # Connect to Services microservice Kafka
    consumer:
      group-id: your-service-group
      key-deserializer: org.apache.kafka.common.serialization.StringDeserializer
      value-deserializer: org.springframework.kafka.support.serializer.JsonDeserializer
      properties:
        spring.json.trusted.packages: "*"
```

#### 3. Create Event Consumer

```java
@Component
public class ServiceEventConsumer {
    
    @KafkaListener(topics = "marketplace.service.created")
    public void handleServiceCreated(ServiceEventDto event) {
        log.info("New service created: {}", event.getTitle());
        // Your integration logic here
    }
    
    @KafkaListener(topics = "marketplace.service.updated") 
    public void handleServiceUpdated(ServiceEventDto event) {
        log.info("Service updated: {}", event.getTitle());
        // Your integration logic here
    }
    
    @KafkaListener(topics = "marketplace.service.deleted")
    public void handleServiceDeleted(ServiceEventDto event) {
        log.info("Service deleted: {}", event.getTitle());
        // Your integration logic here
    }
}
```

#### 4. Import Required DTOs

Copy the `ServiceEventDto` class from this microservice or create a shared library.

## 🧪 Testing

### 1. Health Check

```bash
curl http://localhost:8080/api/v1/services/health
# Expected: "Marketplace Service is running"
```

### 2. Create Service (requires JWT token)

```bash
curl -X POST http://localhost:8080/api/v1/services \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "title": "Test Service",
    "description": "A test service",
    "price": 100.00
  }'
```

### 3. Monitor Kafka Events

1. Open Kafka UI: `http://localhost:8090`
2. Navigate to Topics → `http://localhost:8090/ui/clusters/local/all-topics`
3. Check `marketplace.service.created` topic for published events
4. You should see the "local" cluster with all available topics

### 4. Test GraphQL

```bash
curl -X POST http://localhost:8080/graphql \
  -H "Content-Type: application/json" \
  -d '{
    "query": "query { services { id title price } }"
  }'
```

## 🔐 Security Setup

### Keycloak Configuration

1. **Start Keycloak** (if not running):
   ```bash
   # If you have Keycloak Docker setup
   docker run -p 8180:8080 -e KEYCLOAK_ADMIN=admin -e KEYCLOAK_ADMIN_PASSWORD=admin quay.io/keycloak/keycloak:latest start-dev
   ```

2. **Create JWT Token** for testing:
   ```bash
   curl -X POST "http://localhost:8180/realms/microservices-ecosystem/protocol/openid-connect/token" \
        -H "Content-Type: application/x-www-form-urlencoded" \
        -d "grant_type=password" \
        -d "client_id=microo-service" \
        -d "username=test-provider" \
        -d "password=password"
   ```

## 🐳 Docker Commands Reference

### Kafka Management

```bash
# Start all Kafka services
docker-compose -f docker-compose-kafka.yml up -d

# Stop all Kafka services  
docker-compose -f docker-compose-kafka.yml down

# View logs
docker-compose -f docker-compose-kafka.yml logs -f

# Check service status
docker-compose -f docker-compose-kafka.yml ps

# Remove all volumes (clean slate)
docker-compose -f docker-compose-kafka.yml down -v
```

### Monitoring

- **Kafka UI**: `http://localhost:8090`
- **Application Health**: `http://localhost:8080/actuator/health`
- **H2 Database Console**: `http://localhost:8080/h2-console`

## 🚨 Troubleshooting

### Common Issues

#### 1. Kafka Connection Failed
```
Error: org.apache.kafka.common.errors.TimeoutException
```
**Solution**: Ensure Kafka is running with `docker-compose -f docker-compose-kafka.yml up -d`

#### 2. Port Already in Use
```
Error: Port 8080 is already in use
```
**Solution**: 
- Kill existing process: `netstat -ano | findstr :8080`
- Or use different port: `mvn spring-boot:run -Dserver.port=8081`

#### 3. Docker Desktop Not Running
```
Error: Cannot connect to Docker daemon
```
**Solution**: Start Docker Desktop application

#### 4. Authentication Required
```
Error: 401 Unauthorized
```
**Solution**: Include valid JWT token in Authorization header

### Health Checks

```bash
# Check if application is running
curl http://localhost:8080/api/v1/services/health

# Check if Kafka is accessible
curl http://localhost:8090

# Check database console
curl http://localhost:8080/h2-console
```

## 📚 Additional Resources

- **Spring Boot Documentation**: https://spring.io/projects/spring-boot
- **Spring Kafka Documentation**: https://spring.io/projects/spring-kafka
- **Kafka Documentation**: https://kafka.apache.org/documentation/
- **Keycloak Documentation**: https://www.keycloak.org/documentation

## 🤝 Team Integration Checklist

### For Services Microservice (This Service) ✅

- [x] Kafka infrastructure running on `localhost:9092`
- [x] REST API available on `localhost:8080`
- [x] Events published to topics
- [x] Security configured with Keycloak
- [x] Health endpoints available
- [x] Documentation complete

### For Consumer Microservices (Your Task)

- [ ] Add Kafka consumer configuration
- [ ] Import `ServiceEventDto` structure
- [ ] Implement event handlers (`@KafkaListener`)
- [ ] Test event consumption
- [ ] Update your application.yml with Kafka connection
- [ ] Test integration end-to-end

## 📞 Support

If you encounter any issues during integration:

1. **Check the logs**: `mvn spring-boot:run` output
2. **Verify Kafka is running**: `http://localhost:8090`
3. **Test connectivity**: Use the health check endpoints
4. **Check this README**: Troubleshooting section

---

**🎉 Happy Integration!** 

Your Services microservice is ready to serve as the foundation for the marketplace ecosystem. Once you implement your Kafka consumers, you'll have a fully event-driven architecture!