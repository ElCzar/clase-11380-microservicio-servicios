package microoservicios.service.microo.controller;

import microoservicios.service.microo.dto.ServiceEventDto;
import microoservicios.service.microo.entity.ServiceEntity;
import microoservicios.service.microo.kafka.ServiceEventPublisher;
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
    private final ServiceEventPublisher eventPublisher;

    public ServiceController(MarketPlaceService marketPlaceService, ServiceEventPublisher eventPublisher) {
        this.marketPlaceService = marketPlaceService;
        this.eventPublisher = eventPublisher;
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
        ServiceEntity createdService = marketPlaceService.create(service);
        
        // Construir DTO de evento
        String userId = (auth != null) ? auth.getName() : "system";
        ServiceEventDto event = new ServiceEventDto(
            createdService.getId(),
            createdService.getTitle(),
            createdService.getDescription(),
            createdService.getPrice() != null ? createdService.getPrice().doubleValue() : null,
            createdService.getAverageRating(),
            "CREATED",
            userId
        );
        
        // Publicar en Kafka vía binding serviceEvents-out-0
        eventPublisher.publishEvent(event);
        
        return ResponseEntity.ok(createdService);
    }

    // Update an existing service (providers and admins only)
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('PROVIDER') or hasRole('ADMIN')")
    public ResponseEntity<ServiceEntity> updateService(@PathVariable UUID id, @RequestBody ServiceEntity service, Authentication auth) {
        Optional<ServiceEntity> updatedService = marketPlaceService.update(id, service);
        
        if (updatedService.isPresent()) {
            // Construir DTO de evento
            String userId = (auth != null) ? auth.getName() : "system";
            ServiceEventDto event = new ServiceEventDto(
                updatedService.get().getId(),
                updatedService.get().getTitle(),
                updatedService.get().getDescription(),
                updatedService.get().getPrice() != null ? updatedService.get().getPrice().doubleValue() : null,
                updatedService.get().getAverageRating(),
                "UPDATED",
                userId
            );
            
            // Publicar en Kafka vía binding serviceEvents-out-0
            eventPublisher.publishEvent(event);
            
            return ResponseEntity.ok(updatedService.get());
        }
        
        return ResponseEntity.notFound().build();
    }

    // Delete a service (providers and admins only)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('PROVIDER') or hasRole('ADMIN')")
    public ResponseEntity<Void> deleteService(@PathVariable UUID id, Authentication auth) {
        // Get the service before deletion for Kafka event
        Optional<ServiceEntity> serviceToDelete = marketPlaceService.getById(id);
        
        boolean deleted = marketPlaceService.delete(id);
        
        if (deleted && serviceToDelete.isPresent()) {
            // Construir DTO de evento
            String userId = (auth != null) ? auth.getName() : "system";
            ServiceEventDto event = new ServiceEventDto(
                serviceToDelete.get().getId(),
                serviceToDelete.get().getTitle(),
                serviceToDelete.get().getDescription(),
                serviceToDelete.get().getPrice() != null ? serviceToDelete.get().getPrice().doubleValue() : null,
                serviceToDelete.get().getAverageRating(),
                "DELETED",
                userId
            );
            
            // Publicar en Kafka vía binding serviceEvents-out-0
            eventPublisher.publishEvent(event);
            
            return ResponseEntity.noContent().build();
        }
        
        return ResponseEntity.notFound().build();
    }
}
