# JWT Token Testing Script - Different Roles and Clients
Write-Host "===============================================================================" -ForegroundColor Cyan
Write-Host "                    JWT Token Role Comparison Script" -ForegroundColor Cyan
Write-Host "===============================================================================" -ForegroundColor Cyan
Write-Host ""

$KEYCLOAK_URL = "http://localhost:8180"
$REALM = "microservices-ecosystem"

# Test different client configurations
$clients = @(
    @{
        name = "perfil-service"
        client_id = "perfil-service"
        client_secret = "XhzE490FVJwoJ3OdLlyyqpDyIaf1lgVe"
    },
    @{
        name = "microo-service"
        client_id = "microo-service" 
        client_secret = "vbWn3RLXfcKVZ5zE7zOxf26XCnZxklxC"
    }
)

$users = @(
    @{
        name = "testuser"
        username = "testuser"
        password = "testpassword"
    },
    @{
        name = "admin (if exists)"
        username = "admin"
        password = "admin"
    },
    @{
        name = "provider (if exists)"
        username = "provider"
        password = "provider"
    }
)

foreach ($client in $clients) {
    Write-Host "Testing Client: $($client.name)" -ForegroundColor Yellow
    Write-Host "===============================================================================" -ForegroundColor Yellow
    
    foreach ($user in $users) {
        Write-Host ""
        Write-Host "User: $($user.name)" -ForegroundColor White
        Write-Host "---" -ForegroundColor Gray
        
        try {
            $body = "grant_type=password&username=$($user.username)&password=$($user.password)&client_id=$($client.client_id)&client_secret=$($client.client_secret)"
            
            $response = Invoke-RestMethod -Uri "$KEYCLOAK_URL/realms/$REALM/protocol/openid-connect/token" -Method POST -Body $body -ContentType 'application/x-www-form-urlencoded'
            
            # Extract and decode JWT payload (basic decoding)
            $token = $response.access_token
            $parts = $token.Split('.')
            $payload = $parts[1]
            
            # Add padding if needed
            while ($payload.Length % 4) {
                $payload += "="
            }
            
            $decodedBytes = [System.Convert]::FromBase64String($payload)
            $decodedText = [System.Text.Encoding]::UTF8.GetString($decodedBytes)
            $tokenData = $decodedText | ConvertFrom-Json
            
            Write-Host "✅ SUCCESS - Token obtained" -ForegroundColor Green
            Write-Host "User: $($tokenData.preferred_username)" -ForegroundColor Cyan
            Write-Host "Issuer: $($tokenData.iss)" -ForegroundColor Cyan
            Write-Host "Client: $($tokenData.azp)" -ForegroundColor Cyan
            
            # Extract roles
            if ($tokenData.realm_access -and $tokenData.realm_access.roles) {
                Write-Host "Realm Roles:" -ForegroundColor Magenta
                foreach ($role in $tokenData.realm_access.roles) {
                    if ($role -eq "PROVIDER" -or $role -eq "ADMIN") {
                        Write-Host "  🔑 $role" -ForegroundColor Green
                    } else {
                        Write-Host "  • $role" -ForegroundColor White
                    }
                }
            }
            
            if ($tokenData.resource_access) {
                Write-Host "Resource Roles:" -ForegroundColor Magenta
                foreach ($resource in $tokenData.resource_access.PSObject.Properties) {
                    Write-Host "  Resource: $($resource.Name)" -ForegroundColor Cyan
                    foreach ($role in $resource.Value.roles) {
                        if ($role -eq "PROVIDER" -or $role -eq "ADMIN") {
                            Write-Host "    🔑 $role" -ForegroundColor Green
                        } else {
                            Write-Host "    • $role" -ForegroundColor White
                        }
                    }
                }
            }
            
            # Test API access
            Write-Host "Testing API Access:" -ForegroundColor Yellow
            $headers = @{ 'Authorization' = "Bearer $token" }
            
            try {
                $healthResponse = Invoke-RestMethod -Uri 'http://localhost:8080/api/v1/services/health' -Headers $headers
                Write-Host "  ✅ Health endpoint: $healthResponse" -ForegroundColor Green
            } catch {
                Write-Host "  ❌ Health endpoint failed: $($_.Exception.Message)" -ForegroundColor Red
            }
            
            try {
                $serviceBody = '{"title":"Test Service","description":"Testing roles","price":99.99,"dtype":"ServiceEntity"}'
                $serviceHeaders = @{ 'Authorization' = "Bearer $token"; 'Content-Type' = 'application/json' }
                $serviceResponse = Invoke-RestMethod -Uri 'http://localhost:8080/api/v1/services' -Method POST -Headers $serviceHeaders -Body $serviceBody
                Write-Host "  ✅ Create service: SUCCESS (ID: $($serviceResponse.id))" -ForegroundColor Green
            } catch {
                if ($_.Exception.Message -like "*403*") {
                    Write-Host "  ⚠️  Create service: 403 Forbidden (needs PROVIDER/ADMIN role)" -ForegroundColor Yellow
                } else {
                    Write-Host "  ❌ Create service failed: $($_.Exception.Message)" -ForegroundColor Red
                }
            }
            
        } catch {
            Write-Host "❌ FAILED - Could not get token" -ForegroundColor Red
            Write-Host "Error: $($_.Exception.Message)" -ForegroundColor Red
        }
    }
    Write-Host ""
}

Write-Host "===============================================================================" -ForegroundColor Cyan
Write-Host "                              ROLE SUMMARY" -ForegroundColor Cyan
Write-Host "===============================================================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "🔑 Required Roles for API Endpoints:" -ForegroundColor Yellow
Write-Host "  • GET /health        - No authentication required" -ForegroundColor White
Write-Host "  • GET /services      - No authentication required" -ForegroundColor White
Write-Host "  • POST /services     - Requires PROVIDER or ADMIN role" -ForegroundColor White
Write-Host "  • PUT /services/{id} - Requires PROVIDER or ADMIN role" -ForegroundColor White
Write-Host "  • DELETE /services/{id} - Requires PROVIDER or ADMIN role" -ForegroundColor White
Write-Host ""
Write-Host "Press any key to continue..."
Read-Host