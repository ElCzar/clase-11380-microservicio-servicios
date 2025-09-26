package microoservicios.service.microo.services;

import microoservicios.service.microo.entity.ServiceEntity;

import java.util.*;

public interface MarketPlaceService {
    List<ServiceEntity> getAll();
    Optional<ServiceEntity> getById(UUID id);
    ServiceEntity create(ServiceEntity service);
    Optional<ServiceEntity> update(UUID id, ServiceEntity service);
    boolean delete(UUID id);
}
