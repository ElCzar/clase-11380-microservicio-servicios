package microoservicios.service.microo.entity;

import jakarta.persistence.*;

@Entity
public class Food extends ServiceEntity{
    private boolean vegan;
    private String location;
    private int capacity;

    // Constructors
    public Food() {}

    // Getters and Setters
    public boolean isVegan() {
        return vegan;
    }

    public void setVegan(boolean vegan) {
        this.vegan = vegan;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }
}
