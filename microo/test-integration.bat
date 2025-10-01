@echo off
echo ================================================================================
echo                    Microservices Integration Test Script
echo ================================================================================
echo.
echo This script will help you test the Eureka and Kafka integration between:
echo - microservicio-servicios (your service)
echo - microservicio-perfil (Eureka server)  
echo - microservicio-ordenpago (Kafka consumer)
echo.

echo Step 1: Starting Kafka and related services...
echo ================================================================================
cd /d "d:\Martin\Programas\Web\microoservicio_servicios\main\clase-11380-microservicio-servicios\microo"
echo Starting Docker Compose for Kafka...
docker-compose -f docker-compose-kafka.yml up -d
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Failed to start Kafka services
    pause
    exit /b 1
)
echo Kafka services started successfully!
echo.

echo Step 2: Wait for Kafka to be ready...
echo ================================================================================
timeout /t 10 /nobreak
echo.

echo Step 3: Starting Eureka Server (perfil microservice)...
echo ================================================================================
cd /d "d:\Martin\Programas\Web\microoservicio_servicios\main\clase-11380-microservicio-perfil\microservicio_perfil"
echo Starting perfil microservice (Eureka Server) on port 8081...
start "Perfil-Eureka-Server" cmd /k "mvnw.cmd spring-boot:run"
timeout /t 15 /nobreak
echo.

echo Step 4: Starting your servicios microservice...
echo ================================================================================
cd /d "d:\Martin\Programas\Web\microoservicio_servicios\main\clase-11380-microservicio-servicios\microo"
echo Starting servicios microservice (Eureka Client) on port 8080...
start "Servicios-Microservice" cmd /k "mvnw.cmd spring-boot:run"
timeout /t 15 /nobreak
echo.

echo Step 5: Starting ordenpago microservice...
echo ================================================================================
cd /d "d:\Martin\Programas\Web\microoservicio_servicios\main\clase-11380-microservicio-ordenpago\demo"
echo Starting ordenpago microservice (Kafka Consumer) on port 8084...
start "OrdenPago-Microservice" cmd /k "mvnw.cmd spring-boot:run"
timeout /t 15 /nobreak
echo.

echo Step 6: Testing Eureka Registration...
echo ================================================================================
echo Opening Eureka Dashboard...
start http://localhost:8761
echo.
echo Please check that 'microservicio-servicios' appears in the Eureka dashboard
pause

echo Step 7: Testing Kafka Integration...
echo ================================================================================
echo Opening Kafka UI to monitor topics...
start http://localhost:8090
echo.
echo Now testing API call to create a service (this should trigger a Kafka event)...
echo.

powershell -Command "try { $headers = @{'Content-Type' = 'application/json'}; $body = @{title = 'Test Service for Kafka'; description = 'Testing Spring Cloud Stream event publishing'; price = 99.99; dtype = 'ServiceEntity'} | ConvertTo-Json; $response = Invoke-RestMethod -Uri 'http://localhost:8080/api/services' -Method POST -Headers $headers -Body $body; Write-Host 'Service created successfully:' -ForegroundColor Green; $response | ConvertTo-Json -Depth 3 } catch { Write-Host 'Error creating service:' -ForegroundColor Red; $_.Exception.Message }"

echo.
echo ================================================================================
echo                           Integration Test Results
echo ================================================================================
echo.
echo Please verify the following:
echo.
echo 1. EUREKA INTEGRATION:
echo    - Open http://localhost:8761
echo    - Check that 'microservicio-servicios' is registered
echo.
echo 2. KAFKA INTEGRATION:
echo    - Open http://localhost:8090
echo    - Go to Topics and look for 'marketplace.service.events'
echo    - You should see the event message from the service creation
echo.
echo 3. MICROSERVICE COMMUNICATION:
echo    - Check the ordenpago microservice logs for any consumed messages
echo    - Look for Spring Cloud Stream consumer logs
echo.
echo Press any key to open all monitoring URLs...
pause

start http://localhost:8761
start http://localhost:8090/ui/clusters/local/all-topics
start http://localhost:8080/actuator/health
start http://localhost:8081/actuator/health
start http://localhost:8084/actuator/health

echo.
echo ================================================================================
echo All monitoring URLs opened. Check the status of all services!
echo ================================================================================
pause