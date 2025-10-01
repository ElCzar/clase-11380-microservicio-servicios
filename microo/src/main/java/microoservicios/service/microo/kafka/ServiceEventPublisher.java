package microoservicios.service.microo.kafka;

import microoservicios.service.microo.dto.ServiceEventDto;
import microoservicios.service.microo.entity.ServiceEntity;
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
    
    @Value("${spring.cloud.stream.bindings.serviceEvents-out-0.destination:marketplace.service.events}")
    private String bindingDestination;
    
    // Binding name for the unified events channel
    private static final String BINDING_NAME = "serviceEvents-out-0";
    
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
    
    private void publishEvent(ServiceEventDto event, String eventDescription) {
        try {
            // Create message with headers for better tracking
            Message<ServiceEventDto> message = MessageBuilder
                .withPayload(event)
                .setHeader("eventType", event.getEventType())
                .setHeader("serviceId", event.getServiceId().toString())
                .setHeader("timestamp", System.currentTimeMillis())
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