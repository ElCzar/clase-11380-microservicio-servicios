package microoservicios.service.microo.services;

import microoservicios.service.microo.dto.kafka.ServiceRequestDto;
import microoservicios.service.microo.dto.kafka.ServiceResponseDto;
import microoservicios.service.microo.entity.ServiceEntity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.function.Consumer;

/**
 * Service for handling Kafka request-response communication with orden-pago
 * microservice
 */
@Service
public class ServiceRequestHandler {

    private static final Logger logger = LoggerFactory.getLogger(ServiceRequestHandler.class);

    private final MarketPlaceService marketPlaceService;
    private final StreamBridge streamBridge;

    public ServiceRequestHandler(MarketPlaceService marketPlaceService, StreamBridge streamBridge) {
        this.marketPlaceService = marketPlaceService;
        this.streamBridge = streamBridge;
    }

    /**
     * Handles service requests from orden-pago microservice
     */
    public void handleServiceRequest(ServiceRequestDto request) {
        logger.info("Received service request: serviceId={}, requestId={}, requester={}",
                request.getServiceId(), request.getRequestId(), request.getRequesterService());

        try {
            // Find the service in the database
            Optional<ServiceEntity> serviceOptional = marketPlaceService.getById(request.getServiceId());

            ServiceResponseDto response;
            if (serviceOptional.isPresent()) {
                // Service found - create response DTO
                ServiceEntity service = serviceOptional.get();
                response = ServiceResponseDto.fromServiceEntity(service, request.getRequestId());
                logger.info("Found service: {} - {}", service.getId(), service.getTitle());
            } else {
                // Service not found - create error response
                response = ServiceResponseDto.error(request.getRequestId(), "Service not found");
                logger.warn("Service not found: {}", request.getServiceId());
            }

            // Send response back via Kafka
            boolean sent = streamBridge.send("serviceResponse-out-0", response);
            if (sent) {
                logger.info("Service response sent successfully for requestId: {}", request.getRequestId());
            } else {
                logger.error("Failed to send service response for requestId: {}", request.getRequestId());
            }

        } catch (Exception e) {
            logger.error("Error processing service request: {}", request, e);

            // Send error response
            ServiceResponseDto errorResponse = ServiceResponseDto.error(request.getRequestId(),
                    "Internal error: " + e.getMessage());
            streamBridge.send("serviceResponse-out-0", errorResponse);
        }
    }
}

/**
 * Configuration for Kafka consumers using Spring Cloud Stream functional
 * programming model
 */
@Configuration
class ServiceKafkaConsumerConfig {

    private static final Logger logger = LoggerFactory.getLogger(ServiceKafkaConsumerConfig.class);

    private final ServiceRequestHandler serviceRequestHandler;

    public ServiceKafkaConsumerConfig(ServiceRequestHandler serviceRequestHandler) {
        this.serviceRequestHandler = serviceRequestHandler;
    }

    /**
     * Consumer for service requests from orden-pago microservice
     * This matches the expected binding name in application.yml:
     * serviceRequest-in-0
     */
    @Bean
    public Consumer<ServiceRequestDto> serviceRequest() {
        logger.info("🔧 Registering serviceRequest consumer bean");
        return request -> {
            try {
                logger.info("Processing incoming service request: {}", request);
                serviceRequestHandler.handleServiceRequest(request);
            } catch (Exception e) {
                logger.error("Error processing service request: {}", request, e);
            }
        };
    }
}