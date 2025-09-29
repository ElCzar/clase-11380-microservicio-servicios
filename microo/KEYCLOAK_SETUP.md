# Keycloak Integration Setup Guide

## 🔐 Security Configuration Overview

Your microservice now includes proper Keycloak OAuth2 JWT authentication with role-based access control.

## 📋 Configuration Details

### application.yml Settings
```yaml
spring:
  security:
    oauth2:
      resourceserver:
        jwt:
          issuer-uri: http://localhost:8761/realms/marketplace
          jwk-set-uri: http://localhost:8761/realms/marketplace/protocol/openid-connect/certs
```

**⚠️ IMPORTANT**: Update these URLs to match your actual Keycloak server:
- Replace `localhost:8761` with your Keycloak server address
- Replace `marketplace` with your actual realm name

### 🔒 Endpoint Security

| Endpoint | Access Level | Required Roles |
|----------|-------------|----------------|
| `GET /api/v1/services` | Public | None (anyone can view) |
| `GET /api/v1/services/{id}` | Public | None (anyone can view) |
| `GET /api/v1/services/health` | Public | None |
| `POST /api/v1/services` | Protected | `PROVIDER` or `ADMIN` |
| `PUT /api/v1/services/{id}` | Protected | `PROVIDER` or `ADMIN` |
| `DELETE /api/v1/services/{id}` | Protected | `PROVIDER` or `ADMIN` |

### 👥 Expected Keycloak Roles

Configure these roles in your Keycloak realm:
- `USER` - Regular users who can browse services
- `PROVIDER` - Service providers who can create/edit/delete services
- `ADMIN` - Administrators with full access

## 🚀 Testing Guide

### 1. Test Public Endpoints (No Authentication)
```powershell
# Should work without JWT token
Invoke-RestMethod -Uri "http://localhost:8080/api/v1/services" -Method GET
Invoke-RestMethod -Uri "http://localhost:8080/api/v1/services/health" -Method GET
```

### 2. Test Protected Endpoints (Requires JWT)
```powershell
# Should return 401 Unauthorized without JWT token
Invoke-RestMethod -Uri "http://localhost:8080/api/v1/services" -Method POST -Body '{"title":"Test"}' -ContentType "application/json"
```

### 3. Test with JWT Token
```powershell
# Get JWT token from Keycloak first, then:
$headers = @{ 'Authorization' = 'Bearer YOUR_JWT_TOKEN' }
Invoke-RestMethod -Uri "http://localhost:8080/api/v1/services" -Method POST -Headers $headers -Body '{"title":"Test Service","description":"Test","price":100}' -ContentType "application/json"
```

## 🛠️ Setup Checklist

- [x] Added OAuth2 Resource Server dependency to pom.xml
- [x] Updated SecurityConfig with JWT authentication
- [x] Configured application.yml with Keycloak settings
- [x] Added role-based access control to endpoints
- [ ] **TO DO**: Update Keycloak URLs in application.yml to match your setup
- [ ] **TO DO**: Configure Keycloak realm with appropriate roles
- [ ] **TO DO**: Create Keycloak client for this microservice
- [ ] **TO DO**: Test with actual JWT tokens from Keycloak

## 🔧 Next Steps

1. **Start your Keycloak server** on the configured port
2. **Create a realm** named "marketplace" (or update application.yml)
3. **Create a client** for this microservice
4. **Configure roles**: USER, PROVIDER, ADMIN
5. **Create test users** with different roles
6. **Restart your Spring Boot application**
7. **Test the endpoints** with real JWT tokens

## 📝 Notes

- H2 console remains accessible for development
- Health endpoints are public for monitoring
- JWT tokens are validated against Keycloak's public keys
- Roles are extracted from the JWT token's "realm_access.roles" claim