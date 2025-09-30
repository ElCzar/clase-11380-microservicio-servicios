@echo off
setlocal enabledelayedexpansion

echo ================================================================================
echo                          Create Test User in Keycloak
echo ================================================================================
echo.

REM Configuration
set KEYCLOAK_URL=http://localhost:8180
set REALM=microservices-ecosystem
set ADMIN_USERNAME=admin
set ADMIN_PASSWORD=admin

echo Creating test user in Keycloak...
echo - Keycloak URL: %KEYCLOAK_URL%
echo - Realm: %REALM%
echo.

REM Get user details
set /p USERNAME="Enter username for new user (default: testuser): "
if "%USERNAME%"=="" set USERNAME=testuser

set /p PASSWORD="Enter password for new user (default: testpassword): "
if "%PASSWORD%"=="" set PASSWORD=testpassword

set /p EMAIL="Enter email for new user (default: test@example.com): "
if "%EMAIL%"=="" set EMAIL=test@example.com

set /p FIRSTNAME="Enter first name (default: Test): "
if "%FIRSTNAME%"=="" set FIRSTNAME=Test

set /p LASTNAME="Enter last name (default: User): "
if "%LASTNAME%"=="" set LASTNAME=User

echo.
echo Creating user: %USERNAME% (%EMAIL%)
echo.

REM Create PowerShell command to create user
set PS_COMMAND=^
try { ^
    Write-Host 'Getting admin token...'; ^
    $adminToken = (Invoke-RestMethod -Uri '%KEYCLOAK_URL%/realms/master/protocol/openid-connect/token' -Method POST -Body 'grant_type=password&username=%ADMIN_USERNAME%&password=%ADMIN_PASSWORD%&client_id=admin-cli' -ContentType 'application/x-www-form-urlencoded').access_token; ^
    Write-Host 'Admin token obtained successfully.'; ^
    Write-Host ''; ^
    Write-Host 'Creating user...'; ^
    $userJson = @{ ^
        username = '%USERNAME%'; ^
        email = '%EMAIL%'; ^
        firstName = '%FIRSTNAME%'; ^
        lastName = '%LASTNAME%'; ^
        enabled = $true; ^
        credentials = @(@{ ^
            type = 'password'; ^
            value = '%PASSWORD%'; ^
            temporary = $false ^
        }) ^
    } ^| ConvertTo-Json -Depth 3; ^
    $headers = @{ ^
        'Authorization' = 'Bearer ' + $adminToken; ^
        'Content-Type' = 'application/json' ^
    }; ^
    Invoke-RestMethod -Uri '%KEYCLOAK_URL%/admin/realms/%REALM%/users' -Method POST -Body $userJson -Headers $headers; ^
    Write-Host '========================================'; ^
    Write-Host 'SUCCESS: User created successfully!'; ^
    Write-Host '========================================'; ^
    Write-Host 'Username:' '%USERNAME%'; ^
    Write-Host 'Password:' '%PASSWORD%'; ^
    Write-Host 'Email:' '%EMAIL%'; ^
    Write-Host 'Full Name:' '%FIRSTNAME% %LASTNAME%'; ^
    Write-Host ''; ^
    Write-Host 'You can now use get-jwt-token.bat to obtain a JWT token.'; ^
} catch { ^
    if ($_.Exception.Response.StatusCode -eq 'Conflict') { ^
        Write-Host '========================================'; ^
        Write-Host 'INFO: User already exists'; ^
        Write-Host '========================================'; ^
        Write-Host 'Username:' '%USERNAME%' 'already exists in the system.'; ^
        Write-Host 'You can still use this username with get-jwt-token.bat'; ^
    } else { ^
        Write-Host '========================================'; ^
        Write-Host 'ERROR: Failed to create user'; ^
        Write-Host '========================================'; ^
        Write-Host 'Error:' $_.Exception.Message; ^
        Write-Host ''; ^
        Write-Host 'Common Solutions:'; ^
        Write-Host '1. Make sure Keycloak is running at %KEYCLOAK_URL%'; ^
        Write-Host '2. Verify admin credentials (admin/admin)'; ^
        Write-Host '3. Check if Keycloak is fully started'; ^
        Write-Host '4. Try accessing %KEYCLOAK_URL% in your browser'; ^
    } ^
}

REM Execute PowerShell command
powershell -NoProfile -ExecutionPolicy Bypass -Command "%PS_COMMAND%"

echo.
echo ================================================================================
echo Press any key to exit...
pause >nul