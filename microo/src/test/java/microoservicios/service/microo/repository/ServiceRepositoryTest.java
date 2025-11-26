package microoservicios.service.microo.repository;

import microoservicios.service.microo.entity.ServiceEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import static org.junit.jupiter.api.Assertions.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@DataJpaTest  // This annotation sets up a test database
class ServiceRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;  // Helper for database operations in tests

    @Autowired
    private ServiceRepository serviceRepository;

    @Test
    void testSaveAndFindService() {
        // Given - Create a service
        ServiceEntity service = new ServiceEntity();
        service.setTitle("Test Service");
        service.setDescription("Test Description");
        service.setPrice(new BigDecimal("150.00"));
        service.setAverageRating(4.2);
        
        // When - Save the service
        ServiceEntity savedService = serviceRepository.save(service);
        
        // Then - Check it was saved correctly
        assertNotNull(savedService.getId());
        assertEquals("Test Service", savedService.getTitle());
        assertEquals("Test Description", savedService.getDescription());
        assertEquals(new BigDecimal("150.00"), savedService.getPrice());
        assertEquals(4.2, savedService.getAverageRating());
    }
    
    @Test
    void testFindById() {
        // Given - Create and save a service
        ServiceEntity service = new ServiceEntity();
        service.setTitle("Find Me");
        service.setDescription("I can be found");
        service.setPrice(new BigDecimal("100.00"));
        service.setAverageRating(5.0);
        
        ServiceEntity savedService = entityManager.persistAndFlush(service);
        
        // When - Find by ID
        Optional<ServiceEntity> found = serviceRepository.findById(savedService.getId());
        
        // Then - Check it was found
        assertTrue(found.isPresent());
        assertEquals("Find Me", found.get().getTitle());
        assertEquals("I can be found", found.get().getDescription());
    }
    
    @Test
    void testFindAll() {
        // Given - Create multiple services
        ServiceEntity service1 = createTestService("Service 1", "Description 1", "100.00");
        ServiceEntity service2 = createTestService("Service 2", "Description 2", "200.00");
        
        entityManager.persistAndFlush(service1);
        entityManager.persistAndFlush(service2);
        
        // When - Find all services
        List<ServiceEntity> services = serviceRepository.findAll();
        
        // Then - Check both were found
        assertEquals(2, services.size());
    }
    
    @Test
    void testDeleteService() {
        // Given - Create and save a service
        ServiceEntity service = createTestService("Delete Me", "I will be deleted", "50.00");
        ServiceEntity savedService = entityManager.persistAndFlush(service);
        
        // When - Delete the service
        serviceRepository.deleteById(savedService.getId());
        
        // Then - Check it's gone
        Optional<ServiceEntity> deleted = serviceRepository.findById(savedService.getId());
        assertFalse(deleted.isPresent());
    }
    
    // Helper method to create test services
    private ServiceEntity createTestService(String title, String description, String price) {
        ServiceEntity service = new ServiceEntity();
        service.setTitle(title);
        service.setDescription(description);
        service.setPrice(new BigDecimal(price));
        service.setAverageRating(4.0);
        return service;
    }
}