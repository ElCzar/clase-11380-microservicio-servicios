package microoservicios.service.microo.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Accomodation {
    private LocalDateTime checkIn;
    private LocalDateTime checkOut;
    private String location;
    private int maxGuests;

}
