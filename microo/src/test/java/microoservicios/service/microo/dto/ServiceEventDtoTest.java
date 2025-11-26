package microoservicios.service.microo.dto;

import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDateTime;
import java.util.UUID;

@ActiveProfiles("test")
@TestPropertySource(properties = {
    "KAFKA_BOOTSTRAP_SERVERS=embedded",
    "KAFKA_BROKERS=embedded"
})
class ServiceEventDtoTest {

    @Test
    void testCreateServiceEventDto() {
        // Given
        UUID serviceId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();
        
        // When
        ServiceEventDto dto = new ServiceEventDto();
        dto.setServiceId(serviceId);
        dto.setTitle("Test Title");
        dto.setDescription("Test Description");
        dto.setPrice(200.00);  // Use Double instead of BigDecimal
        dto.setAverageRating(4.8);
        dto.setEventType("CREATED");
        dto.setTimestamp(now);
        dto.setUserId("testuser");
        
        // Then
        assertEquals(serviceId, dto.getServiceId());
        assertEquals("Test Title", dto.getTitle());
        assertEquals("Test Description", dto.getDescription());
        assertEquals(200.00, dto.getPrice());
        assertEquals(4.8, dto.getAverageRating());
        assertEquals("CREATED", dto.getEventType());
        assertEquals(now, dto.getTimestamp());
        assertEquals("testuser", dto.getUserId());
    }
    
    @Test
    void testDefaultConstructor() {
        // When
        ServiceEventDto dto = new ServiceEventDto();
        
        // Then
        assertNull(dto.getServiceId());
        assertNull(dto.getTitle());
        assertNull(dto.getDescription());
        assertNull(dto.getPrice());
        assertNull(dto.getAverageRating());
        assertNull(dto.getEventType());
        assertNull(dto.getTimestamp());
        assertNull(dto.getUserId());
    }
}
