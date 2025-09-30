package microoservicios.service.microo.kafka;

import microoservicios.service.microo.dto.ServiceEventDto;
import microoservicios.service.microo.entity.ServiceEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class ServiceKafkaProducer {
    
    private static final Logger logger = LoggerFactory.getLogger(ServiceKafkaProducer.class);
    
    private final KafkaTemplate<String, Object> kafkaTemplate;
    
    @Value("${kafka.topics.service-created:marketplace.service.created}")
    private String serviceCreatedTopic;
    
    @Value("${kafka.topics.service-updated:marketplace.service.updated}")
    private String serviceUpdatedTopic;
    
    @Value("${kafka.topics.service-deleted:marketplace.service.deleted}")
    private String serviceDeletedTopic;
    
    public ServiceKafkaProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }
    
    public void publishServiceCreated(ServiceEntity service, String userId) {
        ServiceEventDto event = createEventDto(service, "CREATED", userId);
        publishEvent(serviceCreatedTopic, event, "Service Created");
    }
    
    public void publishServiceUpdated(ServiceEntity service, String userId) {
        ServiceEventDto event = createEventDto(service, "UPDATED", userId);
        publishEvent(serviceUpdatedTopic, event, "Service Updated");
    }
    
    public void publishServiceDeleted(ServiceEntity service, String userId) {
        ServiceEventDto event = createEventDto(service, "DELETED", userId);
        publishEvent(serviceDeletedTopic, event, "Service Deleted");
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
    
    private void publishEvent(String topic, ServiceEventDto event, String eventDescription) {
        try {
            String key = event.getServiceId().toString();
            
            CompletableFuture<SendResult<String, Object>> future = 
                kafkaTemplate.send(topic, key, event);
            
            future.whenComplete((result, exception) -> {
                if (exception == null) {
                    logger.info("{} event published successfully to topic '{}' with key '{}' at offset '{}'", 
                        eventDescription, topic, key, result.getRecordMetadata().offset());
                } else {
                    logger.error("Failed to publish {} event to topic '{}' with key '{}'", 
                        eventDescription, topic, key, exception);
                }
            });
            
        } catch (Exception e) {
            logger.error("Error publishing {} event to Kafka: {}", eventDescription, e.getMessage(), e);
        }
    }
}