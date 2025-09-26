package microoservicios.service.microo.services.impl;

import microoservicios.service.microo.entity.ServiceEntity;
import microoservicios.service.microo.repository.ServiceRepository;
import microoservicios.service.microo.services.MarketPlaceService;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class MarketPlaceServiceImpl implements MarketPlaceService {

    private final ServiceRepository repository;

    public MarketPlaceServiceImpl(ServiceRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<ServiceEntity> getAll() {
        return repository.findAll();
    }

    @Override
    public Optional<ServiceEntity> getById(UUID id) {
        return repository.findById(id);
    }

    @Override
    public ServiceEntity create(ServiceEntity service) {
        return repository.save(service);
    }

    @Override
    public Optional<ServiceEntity> update(UUID id, ServiceEntity updated) {
        return repository.findById(id)
                .map(existing -> {
                    existing.setTitle(updated.getTitle());
                    existing.setDescription(updated.getDescription());
                    existing.setPrice(updated.getPrice());
                    existing.setCategory(updated.getCategory());
                    existing.setStatus(updated.getStatus());
                    existing.setCountry(updated.getCountry());
                    return repository.save(existing);
                });
    }

    @Override
    public boolean delete(UUID id) {
        return repository.findById(id)
                .map(service -> {
                    repository.delete(service);
                    return true;
                })
                .orElse(false);
    }
}
