@echo off
echo Getting JWT token from microservices-ecosystem realm...

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
pause