package microoservicios.service.microo.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Transport extends ServiceEntity{
    private LocalDateTime departureDatetime;
    private LocalDateTime arrivalDatetime;
    private String departureLocation;
    private String destinationLocation;

    @ManyToOne
    @JoinColumn(name = "type_id")
    private Transport_type type;

    // Constructors
    public Transport() {}

    // Getters and Setters
    public LocalDateTime getDepartureDatetime() {
        return departureDatetime;
    }

    public void setDepartureDatetime(LocalDateTime departureDatetime) {
        this.departureDatetime = departureDatetime;
    }

    public LocalDateTime getArrivalDatetime() {
        return arrivalDatetime;
    }

    public void setArrivalDatetime(LocalDateTime arrivalDatetime) {
        this.arrivalDatetime = arrivalDatetime;
    }

    public String getDepartureLocation() {
        return departureLocation;
    }

    public void setDepartureLocation(String departureLocation) {
        this.departureLocation = departureLocation;
    }

    public String getDestinationLocation() {
        return destinationLocation;
    }

    public void setDestinationLocation(String destinationLocation) {
        this.destinationLocation = destinationLocation;
    }

    public Transport_type getType() {
        return type;
    }

    public void setType(Transport_type type) {
        this.type = type;
    }
}
