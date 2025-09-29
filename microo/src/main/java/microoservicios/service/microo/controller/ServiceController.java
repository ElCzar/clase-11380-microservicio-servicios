package microoservicios.service.microo.controller;

import microoservicios.service.microo.entity.ServiceEntity;
import microoservicios.service.microo.services.MarketPlaceService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/v1/services")
public class ServiceController {

    private final MarketPlaceService marketPlaceService;

    public ServiceController(MarketPlaceService marketPlaceService) {
        this.marketPlaceService = marketPlaceService;
    }

    // Health check endpoint (public access)
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Marketplace Service is running");
    }

    // Get all services (public access)
    @GetMapping
    public List<ServiceEntity> getAllServices() {
        return marketPlaceService.getAll();
    }

    // Get service by ID (public access)
    @GetMapping("/{id}")
    public ResponseEntity<ServiceEntity> getServiceById(@PathVariable UUID id) {
        return marketPlaceService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Create a new service (providers and admins only)
    @PostMapping
    @PreAuthorize("hasRole('PROVIDER') or hasRole('ADMIN')")
    public ResponseEntity<ServiceEntity> createService(@RequestBody ServiceEntity service, Authentication auth) {
        return ResponseEntity.ok(marketPlaceService.create(service));
    }

    // Update an existing service (providers and admins only)
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('PROVIDER') or hasRole('ADMIN')")
    public ResponseEntity<ServiceEntity> updateService(@PathVariable UUID id, @RequestBody ServiceEntity service, Authentication auth) {
        return marketPlaceService.update(id, service)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Delete a service (providers and admins only)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('PROVIDER') or hasRole('ADMIN')")
    public ResponseEntity<Void> deleteService(@PathVariable UUID id, Authentication auth) {
        boolean deleted = marketPlaceService.delete(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
