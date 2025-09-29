package microoservicios.service.microo.graphql;

import microoservicios.service.microo.entity.ServiceEntity;
import microoservicios.service.microo.repository.ServiceRepository;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class ServiceGraphQlController {

    private final ServiceRepository repository;

    public ServiceGraphQlController(ServiceRepository repository) {
        this.repository = repository;
    }

    @QueryMapping
    public List<ServiceEntity> services(@Argument String title,
                                        @Argument Double minPrice,
                                        @Argument Double maxPrice) {
        return repository.searchServices(title, minPrice, maxPrice);
    }


    @QueryMapping
    public ServiceEntity serviceById(@Argument String id) {
        return repository.findById(java.util.UUID.fromString(id)).orElse(null);
    }
}
