package microoservicios.service.microo.dto.external;

public class WeatherRequest {
    private Double latitude;
    private Double longitude;
    private Integer days;
    private String timezone;

    public WeatherRequest() {}

    public WeatherRequest(Double latitude, Double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.days = 7; // Default 7 days forecast
    }

    public WeatherRequest(Double latitude, Double longitude, Integer days) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.days = days;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Integer getDays() {
        return days;
    }

    public void setDays(Integer days) {
        this.days = days;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }
}

