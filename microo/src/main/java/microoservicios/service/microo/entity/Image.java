package microoservicios.service.microo.entity;


import jakarta.persistence.*;
import java.util.*;

@Entity
public class Image {
    @Id
    @GeneratedValue
    private UUID id;

    private String url;

    private int displayOrder;

    @ManyToOne
    @JoinColumn(name = "servicio_id")
    private ServiceEntity service;

    // Constructors
    public Image() {}

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(int displayOrder) {
        this.displayOrder = displayOrder;
    }

    public ServiceEntity getService() {
        return service;
    }

    public void setService(ServiceEntity service) {
        this.service = service;
    }
}
