package microoservicios.service.microo.entity;

import jakarta.persistence.*;
import java.util.*;

@Entity
public class Transport_type {
    @Id
    @GeneratedValue
    private UUID id;

    private String type;

    @OneToMany(mappedBy = "type")
    private List<Transport> transportes = new ArrayList<>();

    // Constructors
    public Transport_type() {}

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<Transport> getTransportes() {
        return transportes;
    }

    public void setTransportes(List<Transport> transportes) {
        this.transportes = transportes;
    }
}
