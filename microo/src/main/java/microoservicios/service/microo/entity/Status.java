package microoservicios.service.microo.entity;

import jakarta.persistence.*;
import java.util.*;

@Entity
public class Status {
   @Id
    @GeneratedValue
    private UUID id;

    private String name;

    @OneToMany(mappedBy = "status")
    private List<ServiceEntity> services = new ArrayList<>();

    // Constructors
    public Status() {}

    public Status(String name) {
        this.name = name;
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ServiceEntity> getServices() {
        return services;
    }

    public void setServices(List<ServiceEntity> services) {
        this.services = services;
    }
}
