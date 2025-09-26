package microoservicios.service.microo.controller;

import microoservicios.service.microo.entity.ServiceEntity;
import microoservicios.service.microo.services.MarketPlaceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/v1/services")
public class ServiceController {

    private final MarketPlaceService marketPlaceService;

    public ServiceController(MarketPlaceService marketPlaceService) {
        this.marketPlaceService = marketPlaceService;
    }

    // Get all services
    @GetMapping
    public List<ServiceEntity> getAllServices() {
        return marketPlaceService.getAll();
    }

    // Get service by ID
    @GetMapping("/{id}")
    public ResponseEntity<ServiceEntity> getServiceById(@PathVariable UUID id) {
        return marketPlaceService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Create a new service
    @PostMapping
    public ResponseEntity<ServiceEntity> createService(@RequestBody ServiceEntity service) {
        return ResponseEntity.ok(marketPlaceService.create(service));
    }

    // Update an existing service
    @PutMapping("/{id}")
    public ResponseEntity<ServiceEntity> updateService(@PathVariable UUID id, @RequestBody ServiceEntity service) {
        return marketPlaceService.update(id, service)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Delete a service
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteService(@PathVariable UUID id) {
        boolean deleted = marketPlaceService.delete(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
