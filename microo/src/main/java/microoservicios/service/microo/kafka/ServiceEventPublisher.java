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

    public ServiceEventPublisher(StreamBridge streamBridge) {
        this.streamBridge = streamBridge;
    }

    public void publishServiceCreated(ServiceEntity service, String userId) {
        publishEvent(createEventDto(service, "CREATED", userId),
                     "Service Created", "serviceCreated-out-0");
    }

    public void publishServiceUpdated(ServiceEntity service, String userId) {
        publishEvent(createEventDto(service, "UPDATED", userId),
                     "Service Updated", "serviceUpdated-out-0");
    }

    public void publishServiceDeleted(ServiceEntity service, String userId) {
        publishEvent(createEventDto(service, "DELETED", userId),
                     "Service Deleted", "serviceDeleted-out-0");
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

    private void publishEvent(ServiceEventDto event, String eventDescription, String bindingName) {
        try {
            Message<ServiceEventDto> message = MessageBuilder
                .withPayload(event)
                .setHeader("eventType", event.getEventType())
                .setHeader("serviceId", event.getServiceId().toString())
                .setHeader("timestamp", System.currentTimeMillis())
                .build();

            boolean sent = streamBridge.send(bindingName, message);

            if (sent) {
                logger.info("{} published to binding '{}' for service ID '{}' (eventType={})",
                        eventDescription, bindingName, event.getServiceId(), event.getEventType());
            } else {
                logger.warn("Failed to publish {} to binding '{}' for service ID '{}'",
                        eventDescription, bindingName, event.getServiceId());
            }
        } catch (Exception e) {
            logger.error("Error publishing {} event to binding '{}': {}", eventDescription, bindingName, e.getMessage(), e);
        }
    }
}
