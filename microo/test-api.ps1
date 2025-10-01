# ServiceController API Testing Script
# Make sure your microservice is running on localhost:8080
# Make sure Keycloak is running on localhost:8180

Write-Host "🚀 Starting API Tests for ServiceController" -ForegroundColor Green
Write-Host "===============================================" -ForegroundColor Green

# Configuration
$baseUrl = "http://localhost:8080"
$keycloakUrl = "http://localhost:8180"
$realm = "microservices-ecosystem"
$clientId = "microo-service"
$clientSecret = "vbWn3RLXfcKVZ5zE7zOxf26XCnZxklxC"
$username = "testuser"
$password = "testpassword"

# Step 1: Get JWT Token
Write-Host "`n1️⃣ Getting JWT Token from Keycloak..." -ForegroundColor Yellow

$tokenBody = @{
    grant_type = "password"
    username = $username
    password = $password
    client_id = $clientId
    client_secret = $clientSecret
}

try {
    $tokenResponse = Invoke-RestMethod -Uri "$keycloakUrl/realms/$realm/protocol/openid-connect/token" -Method Post -Body $tokenBody -ContentType "application/x-www-form-urlencoded"
    $token = $tokenResponse.access_token
    Write-Host "✅ Token obtained successfully" -ForegroundColor Green
    Write-Host "Token expires in: $($tokenResponse.expires_in) seconds" -ForegroundColor Cyan
} catch {
    Write-Host "❌ Failed to get token: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

# Headers for authenticated requests
$headers = @{
    "Authorization" = "Bearer $token"
    "Content-Type" = "application/json"
}

# Step 2: Health Check
Write-Host "`n2️⃣ Testing Health Endpoint..." -ForegroundColor Yellow
try {
    $healthResponse = Invoke-RestMethod -Uri "$baseUrl/api/v1/services/health" -Method Get
    Write-Host "✅ Health check: $healthResponse" -ForegroundColor Green
} catch {
    Write-Host "❌ Health check failed: $($_.Exception.Message)" -ForegroundColor Red
}

# Step 3: Get All Services (should be empty initially)
Write-Host "`n3️⃣ Getting All Services..." -ForegroundColor Yellow
try {
    $allServices = Invoke-RestMethod -Uri "$baseUrl/api/v1/services" -Method Get
    Write-Host "✅ Found $($allServices.Count) services" -ForegroundColor Green
    if ($allServices.Count -gt 0) {
        Write-Host "Services:" -ForegroundColor Cyan
        $allServices | ForEach-Object { Write-Host "  - $($_.title) (ID: $($_.id))" -ForegroundColor White }
    }
} catch {
    Write-Host "❌ Get all services failed: $($_.Exception.Message)" -ForegroundColor Red
}

# Step 4: Create a New Service
Write-Host "`n4️⃣ Creating a New Service..." -ForegroundColor Yellow
$newService = @{
    title = "PowerShell Test Service"
    description = "A service created via PowerShell script for testing"
    price = 199.99
    category = "TECHNOLOGY"
    providerId = "550e8400-e29b-41d4-a716-446655440001"
    isActive = $true
    averageRating = 4.5
} | ConvertTo-Json

try {
    $createdService = Invoke-RestMethod -Uri "$baseUrl/api/v1/services" -Method Post -Body $newService -Headers $headers
    Write-Host "✅ Service created successfully!" -ForegroundColor Green
    Write-Host "Service ID: $($createdService.id)" -ForegroundColor Cyan
    Write-Host "Service Title: $($createdService.title)" -ForegroundColor Cyan
    $serviceId = $createdService.id
} catch {
    Write-Host "❌ Service creation failed: $($_.Exception.Message)" -ForegroundColor Red
    Write-Host "Response: $($_.Exception.Response)" -ForegroundColor Red
}

# Step 5: Get Service by ID
if ($serviceId) {
    Write-Host "`n5️⃣ Getting Service by ID..." -ForegroundColor Yellow
    try {
        $serviceById = Invoke-RestMethod -Uri "$baseUrl/api/v1/services/$serviceId" -Method Get -Headers $headers
        Write-Host "✅ Retrieved service: $($serviceById.title)" -ForegroundColor Green
    } catch {
        Write-Host "❌ Get service by ID failed: $($_.Exception.Message)" -ForegroundColor Red
    }

    # Step 6: Update Service
    Write-Host "`n6️⃣ Updating Service..." -ForegroundColor Yellow
    $updateService = @{
        title = "PowerShell Test Service - UPDATED"
        description = "An updated service via PowerShell script"
        price = 249.99
        category = "TECHNOLOGY"
        providerId = "550e8400-e29b-41d4-a716-446655440001"
        isActive = $true
        averageRating = 4.8
    } | ConvertTo-Json

    try {
        $updatedService = Invoke-RestMethod -Uri "$baseUrl/api/v1/services/$serviceId" -Method Put -Body $updateService -Headers $headers
        Write-Host "✅ Service updated successfully!" -ForegroundColor Green
        Write-Host "New Title: $($updatedService.title)" -ForegroundColor Cyan
        Write-Host "New Price: $($updatedService.price)" -ForegroundColor Cyan
    } catch {
        Write-Host "❌ Service update failed: $($_.Exception.Message)" -ForegroundColor Red
    }

    # Step 7: Delete Service
    Write-Host "`n7️⃣ Deleting Service..." -ForegroundColor Yellow
    try {
        Invoke-RestMethod -Uri "$baseUrl/api/v1/services/$serviceId" -Method Delete -Headers $headers
        Write-Host "✅ Service deleted successfully!" -ForegroundColor Green
    } catch {
        Write-Host "❌ Service deletion failed: $($_.Exception.Message)" -ForegroundColor Red
    }
}

# Step 8: Final Check - Get All Services Again
Write-Host "`n8️⃣ Final Check - Getting All Services..." -ForegroundColor Yellow
try {
    $finalServices = Invoke-RestMethod -Uri "$baseUrl/api/v1/services" -Method Get
    Write-Host "✅ Final count: $($finalServices.Count) services" -ForegroundColor Green
} catch {
    Write-Host "❌ Final check failed: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "`n🎉 API Testing Complete!" -ForegroundColor Green
Write-Host "===============================================" -ForegroundColor Green
Write-Host "💡 Check Kafka UI at http://localhost:8090/ui/clusters/local/all-topics" -ForegroundColor Cyan
Write-Host "💡 Look for events in 'marketplace.service.events' topic" -ForegroundColor Cyan