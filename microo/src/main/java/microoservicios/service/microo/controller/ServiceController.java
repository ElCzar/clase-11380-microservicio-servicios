package microoservicios.service.microo.controller;

import microoservicios.service.microo.dto.ServiceRequestDto;
import microoservicios.service.microo.dto.ServiceResponseDto;
import microoservicios.service.microo.dto.ServiceEventDto;
import microoservicios.service.microo.entity.*;
import microoservicios.service.microo.kafka.ServiceEventPublisher;
import microoservicios.service.microo.services.MarketPlaceService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/services")
public class ServiceController {

    private final MarketPlaceService marketPlaceService;
    private final ServiceEventPublisher eventPublisher;

    public ServiceController(MarketPlaceService marketPlaceService, ServiceEventPublisher eventPublisher) {
        this.marketPlaceService = marketPlaceService;
        this.eventPublisher = eventPublisher;
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Marketplace Service is running");
    }

    @GetMapping
    public List<ServiceResponseDto> getAllServices(Authentication auth) {

        List<ServiceResponseDto> services = marketPlaceService.getAll().stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());
        for (ServiceResponseDto service : services) {
            //publishEvent(service, "CREATED", auth);
            System.out.println("Service: " + service.getId() + " - " + service.getTitle());
        }

        return services;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServiceResponseDto> getServiceById(@PathVariable UUID id) {
        return marketPlaceService.getById(id)
                .map(service -> ResponseEntity.ok(toResponseDto(service)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ServiceResponseDto> createService(@RequestBody ServiceRequestDto dto, Authentication auth) {
        ServiceEntity entity = fromRequestDto(dto);
        ServiceEntity createdService = marketPlaceService.create(entity);

        publishEvent(createdService, "CREATED", auth);

        return ResponseEntity.ok(toResponseDto(createdService));
    }

    @PostMapping("/publish-all")
    public ResponseEntity<String> publishAll(Authentication auth) {
        List<ServiceEntity> services = marketPlaceService.getAll();

        String userId = (auth != null) ? auth.getName() : "system";

        // Convertimos cada entidad en un DTO de evento
        List<ServiceEventDto> events = services.stream()
                .map(service -> new ServiceEventDto(
                        service.getId(),
                        service.getTitle(),
                        service.getDescription(),
                        service.getPrice() != null ? service.getPrice().doubleValue() : null,
                        service.getAverageRating(),
                        "PUBLISH_ALL",
                        userId
                ))
                .toList();

            eventPublisher.publishEvents(events);

            return ResponseEntity.ok("✅ Se publicaron " + events.size() + " servicios en Kafka.");
        }


    @PutMapping("/{id}")
    public ResponseEntity<ServiceResponseDto> updateService(@PathVariable UUID id,
                                                            @RequestBody ServiceRequestDto dto,
                                                            Authentication auth) {
        ServiceEntity entity = fromRequestDto(dto);
        Optional<ServiceEntity> updated = marketPlaceService.update(id, entity);

        if (updated.isPresent()) {
            publishEvent(updated.get(), "UPDATED", auth);
            return ResponseEntity.ok(toResponseDto(updated.get()));
        }

        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteService(@PathVariable UUID id, Authentication auth) {
        Optional<ServiceEntity> serviceToDelete = marketPlaceService.getById(id);

        boolean deleted = marketPlaceService.delete(id);

        if (deleted && serviceToDelete.isPresent()) {
            publishEvent(serviceToDelete.get(), "DELETED", auth);
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.notFound().build();
    }

    // ---------- Helpers ----------
    private void publishEvent(ServiceEntity service, String action, Authentication auth) {
        String userId = (auth != null) ? auth.getName() : "system";
        ServiceEventDto event = new ServiceEventDto(
                service.getId(),
                service.getTitle(),
                service.getDescription(),
                service.getPrice() != null ? service.getPrice().doubleValue() : null,
                service.getAverageRating(),
                action,
                userId
        );
        eventPublisher.publishEvent(event);
    }

    private ServiceResponseDto toResponseDto(ServiceEntity entity) {
        ServiceResponseDto dto = new ServiceResponseDto();
        dto.setId(entity.getId());
        dto.setTitle(entity.getTitle());
        dto.setDescription(entity.getDescription());
        dto.setPrice(entity.getPrice());
        dto.setAverageRating(entity.getAverageRating());

        if (entity.getCategory() != null) {
            dto.setCategoryId(entity.getCategory().getId());
            dto.setCategoryName(entity.getCategory().getName());
        }
        if (entity.getStatus() != null) {
            dto.setStatusId(entity.getStatus().getId());
            dto.setStatusName(entity.getStatus().getName());
        }
        if (entity.getCountry() != null) {
            dto.setCountryId(entity.getCountry().getId());
            dto.setCountryName(entity.getCountry().getName());
        }
        return dto;
    }

    private ServiceEntity fromRequestDto(ServiceRequestDto dto) {
        ServiceEntity entity = new ServiceEntity();
        entity.setTitle(dto.getTitle());
        entity.setDescription(dto.getDescription());
        entity.setPrice(dto.getPrice());
        entity.setAverageRating(dto.getAverageRating());

        if (dto.getCategoryId() != null) {
            Category category = new Category();
            category.setId(dto.getCategoryId());
            entity.setCategory(category);
        }
        if (dto.getStatusId() != null) {
            Status status = new Status();
            status.setId(dto.getStatusId());
            entity.setStatus(status);
        }
        if (dto.getCountryId() != null) {
            Country country = new Country();
            country.setId(dto.getCountryId());
            entity.setCountry(country);
        }
        return entity;
    }
}
