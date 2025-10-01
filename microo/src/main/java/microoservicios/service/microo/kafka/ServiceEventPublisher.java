package microoservicios.service.microo.kafka;

import microoservicios.service.microo.dto.ServiceEventDto;
import microoservicios.service.microo.entity.ServiceEntity;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Service
public class ServiceEventPublisher {
    
    private static final Logger logger = LoggerFactory.getLogger(ServiceEventPublisher.class);
    
    private final StreamBridge streamBridge;
    
    @Value("${spring.cloud.stream.bindings.serviceResponse-out-0.destination:service-response-topic}")
    private String bindingDestination;
    
    // Binding name for the unified events channel
    private static final String BINDING_NAME = "serviceResponse-out-0";
    
    public ServiceEventPublisher(StreamBridge streamBridge) {
        this.streamBridge = streamBridge;
    }
    
    public void publishServiceCreated(ServiceEntity service, String userId) {
        ServiceEventDto event = createEventDto(service, "CREATED", userId);
        publishEvent(event, "Service Created");
    }
    
    public void publishServiceUpdated(ServiceEntity service, String userId) {
        ServiceEventDto event = createEventDto(service, "UPDATED", userId);
        publishEvent(event, "Service Updated");
    }
    
    public void publishServiceDeleted(ServiceEntity service, String userId) {
        ServiceEventDto event = createEventDto(service, "DELETED", userId);
        publishEvent(event, "Service Deleted");
    }
    
    public void publishEvent(ServiceEventDto event) {
        publishEvent(event, "Service Event");
    }
    
    private ServiceEventDto createEventDto(ServiceEntity service, String eventType, String userId) {
        return new ServiceEventDto(
            service.getId(),
            service.getTitle(),
            service.getDescription(),
            service.getPrice() != null ? service.getPrice().doubleValue() : null,
            service.getAverageRating(),
            eventType,
            userId
        );
    }

    public void publishEvents(List<ServiceEventDto> events) {
        try {
            Message<List<ServiceEventDto>> message = MessageBuilder
                    .withPayload(events)
                    .setHeader("eventType", "PUBLISH_ALL")
                    .setHeader("size", events.size())
                    .build();

            boolean sent = streamBridge.send(BINDING_NAME, message);

            if (sent) {
                logger.info("Publish-All: Se enviaron {} servicios al binding '{}' -> topic '{}'",
                        events.size(), BINDING_NAME, bindingDestination);
            } else {
                logger.warn("Publish-All: Falló el envío de {} servicios al binding '{}'",
                        events.size(), BINDING_NAME);
            }

        } catch (Exception e) {
            logger.error("Error al publicar lista de servicios: {}", e.getMessage(), e);
        }
    }

    
    private void publishEvent(ServiceEventDto event, String eventDescription) {
        try {
            // Create message with headers for better tracking
            Message<ServiceEventDto> message = MessageBuilder
                .withPayload(event)
                .setHeader("eventType", event.getEventType())
                .setHeader("serviceId", event.getServiceId().toString())
                .build();
            
            // Send using StreamBridge with binding name
            boolean sent = streamBridge.send(BINDING_NAME, message);
            
            if (sent) {
                logger.info("{} event published successfully to binding '{}' -> topic '{}' for service ID '{}' with event type '{}'", 
                    eventDescription, BINDING_NAME, bindingDestination, event.getServiceId(), event.getEventType());
            } else {
                logger.warn("Failed to publish {} event to binding '{}' for service ID '{}'", 
                    eventDescription, BINDING_NAME, event.getServiceId());
            }
            
        } catch (Exception e) {
            logger.error("Error publishing {} event to Spring Cloud Stream: {}", eventDescription, e.getMessage(), e);
        }
    }
}