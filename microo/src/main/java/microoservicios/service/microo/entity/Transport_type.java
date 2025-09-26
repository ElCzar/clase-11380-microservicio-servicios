package microoservicios.service.microo.entity;

import jakarta.persistence.*;
import java.util.*;

@Entity
public class Transport_type {
    @Id
    @GeneratedValue
    private UUID id;

    private String type;

    @OneToMany(mappedBy = "type_id")
    private List<Transport> transportes = new ArrayList<>();
}
