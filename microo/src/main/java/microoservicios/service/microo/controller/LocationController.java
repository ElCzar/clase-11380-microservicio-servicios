package microoservicios.service.microo.controller;

import microoservicios.service.microo.dto.external.GeoApifyResponse;
import microoservicios.service.microo.services.external.GeoApifyService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/location")
public class LocationController {

    private final GeoApifyService geoApifyService;

    public LocationController(GeoApifyService geoApifyService) {
        this.geoApifyService = geoApifyService;
    }

    /**
     * Geocode an address to get coordinates
     * GET /api/v1/location/geocode?address={address}
     */
    @GetMapping("/geocode")
    @PreAuthorize("authenticated")
    public ResponseEntity<GeoApifyResponse> geocode(@RequestParam String address) {
        if (address == null || address.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        try {
            GeoApifyResponse response = geoApifyService.geocode(address);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Reverse geocode coordinates to get address
     * GET /api/v1/location/reverse-geocode?lat={latitude}&lon={longitude}
     */
    @GetMapping("/reverse-geocode")
    @PreAuthorize("authenticated")
    public ResponseEntity<GeoApifyResponse> reverseGeocode(
            @RequestParam Double lat,
            @RequestParam Double lon) {
        if (lat == null || lon == null) {
            return ResponseEntity.badRequest().build();
        }
        try {
            GeoApifyResponse response = geoApifyService.reverseGeocode(lat, lon);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Search for places using text query
     * GET /api/v1/location/places?query={searchQuery}
     */
    @GetMapping("/places")
    @PreAuthorize("authenticated")
    public ResponseEntity<GeoApifyResponse> searchPlaces(@RequestParam String query) {
        if (query == null || query.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        try {
            GeoApifyResponse response = geoApifyService.searchPlaces(query);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}

