@echo off
echo ===============================================================================
echo                      Testing API Without Eureka Dependency
echo ===============================================================================
echo.

echo Step 1: Getting JWT Token...
powershell -Command ^
"$body = 'grant_type=password&username=testuser&password=testpassword&client_id=perfil-service&client_secret=XhzE490FVJwoJ3OdLlyyqpDyIaf1lgVe'; ^
$token = (Invoke-RestMethod -Uri 'http://localhost:8180/realms/microservices-ecosystem/protocol/openid-connect/token' -Method POST -Body $body -ContentType 'application/x-www-form-urlencoded').access_token; ^
echo 'Token obtained successfully'; ^
$headers = @{'Authorization' = \"Bearer $token\"; 'Content-Type' = 'application/json'}; ^
$service = @{title = 'Kafka Test Service'; description = 'Testing without Eureka'; price = 199.99; dtype = 'ServiceEntity'} | ConvertTo-Json; ^
$result = Invoke-RestMethod -Uri 'http://localhost:8080/api/services' -Method POST -Headers $headers -Body $service; ^
Write-Host \"Service created with ID: $($result.id)\" -ForegroundColor Green; ^
Write-Host \"Kafka event published to marketplace.service.events\" -ForegroundColor Yellow"

echo.
echo Step 2: Opening Kafka UI...
start http://localhost:8090/ui/clusters/local/topics/marketplace.service.events

echo.
echo If this works, the issue is with Eureka configuration, not your API.
pause