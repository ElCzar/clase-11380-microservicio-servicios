@echo off
echo ================================================================================
echo                    Kafka Event Testing for OrdenPago Integration
echo ================================================================================
echo.
echo This script will test if your servicios microservice is correctly publishing
echo Kafka events that the ordenpago microservice can consume.
echo.

echo Step 1: Starting Kafka Infrastructure...
echo ================================================================================
cd /d "d:\Martin\Programas\Web\microoservicio_servicios\main\clase-11380-microservicio-servicios\microo"
echo Starting Docker Compose for Kafka...
docker-compose -f docker-compose-kafka.yml up -d

if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Failed to start Kafka services
    pause
    exit /b 1
)

echo Waiting for Kafka to be ready...
timeout /t 15 /nobreak
echo.

echo Step 2: Starting Your Servicios Microservice...
echo ================================================================================
echo Starting servicios microservice with Spring Cloud Stream...
start "Servicios-Producer" cmd /k "mvnw.cmd spring-boot:run"
echo Waiting for microservice to start...
timeout /t 25 /nobreak
echo.

echo Step 3: Opening Kafka Monitoring...
echo ================================================================================
echo Opening Kafka UI to monitor topics and messages...
start http://localhost:8090/ui/clusters/local/all-topics
timeout /t 3
echo.
echo Please check the Kafka UI for existing topics.
echo Look for: marketplace.service.events
pause

echo Step 4: Testing Service Creation (Kafka Event Publishing)...
echo ================================================================================
echo Creating a test service to trigger Kafka event...

powershell -Command "
$headers = @{'Content-Type' = 'application/json'}
$testService = @{
    title = 'Kafka Integration Test Service'
    description = 'Testing event publishing to marketplace.service.events topic'
    price = 299.99
    dtype = 'ServiceEntity'
} | ConvertTo-Json

Write-Host 'Sending POST request to create service...' -ForegroundColor Yellow
Write-Host 'Payload:' -ForegroundColor Cyan
Write-Host $testService -ForegroundColor White

try {
    $response = Invoke-RestMethod -Uri 'http://localhost:8080/api/services' -Method POST -Headers $headers -Body $testService
    Write-Host '' 
    Write-Host '=== SERVICE CREATED SUCCESSFULLY ===' -ForegroundColor Green
    Write-Host 'Response:' -ForegroundColor Cyan
    $response | ConvertTo-Json -Depth 3
    Write-Host ''
    Write-Host 'EVENT SHOULD BE PUBLISHED TO: marketplace.service.events' -ForegroundColor Yellow
} catch {
    Write-Host 'ERROR: Failed to create service' -ForegroundColor Red
    Write-Host $_.Exception.Message -ForegroundColor Red
    if ($_.Exception.Response) {
        Write-Host 'Status Code:' $_.Exception.Response.StatusCode -ForegroundColor Red
    }
}
"

echo.
echo Step 5: Testing Service Update (Another Kafka Event)...
echo ================================================================================
powershell -Command "
try {
    # First get all services to find one to update
    $services = Invoke-RestMethod -Uri 'http://localhost:8080/api/services' -Method GET
    if ($services -and $services.Count -gt 0) {
        $serviceToUpdate = $services[0]
        $updatePayload = @{
            id = $serviceToUpdate.id
            title = 'UPDATED - Kafka Test Service'
            description = 'Updated service to test Kafka event publishing'
            price = 399.99
            dtype = 'ServiceEntity'
        } | ConvertTo-Json
        
        Write-Host 'Updating service with ID:' $serviceToUpdate.id -ForegroundColor Yellow
        $updateResponse = Invoke-RestMethod -Uri ('http://localhost:8080/api/services/' + $serviceToUpdate.id) -Method PUT -Headers @{'Content-Type' = 'application/json'} -Body $updatePayload
        
        Write-Host ''
        Write-Host '=== SERVICE UPDATED SUCCESSFULLY ===' -ForegroundColor Green
        Write-Host 'ANOTHER EVENT PUBLISHED TO: marketplace.service.events' -ForegroundColor Yellow
    } else {
        Write-Host 'No services found to update' -ForegroundColor Yellow
    }
} catch {
    Write-Host 'Update failed:' $_.Exception.Message -ForegroundColor Red
}
"

echo.
echo Step 6: Kafka Topic Verification...
echo ================================================================================
echo Now let's verify the events were published to Kafka...
echo.
echo Opening Kafka UI - Check the marketplace.service.events topic for messages
start http://localhost:8090/ui/clusters/local/topics/marketplace.service.events

echo.
echo You should see:
echo - Topic: marketplace.service.events
echo - Messages: At least 2 messages (CREATE and UPDATE events)
echo - Message content: JSON with ServiceEventDto structure
echo.
pause

echo Step 7: Manual Kafka Consumer Test...
echo ================================================================================
echo Let's also test with a manual Kafka consumer to see the raw messages...
echo.

docker exec -it microo-kafka-1 kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic marketplace.service.events --from-beginning --timeout-ms 10000

echo.
echo Step 8: Simulating OrdenPago Consumer...
echo ================================================================================
echo Now let's simulate what ordenpago microservice should receive...
echo.

powershell -Command "
Write-Host 'Simulating OrdenPago Kafka Consumer...' -ForegroundColor Cyan
Write-Host 'This is what ordenpago microservice should implement:' -ForegroundColor Yellow
Write-Host ''
Write-Host '@Bean' -ForegroundColor Green
Write-Host 'public Consumer<ServiceEventDto> handleMarketplaceEvents() {' -ForegroundColor Green
Write-Host '    return event -> {' -ForegroundColor Green
Write-Host '        log.info(\"Received service event: {}\", event);' -ForegroundColor Green
Write-Host '        // Process the event (create order, update payment, etc.)' -ForegroundColor Green
Write-Host '    };' -ForegroundColor Green
Write-Host '}' -ForegroundColor Green
Write-Host ''
Write-Host 'And in application.yml:' -ForegroundColor Yellow
Write-Host 'handleMarketplaceEvents-in-0:' -ForegroundColor Green
Write-Host '  destination: marketplace.service.events' -ForegroundColor Green
Write-Host '  content-type: application/json' -ForegroundColor Green
Write-Host '  group: ordenpago-consumer-group' -ForegroundColor Green
"

echo.
echo ================================================================================
echo                           KAFKA INTEGRATION TEST RESULTS
echo ================================================================================
echo.
echo WHAT TO VERIFY:
echo.
echo 1. KAFKA UI (http://localhost:8090):
echo    ✓ Topic 'marketplace.service.events' exists
echo    ✓ Messages are present in the topic
echo    ✓ Messages contain ServiceEventDto structure
echo.
echo 2. MESSAGE STRUCTURE should look like:
echo    {
echo      "serviceId": "uuid-here",
echo      "eventType": "SERVICE_CREATED" or "SERVICE_UPDATED",
echo      "timestamp": "2025-09-30T...",
echo      "service": { ... service details ... }
echo    }
echo.
echo 3. FOR ORDENPAGO INTEGRATION:
echo    - The topic name matches: marketplace.service.events
echo    - Content-type is: application/json
echo    - Events are properly formatted JSON
echo.
echo ================================================================================
echo Your Kafka event publishing is working if you see messages in the topic!
echo ================================================================================

pause