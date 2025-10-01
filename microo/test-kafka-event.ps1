# PowerShell script to test Kafka event publishing
# This script will create a service and check if the topic is created

Write-Host "Testing Kafka Event Publishing..." -ForegroundColor Yellow

# Step 1: Get JWT token
Write-Host "`n1. Getting JWT token..." -ForegroundColor Green
$tokenResponse = Invoke-RestMethod -Uri "http://localhost:8180/realms/microservices-ecosystem/protocol/openid-connect/token" -Method POST -ContentType "application/x-www-form-urlencoded" -Body "grant_type=client_credentials&client_id=perfil-service&client_secret=V6nBCqj4jQmxb2i9LJYxgD24fFj3P8sQ"

if (-not $tokenResponse.access_token) {
    Write-Host "Failed to get JWT token" -ForegroundColor Red
    exit 1
}

$token = $tokenResponse.access_token
Write-Host "JWT token obtained successfully" -ForegroundColor Green

# Step 2: Check microservice health
Write-Host "`n2. Checking microservice health..." -ForegroundColor Green
try {
    $healthResponse = Invoke-RestMethod -Uri "http://localhost:8083/actuator/health" -Method GET
    Write-Host "Microservice is healthy: $($healthResponse.status)" -ForegroundColor Green
} catch {
    Write-Host "Microservice is not running on port 8083" -ForegroundColor Red
    exit 1
}

# Step 3: Create a test service
Write-Host "`n3. Creating a test service..." -ForegroundColor Green
$headers = @{
    "Authorization" = "Bearer $token"
    "Content-Type" = "application/json"
}

$serviceData = @{
    title = "Kafka Event Test Service"
    description = "Testing Kafka event publishing"
    price = 99.99
    status = @{ id = "e8b2c1d5-4f3e-4a2d-8c7b-5e9f3d2a1b0c" }
    category = @{ id = "b1a2c3d4-5e6f-7a8b-9c0d-1e2f3a4b5c6d" }
    country = @{ id = "c2d3e4f5-6a7b-8c9d-0e1f-2a3b4c5d6e7f" }
}

$jsonData = $serviceData | ConvertTo-Json -Depth 3

try {
    $response = Invoke-RestMethod -Uri "http://localhost:8083/services" -Method POST -Headers $headers -Body $jsonData
    Write-Host "Service created successfully with ID: $($response.id)" -ForegroundColor Green
} catch {
    Write-Host "Failed to create service: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

# Step 4: Wait a moment for Kafka processing
Write-Host "`n4. Waiting 5 seconds for Kafka event processing..." -ForegroundColor Green
Start-Sleep -Seconds 5

# Step 5: Check Kafka UI for topics
Write-Host "`n5. Checking Kafka topics..." -ForegroundColor Green
Write-Host "Open your browser and go to: http://localhost:8090/ui/clusters/local/all-topics" -ForegroundColor Cyan
Write-Host "Look for the topic: marketplace.service.events" -ForegroundColor Cyan

# Step 6: Optional - Update the service to trigger another event
Write-Host "`n6. Updating the service to trigger another event..." -ForegroundColor Green
$updateData = @{
    title = "Kafka Event Test Service - UPDATED"
    description = "Updated to test Kafka event publishing"
    price = 149.99
    status = @{ id = "e8b2c1d5-4f3e-4a2d-8c7b-5e9f3d2a1b0c" }
    category = @{ id = "b1a2c3d4-5e6f-7a8b-9c0d-1e2f3a4b5c6d" }
    country = @{ id = "c2d3e4f5-6a7b-8c9d-0e1f-2a3b4c5d6e7f" }
}

$updateJson = $updateData | ConvertTo-Json -Depth 3

try {
    $updateResponse = Invoke-RestMethod -Uri "http://localhost:8083/services/$($response.id)" -Method PUT -Headers $headers -Body $updateJson
    Write-Host "Service updated successfully" -ForegroundColor Green
} catch {
    Write-Host "Failed to update service: $($_.Exception.Message)" -ForegroundColor Red
}

# Step 7: Wait again and check
Write-Host "`n7. Waiting another 5 seconds..." -ForegroundColor Green
Start-Sleep -Seconds 5

Write-Host "`nTest completed. Check Kafka UI at http://localhost:8090/ui/clusters/local/all-topics for the topic 'marketplace.service.events'" -ForegroundColor Yellow
Write-Host "If the topic exists, the ServiceEventPublisher is working correctly." -ForegroundColor Yellow
Write-Host "If the topic doesn't exist, there's an issue with the Spring Cloud Stream configuration." -ForegroundColor Yellow