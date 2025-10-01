@echo off
echo Getting JWT token for microo-service API testing (using perfil-service client)...

curl -X POST "http://localhost:8180/realms/microservices-ecosystem/protocol/openid-connect/token" ^
     -H "Content-Type: application/x-www-form-urlencoded" ^
     -d "grant_type=password" ^
     -d "username=testuser" ^
     -d "password=testpassword" ^
     -d "client_id=perfil-service" ^
     -d "client_secret=XhzE490FVJwoJ3OdLlyyqpDyIaf1lgVe"

echo.
echo.
echo Copy the access_token value from above and use it in your API calls.
echo Example usage:
echo curl -H "Authorization: Bearer YOUR_TOKEN_HERE" http://localhost:8080/api/v1/services/health
pause