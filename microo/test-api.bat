@echo off
setlocal enabledelayedexpansion

echo ================================================================================
echo                          Test API with JWT Token
echo ================================================================================
echo.

set API_BASE_URL=http://localhost:8080
set TOKEN_FILE=jwt-token.txt

REM Check if token file exists
if not exist "%TOKEN_FILE%" (
    echo ERROR: No JWT token file found!
    echo Please run get-jwt-token.bat first to obtain a token.
    echo.
    goto :end
)

REM Read token from file
set /p JWT_TOKEN=<"%TOKEN_FILE%"

echo Using JWT token from: %TOKEN_FILE%
echo API Base URL: %API_BASE_URL%
echo.

echo Available API endpoints to test:
echo 1. GET  /api/v1/services - List all services
echo 2. POST /api/v1/services - Create a new service
echo 3. GET  /actuator/health - Health check (no auth needed)
echo 4. GET  /h2-console - H2 Database console
echo 5. POST /graphql - GraphQL endpoint
echo 6. Custom endpoint
echo.

set /p CHOICE="Select an option (1-6): "

if "%CHOICE%"=="1" (
    set ENDPOINT=/api/v1/services
    set METHOD=GET
    set BODY=
) else if "%CHOICE%"=="2" (
    set ENDPOINT=/api/v1/services
    set METHOD=POST
    set BODY={"title":"Test Service","description":"A test service","price":99.99,"category":{"name":"Testing"},"country":{"name":"Test Country"},"status":{"name":"Active"}}
) else if "%CHOICE%"=="3" (
    set ENDPOINT=/actuator/health
    set METHOD=GET
    set BODY=
    set JWT_TOKEN=
) else if "%CHOICE%"=="4" (
    set ENDPOINT=/h2-console
    set METHOD=GET
    set BODY=
) else if "%CHOICE%"=="5" (
    set ENDPOINT=/graphql
    set METHOD=POST
    set BODY={"query":"{ __schema { types { name } } }"}
) else if "%CHOICE%"=="6" (
    set /p ENDPOINT="Enter endpoint (e.g., /api/v1/services): "
    set /p METHOD="Enter HTTP method (GET/POST/PUT/DELETE): "
    set /p BODY="Enter request body (leave empty for GET): "
) else (
    echo Invalid choice!
    goto :end
)

echo.
echo Testing API...
echo Method: %METHOD%
echo Endpoint: %API_BASE_URL%%ENDPOINT%
if "%BODY%" neq "" echo Body: %BODY%
echo.

REM Create PowerShell command
if "%JWT_TOKEN%" neq "" (
    set AUTH_HEADER=Authorization = "Bearer %JWT_TOKEN%";
) else (
    set AUTH_HEADER=
)

if "%BODY%" neq "" (
    set PS_COMMAND=^
    try { ^
        $headers = @{ ^
            %AUTH_HEADER% ^
            "Content-Type" = "application/json" ^
        }; ^
        $response = Invoke-RestMethod -Uri '%API_BASE_URL%%ENDPOINT%' -Method %METHOD% -Headers $headers -Body '%BODY%'; ^
        Write-Host '========================================'; ^
        Write-Host 'SUCCESS: API call completed!'; ^
        Write-Host '========================================'; ^
        Write-Host 'Response:'; ^
        $response ^| ConvertTo-Json -Depth 10; ^
    } catch { ^
        Write-Host '========================================'; ^
        Write-Host 'ERROR: API call failed'; ^
        Write-Host '========================================'; ^
        Write-Host 'Status Code:' $_.Exception.Response.StatusCode; ^
        Write-Host 'Error:' $_.Exception.Message; ^
        if ($_.Exception.Response.StatusCode -eq 'Unauthorized') { ^
            Write-Host ''; ^
            Write-Host 'Unauthorized error - your JWT token might be expired.'; ^
            Write-Host 'Please run get-jwt-token.bat to get a new token.'; ^
        } ^
    }
) else (
    set PS_COMMAND=^
    try { ^
        $headers = @{ ^
            %AUTH_HEADER% ^
            "Content-Type" = "application/json" ^
        }; ^
        $response = Invoke-RestMethod -Uri '%API_BASE_URL%%ENDPOINT%' -Method %METHOD% -Headers $headers; ^
        Write-Host '========================================'; ^
        Write-Host 'SUCCESS: API call completed!'; ^
        Write-Host '========================================'; ^
        Write-Host 'Response:'; ^
        $response ^| ConvertTo-Json -Depth 10; ^
    } catch { ^
        Write-Host '========================================'; ^
        Write-Host 'ERROR: API call failed'; ^
        Write-Host '========================================'; ^
        Write-Host 'Status Code:' $_.Exception.Response.StatusCode; ^
        Write-Host 'Error:' $_.Exception.Message; ^
        if ($_.Exception.Response.StatusCode -eq 'Unauthorized') { ^
            Write-Host ''; ^
            Write-Host 'Unauthorized error - your JWT token might be expired.'; ^
            Write-Host 'Please run get-jwt-token.bat to get a new token.'; ^
        } ^
    }
)

REM Execute PowerShell command
powershell -NoProfile -ExecutionPolicy Bypass -Command "%PS_COMMAND%"

:end
echo.
echo ================================================================================
echo Press any key to exit...
pause >nul