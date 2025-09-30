package microoservicios.service.microo.dto.kafka;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.UUID;

/**
 * DTO for receiving service requests FROM orden-pago microservice via Kafka
 * This matches the ServiceRequestDTO used by orden-pago
 */
public class ServiceRequestDto {
    
    @JsonProperty("serviceId")
    private UUID serviceId;
    
    @JsonProperty("requestId") 
    private String requestId; // UUID for correlating request/response
    
    @JsonProperty("requesterService")
    private String requesterService; // name of requesting service
    
    // Constructors
    public ServiceRequestDto() {}
    
    public ServiceRequestDto(UUID serviceId, String requestId, String requesterService) {
        this.serviceId = serviceId;
        this.requestId = requestId;
        this.requesterService = requesterService;
    }
    
    // Getters and Setters
    public UUID getServiceId() {
        return serviceId;
    }
    
    public void setServiceId(UUID serviceId) {
        this.serviceId = serviceId;
    }
    
    public String getRequestId() {
        return requestId;
    }
    
    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }
    
    public String getRequesterService() {
        return requesterService;
    }
    
    public void setRequesterService(String requesterService) {
        this.requesterService = requesterService;
    }
    
    @Override
    public String toString() {
        return "ServiceRequestDto{" +
                "serviceId=" + serviceId +
                ", requestId='" + requestId + '\'' +
                ", requesterService='" + requesterService + '\'' +
                '}';
    }
}