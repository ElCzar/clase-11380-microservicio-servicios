package microoservicios.service.microo.controller;

import microoservicios.service.microo.dto.external.OpenMeteoResponse;
import microoservicios.service.microo.services.external.OpenMeteoService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/weather")
public class WeatherController {

    private final OpenMeteoService openMeteoService;

    public WeatherController(OpenMeteoService openMeteoService) {
        this.openMeteoService = openMeteoService;
    }

    /**
     * Get current weather for given coordinates
     * GET /api/v1/weather/current?lat={latitude}&lon={longitude}
     */
    @GetMapping("/current")
    @PreAuthorize("authenticated")
    public ResponseEntity<OpenMeteoResponse> getCurrentWeather(
            @RequestParam Double lat,
            @RequestParam Double lon) {
        if (lat == null || lon == null) {
            return ResponseEntity.badRequest().build();
        }
        try {
            OpenMeteoResponse response = openMeteoService.getCurrentWeather(lat, lon);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get weather forecast for given coordinates
     * GET /api/v1/weather/forecast?lat={latitude}&lon={longitude}&days={days}
     */
    @GetMapping("/forecast")
    @PreAuthorize("authenticated")
    public ResponseEntity<OpenMeteoResponse> getForecast(
            @RequestParam Double lat,
            @RequestParam Double lon,
            @RequestParam(required = false, defaultValue = "7") Integer days) {
        if (lat == null || lon == null) {
            return ResponseEntity.badRequest().build();
        }
        try {
            OpenMeteoResponse response = openMeteoService.getForecast(lat, lon, days);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get hourly weather forecast for given coordinates
     * GET /api/v1/weather/hourly?lat={latitude}&lon={longitude}&hours={hours}
     */
    @GetMapping("/hourly")
    @PreAuthorize("authenticated")
    public ResponseEntity<OpenMeteoResponse> getHourlyForecast(
            @RequestParam Double lat,
            @RequestParam Double lon,
            @RequestParam(required = false, defaultValue = "48") Integer hours) {
        if (lat == null || lon == null) {
            return ResponseEntity.badRequest().build();
        }
        try {
            OpenMeteoResponse response = openMeteoService.getHourlyForecast(lat, lon, hours);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}

