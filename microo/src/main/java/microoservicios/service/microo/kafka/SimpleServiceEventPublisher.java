package microoservicios.service.microo.kafka;

import microoservicios.service.microo.dto.ServiceResponseDto;
import microoservicios.service.microo.dto.ServiceEventDto;
import microoservicios.service.microo.entity.ServiceEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SimpleServiceEventPublisher {

    private static final Logger logger = LoggerFactory.getLogger(SimpleServiceEventPublisher.class);

    private final StreamBridge streamBridge;

    public SimpleServiceEventPublisher(StreamBridge streamBridge) {
        this.streamBridge = streamBridge;
    }

    // 🔹 Publica UN servicio creado
    public void publishService(ServiceEntity service) {
        ServiceEventDto event = new ServiceEventDto(
                service.getId(),
                service.getTitle(),
                service.getDescription(),
                service.getPrice() != null ? service.getPrice().doubleValue() : null,
                service.getAverageRating(),
                "CREATED",
                "system"
        );

        logger.info("📤 Enviando servicio a Kafka: {}", event);

        streamBridge.send("serviceResponse-out-0",MessageBuilder.withPayload(event).build());
    }

    // 🔹 Publica la LISTA completa de servicios actuales
    public void publishServices(List<ServiceResponseDto> services) {
        logger.info("📤 Enviando LISTA completa de servicios ({} items) a Kafka", services.size());

        streamBridge.send("serviceResponse-out-0",
                MessageBuilder.withPayload(services).build());
    }
}
