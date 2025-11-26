package microoservicios.service.microo.entity;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.math.BigDecimal;
import java.util.UUID;

class ServiceEntityTest {

    @Test
    void testCreateServiceEntity() {
        // Given - Create a new service
        ServiceEntity service = new ServiceEntity();
        
        // When - Set some basic properties
        service.setTitle("Test Service");
        service.setDescription("Test Description");
        service.setPrice(new BigDecimal("100.00"));
        service.setAverageRating(4.5);
        
        // Then - Check that properties are set correctly
        assertEquals("Test Service", service.getTitle());
        assertEquals("Test Description", service.getDescription());
        assertEquals(new BigDecimal("100.00"), service.getPrice());
        assertEquals(4.5, service.getAverageRating());
    }
    
    @Test
    void testServiceEntityConstructor() {
        // Given & When - Create a service using constructor
        ServiceEntity service = new ServiceEntity();
        
        // Then - Check initial values
        assertNull(service.getId());
        assertNull(service.getTitle());
        assertNull(service.getDescription());
        assertNull(service.getPrice());
        assertNull(service.getAverageRating());
    }
    
    @Test
    void testSetAndGetId() {
        // Given
        ServiceEntity service = new ServiceEntity();
        UUID testId = UUID.randomUUID();
        
        // When
        service.setId(testId);
        
        // Then
        assertEquals(testId, service.getId());
    }
}
