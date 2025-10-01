# Kafka Event Testing Script for OrdenPago Integration
# This script tests if your servicios microservice publishes events correctly

Write-Host "=================================================================================" -ForegroundColor Cyan
Write-Host "                    Kafka Event Testing for OrdenPago Integration" -ForegroundColor Cyan  
Write-Host "=================================================================================" -ForegroundColor Cyan
Write-Host ""

# Test 1: Service Creation
Write-Host "🚀 Test 1: Creating a service to trigger SERVICE_CREATED event..." -ForegroundColor Yellow
Write-Host ""

$headers = @{'Content-Type' = 'application/json'}
$createPayload = @{
    title = 'OrdenPago Integration Test'
    description = 'Testing Kafka events for ordenpago consumption'
    price = 150.00
    dtype = 'ServiceEntity'
} | ConvertTo-Json

try {
    Write-Host "Sending CREATE request..." -ForegroundColor White
    $createResponse = Invoke-RestMethod -Uri 'http://localhost:8080/api/services' -Method POST -Headers $headers -Body $createPayload
    Write-Host "✅ SERVICE CREATED SUCCESSFULLY!" -ForegroundColor Green
    Write-Host "Service ID: $($createResponse.id)" -ForegroundColor Cyan
    Write-Host "📤 Kafka event published to: marketplace.service.events" -ForegroundColor Yellow
    $serviceId = $createResponse.id
    Write-Host ""
} catch {
    Write-Host "❌ ERROR: Failed to create service" -ForegroundColor Red
    Write-Host $_.Exception.Message -ForegroundColor Red
    exit 1
}

# Wait a moment for event processing
Start-Sleep -Seconds 2

# Test 2: Service Update
Write-Host "🔄 Test 2: Updating the service to trigger SERVICE_UPDATED event..." -ForegroundColor Yellow
Write-Host ""

$updatePayload = @{
    id = $serviceId
    title = 'UPDATED - OrdenPago Integration Test'
    description = 'Updated service to test UPDATED event for ordenpago'
    price = 199.99
    dtype = 'ServiceEntity'
} | ConvertTo-Json

try {
    Write-Host "Sending UPDATE request..." -ForegroundColor White
    $updateResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/services/$serviceId" -Method PUT -Headers $headers -Body $updatePayload
    Write-Host "✅ SERVICE UPDATED SUCCESSFULLY!" -ForegroundColor Green
    Write-Host "📤 Another Kafka event published to: marketplace.service.events" -ForegroundColor Yellow
    Write-Host ""
} catch {
    Write-Host "⚠️  WARNING: Failed to update service" -ForegroundColor Yellow
    Write-Host $_.Exception.Message -ForegroundColor Yellow
}

# Wait for event processing
Start-Sleep -Seconds 2

# Test 3: Service Deletion
Write-Host "🗑️  Test 3: Deleting the service to trigger SERVICE_DELETED event..." -ForegroundColor Yellow
Write-Host ""

try {
    Write-Host "Sending DELETE request..." -ForegroundColor White
    Invoke-RestMethod -Uri "http://localhost:8080/api/services/$serviceId" -Method DELETE
    Write-Host "✅ SERVICE DELETED SUCCESSFULLY!" -ForegroundColor Green
    Write-Host "📤 DELETE Kafka event published to: marketplace.service.events" -ForegroundColor Yellow
    Write-Host ""
} catch {
    Write-Host "⚠️  WARNING: Failed to delete service" -ForegroundColor Yellow
    Write-Host $_.Exception.Message -ForegroundColor Yellow
}

# Summary and next steps
Write-Host "=================================================================================" -ForegroundColor Cyan
Write-Host "                              TEST SUMMARY" -ForegroundColor Cyan
Write-Host "=================================================================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "📊 Events Published:" -ForegroundColor White
Write-Host "   • SERVICE_CREATED event" -ForegroundColor Green
Write-Host "   • SERVICE_UPDATED event" -ForegroundColor Green  
Write-Host "   • SERVICE_DELETED event" -ForegroundColor Green
Write-Host ""
Write-Host "🎯 Topic: marketplace.service.events" -ForegroundColor Cyan
Write-Host "📋 Content-Type: application/json" -ForegroundColor Cyan
Write-Host ""
Write-Host "🔍 Next Steps - Verify in Kafka UI:" -ForegroundColor Yellow
Write-Host "   1. Open: http://localhost:8090/ui/clusters/local/topics/marketplace.service.events" -ForegroundColor White
Write-Host "   2. Check for 3 messages (CREATE, UPDATE, DELETE)" -ForegroundColor White
Write-Host "   3. Verify message structure contains ServiceEventDto" -ForegroundColor White
Write-Host ""
Write-Host "💡 For OrdenPago Integration:" -ForegroundColor Yellow
Write-Host "   The ordenpago microservice needs a consumer like:" -ForegroundColor White
Write-Host "   @Bean" -ForegroundColor Green
Write-Host "   public Consumer<ServiceEventDto> handleMarketplaceEvents() {" -ForegroundColor Green
Write-Host "       return event -> processServiceEvent(event);" -ForegroundColor Green
Write-Host "   }" -ForegroundColor Green
Write-Host ""

# Open Kafka UI
Write-Host "🌐 Opening Kafka UI to verify events..." -ForegroundColor Cyan
Start-Process "http://localhost:8090/ui/clusters/local/topics/marketplace.service.events"

Write-Host "Press any key to continue..." -ForegroundColor Gray
$null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")