package microoservicios.service.microo.repository;

import microoservicios.service.microo.entity.ServiceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.security.Provider.Service;
import java.util.List;
import java.util.UUID;

public interface ServiceRepository extends JpaRepository<ServiceEntity, UUID> {

    // Buscar por título
    List<ServiceEntity> findByTitleContainingIgnoreCase(String title);

    // Buscar por rango de precios
    List<ServiceEntity> findByPriceBetween(Double minPrice, Double maxPrice);

    //Consulta combinada (título opcional + rango de precio opcional)
    @Query("SELECT s FROM ServiceEntity s " +
           "WHERE (:title IS NULL OR LOWER(s.title) LIKE LOWER(CONCAT('%', :title, '%'))) " +
           "AND (:minPrice IS NULL OR s.price >= :minPrice) " +
           "AND (:maxPrice IS NULL OR s.price <= :maxPrice)")
    List<ServiceEntity> searchServices(
            @Param("title") String title,
            @Param("minPrice") Double minPrice,
            @Param("maxPrice") Double maxPrice
    );
}
