@echo off
echo ================================================================================
echo                    Quick Servicios Microservice Test
echo ================================================================================
echo.

echo Starting your servicios microservice...
echo ================================================================================
cd /d "d:\Martin\Programas\Web\microoservicio_servicios\main\clase-11380-microservicio-servicios\microo"

echo 1. Starting Docker services (Kafka)...
docker-compose -f docker-compose-kafka.yml up -d
timeout /t 10 /nobreak

echo 2. Starting your microservice...
start "Servicios-Test" cmd /k "mvnw.cmd spring-boot:run"
timeout /t 20 /nobreak

echo 3. Testing API and Kafka events...
echo ================================================================================
echo Creating a test service to trigger Kafka event...

powershell -Command "try { $headers = @{'Content-Type' = 'application/json'}; $body = @{title = 'Integration Test Service'; description = 'Testing Eureka and Kafka integration'; price = 123.45; dtype = 'ServiceEntity'} | ConvertTo-Json; Write-Host 'Sending request to create service...' -ForegroundColor Yellow; $response = Invoke-RestMethod -Uri 'http://localhost:8080/api/services' -Method POST -Headers $headers -Body $body; Write-Host 'SUCCESS: Service created!' -ForegroundColor Green; Write-Host 'Response:' -ForegroundColor Cyan; $response | ConvertTo-Json -Depth 3; Write-Host 'Check Kafka UI for the published event on marketplace.service.events topic' -ForegroundColor Yellow } catch { Write-Host 'ERROR: Failed to create service' -ForegroundColor Red; Write-Host $_.Exception.Message -ForegroundColor Red }"

echo.
echo Opening monitoring dashboards...
start http://localhost:8761
start http://localhost:8090/ui/clusters/local/all-topics
start http://localhost:8080/actuator/health

echo.
echo ================================================================================
echo TEST COMPLETE - Check the opened browser tabs:
echo ================================================================================
echo 1. Eureka Dashboard (http://localhost:8761)
echo    - Should show 'microservicio-servicios' registered
echo.
echo 2. Kafka UI (http://localhost:8090)
echo    - Look for 'marketplace.service.events' topic
echo    - Should contain the event from service creation
echo.
echo 3. Service Health (http://localhost:8080/actuator/health)
echo    - Should show UP status
echo ================================================================================

pause