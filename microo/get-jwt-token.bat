@echo off
setlocal enabledelayedexpansion

echo ================================================================================
echo                          JWT Token Generator for Microservices
echo ================================================================================
echo.

REM Configuration
set KEYCLOAK_URL=http://localhost:8180
set REALM=microservices-ecosystem
set CLIENT_ID=perfil-service
set CLIENT_SECRET=XhzE490FVJwoJ3OdLlyyqpDyIaf1lgVe

REM Default test user credentials
set DEFAULT_USERNAME=testuser
set DEFAULT_PASSWORD=testpassword

echo Current Configuration:
echo - Keycloak URL: %KEYCLOAK_URL%
echo - Realm: %REALM%
echo - Client ID: %CLIENT_ID%
echo.

REM Check if user wants to use default credentials or enter custom ones
set /p USE_DEFAULT="Use default test user (testuser/testpassword)? (Y/n): "
if /i "%USE_DEFAULT%"=="n" (
    set /p USERNAME="Enter username: "
    set /p PASSWORD="Enter password: "
) else (
    set USERNAME=%DEFAULT_USERNAME%
    set PASSWORD=%DEFAULT_PASSWORD%
)

echo.
echo Getting JWT token for user: %USERNAME%
echo.

REM Create PowerShell command to get token
set PS_COMMAND=^
try { ^
    $response = Invoke-RestMethod -Uri '%KEYCLOAK_URL%/realms/%REALM%/protocol/openid-connect/token' -Method POST -Body 'grant_type=password&username=%USERNAME%&password=%PASSWORD%&client_id=%CLIENT_ID%&client_secret=%CLIENT_SECRET%' -ContentType 'application/x-www-form-urlencoded'; ^
    Write-Host '========================================'; ^
    Write-Host 'SUCCESS: JWT Token obtained!'; ^
    Write-Host '========================================'; ^
    Write-Host ''; ^
    Write-Host 'Access Token:'; ^
    Write-Host $response.access_token; ^
    Write-Host ''; ^
    Write-Host 'Token Type:' $response.token_type; ^
    Write-Host 'Expires In:' $response.expires_in 'seconds'; ^
    Write-Host 'Refresh Token Available:' ($response.refresh_token -ne $null); ^
    Write-Host ''; ^
    Write-Host '========================================'; ^
    Write-Host 'Usage Examples:'; ^
    Write-Host '========================================'; ^
    Write-Host 'PowerShell:'; ^
    Write-Host '$headers = @{\"Authorization\" = \"Bearer \" + $response.access_token}'; ^
    Write-Host 'Invoke-RestMethod -Uri \"http://localhost:8080/api/v1/services\" -Headers $headers'; ^
    Write-Host ''; ^
    Write-Host 'cURL:'; ^
    Write-Host 'curl -H \"Authorization: Bearer ' + $response.access_token + '\" http://localhost:8080/api/v1/services'; ^
    Write-Host ''; ^
    Write-Host 'Saving token to file: jwt-token.txt'; ^
    $response.access_token ^| Out-File -FilePath 'jwt-token.txt' -Encoding UTF8; ^
    Write-Host 'Token saved successfully!'; ^
} catch { ^
    Write-Host '========================================'; ^
    Write-Host 'ERROR: Failed to get JWT token'; ^
    Write-Host '========================================'; ^
    Write-Host 'Error Message:' $_.Exception.Message; ^
    Write-Host ''; ^
    Write-Host 'Common Solutions:'; ^
    Write-Host '1. Make sure Keycloak is running at %KEYCLOAK_URL%'; ^
    Write-Host '2. Verify username/password are correct'; ^
    Write-Host '3. Check if the test user exists in Keycloak'; ^
    Write-Host '4. Ensure the client configuration is correct'; ^
    Write-Host ''; ^
    Write-Host 'To create test user, run: create-test-user.bat'; ^
}

REM Execute PowerShell command
powershell -NoProfile -ExecutionPolicy Bypass -Command "%PS_COMMAND%"

echo.
echo ================================================================================
echo Press any key to exit...
pause >nul