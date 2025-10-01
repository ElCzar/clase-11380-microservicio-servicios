# Step-by-Step API Testing Guide

## Step 1: Get JWT Token (CRITICAL FIRST STEP)
curl -X POST "http://localhost:8180/realms/microservices-ecosystem/protocol/openid-connect/token" ^
  -H "Content-Type: application/x-www-form-urlencoded" ^
  -d "grant_type=password&username=testuser&password=testpassword&client_id=microo-service&client_secret=vbWn3RLXfcKVZ5zE7zOxf26XCnZxklxC"

## Step 2: Copy the access_token from the response above and replace YOUR_TOKEN_HERE below

## Step 3: Test Health Endpoint
curl -X GET "http://localhost:8080/api/v1/services/health" ^
  -H "Authorization: Bearer YOUR_TOKEN_HERE"

## Step 4: Get All Services
curl -X GET "http://localhost:8080/api/v1/services" ^
  -H "Authorization: Bearer YOUR_TOKEN_HERE"

## Step 5: Create a Service
curl -X POST "http://localhost:8080/api/v1/services" ^
  -H "Authorization: Bearer YOUR_TOKEN_HERE" ^
  -H "Content-Type: application/json" ^
  -d "{\"title\":\"Test Service\",\"description\":\"Test Description\",\"price\":199.99,\"category\":\"TECHNOLOGY\",\"providerId\":\"550e8400-e29b-41d4-a716-446655440001\",\"isActive\":true,\"averageRating\":4.5}"

## Step 6: Update a Service (replace SERVICE_ID with actual ID from step 5)
curl -X PUT "http://localhost:8080/api/v1/services/SERVICE_ID" ^
  -H "Authorization: Bearer YOUR_TOKEN_HERE" ^
  -H "Content-Type: application/json" ^
  -d "{\"title\":\"Updated Test Service\",\"description\":\"Updated Description\",\"price\":249.99,\"category\":\"TECHNOLOGY\",\"providerId\":\"550e8400-e29b-41d4-a716-446655440001\",\"isActive\":true,\"averageRating\":4.8}"

## Step 7: Delete a Service (replace SERVICE_ID with actual ID)
curl -X DELETE "http://localhost:8080/api/v1/services/SERVICE_ID" ^
  -H "Authorization: Bearer YOUR_TOKEN_HERE"

# Note: After each operation, check Kafka UI at http://localhost:8090/ui/clusters/local/all-topics
# Look for events in the 'marketplace.service.events' topic