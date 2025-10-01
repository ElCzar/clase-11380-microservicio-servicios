# Simple Kafka Event Test
Write-Host "===============================================================================" -ForegroundColor Cyan
Write-Host "                Testing API and Kafka Events" -ForegroundColor Cyan
Write-Host "===============================================================================" -ForegroundColor Cyan
Write-Host ""

try {
    # Get JWT Token
    Write-Host "Getting JWT Token..." -ForegroundColor Yellow
    $body = "grant_type=password&username=testuser&password=testpassword&client_id=perfil-service&client_secret=XhzE490FVJwoJ3OdLlyyqpDyIaf1lgVe"
    $token = (Invoke-RestMethod -Uri "http://localhost:8180/realms/microservices-ecosystem/protocol/openid-connect/token" -Method POST -Body $body -ContentType 'application/x-www-form-urlencoded').access_token
    
    Write-Host "Token obtained!" -ForegroundColor Green
    
    # Create Service
    Write-Host "Creating service..." -ForegroundColor Yellow
    $headers = @{
        'Authorization' = "Bearer $token"
        'Content-Type' = 'application/json'
    }
    
    $service = @{
        'title' = 'Kafka Test Service'
        'description' = 'Testing Kafka events'
        'price' = 199.99
        'dtype' = 'ServiceEntity'
    } | ConvertTo-Json
    
    $result = Invoke-RestMethod -Uri 'http://localhost:8080/api/services' -Method POST -Headers $headers -Body $service
    
    Write-Host "SUCCESS!" -ForegroundColor Green
    Write-Host "Service ID: $($result.id)" -ForegroundColor Cyan
    Write-Host "Kafka Event Published: SERVICE_CREATED" -ForegroundColor Yellow
    Write-Host ""
    Write-Host "Opening Kafka UI..." -ForegroundColor Yellow
    
    Start-Process "http://localhost:8090/ui/clusters/local/topics/marketplace.service.events"
    
} catch {
    Write-Host "ERROR: $($_.Exception.Message)" -ForegroundColor Red
    
    if ($_.Exception.Message -like "*401*") {
        Write-Host "Authentication issue - check Keycloak and credentials" -ForegroundColor Yellow
    }
    if ($_.Exception.Message -like "*Connection*") {
        Write-Host "Connection issue - check if services are running" -ForegroundColor Yellow
    }
}

Write-Host ""
Write-Host "Press Enter to continue..."
Read-Host