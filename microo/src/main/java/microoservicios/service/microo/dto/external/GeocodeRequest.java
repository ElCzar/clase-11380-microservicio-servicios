package microoservicios.service.microo.dto.external;

public class GeocodeRequest {
    private String address;
    private String text;

    public GeocodeRequest() {}

    public GeocodeRequest(String address) {
        this.address = address;
        this.text = address;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
        this.text = address;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
        this.address = text;
    }
}

