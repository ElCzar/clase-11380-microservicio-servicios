@echo off
cls
setlocal enabledelayedexpansion

echo ================================================================================
echo                    Microservices Development Helper
echo ================================================================================
echo.
echo This helper provides easy access to all development tools for your microservice.
echo.
echo Current Services Status:
echo ------------------------

REM Check if services are running
powershell -NoProfile -Command "try { $keycloak = (Invoke-WebRequest -Uri 'http://localhost:8180' -TimeoutSec 2).StatusCode } catch { $keycloak = 'DOWN' }; try { $microservice = (Invoke-WebRequest -Uri 'http://localhost:8080/actuator/health' -TimeoutSec 2).StatusCode } catch { $microservice = 'DOWN' }; try { $kafka = (Invoke-WebRequest -Uri 'http://localhost:8090' -TimeoutSec 2).StatusCode } catch { $kafka = 'DOWN' }; Write-Host 'Keycloak (8180):    ' $(if($keycloak -eq 200){'✓ RUNNING'}else{'✗ DOWN'}); Write-Host 'Microservice (8080): ' $(if($microservice -eq 200){'✓ RUNNING'}else{'✗ DOWN'}); Write-Host 'Kafka UI (8090):     ' $(if($kafka -eq 200){'✓ RUNNING'}else{'✗ DOWN'})"

echo.
echo Available Options:
echo ==================
echo.
echo 🔐 Authentication:
echo   1. Create test user in Keycloak
echo   2. Get JWT token
echo.
echo 🧪 API Testing:
echo   3. Test API endpoints with JWT
echo.
echo 🚀 Service Management:
echo   4. Start Keycloak
echo   5. Start Kafka infrastructure
echo   6. Start microservice
echo   7. Stop all services
echo.
echo 🌐 Web Interfaces:
echo   8. Open Keycloak Admin Console
echo   9. Open Kafka UI
echo   10. Open H2 Database Console
echo.
echo 📋 Other Options:
echo   11. View logs
echo   12. Show configuration
echo   0. Exit
echo.

set /p CHOICE="Select an option (0-12): "

if "%CHOICE%"=="1" (
    echo.
    echo Starting create-test-user.bat...
    call create-test-user.bat
    goto :menu
) else if "%CHOICE%"=="2" (
    echo.
    echo Starting get-jwt-token.bat...
    call get-jwt-token.bat
    goto :menu
) else if "%CHOICE%"=="3" (
    echo.
    echo Starting test-api.bat...
    call test-api.bat
    goto :menu
) else if "%CHOICE%"=="4" (
    echo.
    echo Starting Keycloak...
    cd ..\clase-11380-microservicio-perfil
    docker-compose up keycloak -d
    cd ..\clase-11380-microservicio-servicios\microo
    echo Keycloak started! Access at: http://localhost:8180
    pause
    goto :menu
) else if "%CHOICE%"=="5" (
    echo.
    echo Starting Kafka infrastructure...
    docker-compose -f docker-compose-kafka.yml up -d
    echo Kafka infrastructure started! Kafka UI at: http://localhost:8090
    pause
    goto :menu
) else if "%CHOICE%"=="6" (
    echo.
    echo Starting microservice...
    echo Note: This will start in the current window. Press Ctrl+C to stop.
    pause
    mvn spring-boot:run
    goto :menu
) else if "%CHOICE%"=="7" (
    echo.
    echo Stopping all services...
    docker-compose -f docker-compose-kafka.yml down
    cd ..\clase-11380-microservicio-perfil
    docker-compose down
    cd ..\clase-11380-microservicio-servicios\microo
    echo All Docker services stopped!
    pause
    goto :menu
) else if "%CHOICE%"=="8" (
    echo.
    echo Opening Keycloak Admin Console in browser...
    start http://localhost:8180
    echo Login with: admin / admin
    pause
    goto :menu
) else if "%CHOICE%"=="9" (
    echo.
    echo Opening Kafka UI in browser...
    start http://localhost:8090
    pause
    goto :menu
) else if "%CHOICE%"=="10" (
    echo.
    echo Opening H2 Database Console in browser...
    start http://localhost:8080/h2-console
    echo.
    echo H2 Console Connection Details:
    echo JDBC URL: jdbc:h2:mem:marketplace-db
    echo User Name: sa
    echo Password: password
    pause
    goto :menu
) else if "%CHOICE%"=="11" (
    echo.
    echo Select logs to view:
    echo 1. Keycloak logs
    echo 2. Kafka logs
    echo 3. Microservice logs (if running in Docker)
    echo.
    set /p LOG_CHOICE="Select (1-3): "
    if "!LOG_CHOICE!"=="1" (
        cd ..\clase-11380-microservicio-perfil
        docker-compose logs -f keycloak
        cd ..\clase-11380-microservicio-servicios\microo
    ) else if "!LOG_CHOICE!"=="2" (
        docker-compose -f docker-compose-kafka.yml logs -f kafka
    ) else if "!LOG_CHOICE!"=="3" (
        echo Microservice logs would be shown here if running in Docker
        pause
    )
    goto :menu
) else if "%CHOICE%"=="12" (
    echo.
    echo ================================================================================
    echo                              Current Configuration
    echo ================================================================================
    echo.
    echo 🔐 Keycloak Configuration:
    echo   URL: http://localhost:8180
    echo   Admin: admin / admin
    echo   Realm: microservices-ecosystem
    echo   Client ID: perfil-service
    echo   Client Secret: XhzE490FVJwoJ3OdLlyyqpDyIaf1lgVe
    echo.
    echo 🚀 Microservice Configuration:
    echo   URL: http://localhost:8080
    echo   Health Check: http://localhost:8080/actuator/health
    echo   API Base: http://localhost:8080/api/v1
    echo   GraphQL: http://localhost:8080/graphql
    echo   H2 Console: http://localhost:8080/h2-console
    echo.
    echo 📊 Kafka Configuration:
    echo   Broker: localhost:9092
    echo   Zookeeper: localhost:2181
    echo   UI: http://localhost:8090
    echo.
    echo 💾 Database Configuration:
    echo   Type: H2 In-Memory
    echo   URL: jdbc:h2:mem:marketplace-db
    echo   Username: sa
    echo   Password: password
    echo.
    echo ================================================================================
    pause
    goto :menu
) else if "%CHOICE%"=="0" (
    echo.
    echo Goodbye!
    exit /b 0
) else (
    echo.
    echo Invalid choice! Please try again.
    pause
    goto :menu
)

:menu
echo.
echo Press any key to return to main menu...
pause >nul
cls
goto :eof