package microoservicios.service.microo.services.external;

import microoservicios.service.microo.config.ExternalApiConfig;
import microoservicios.service.microo.dto.external.GeoApifyResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
public class GeoApifyService {

    private static final Logger logger = LoggerFactory.getLogger(GeoApifyService.class);
    private final RestClient restClient;
    private final ExternalApiConfig config;

    public GeoApifyService(RestClient restClient, ExternalApiConfig config) {
        this.restClient = restClient;
        this.config = config;
    }

    /**
     * Geocode an address to get coordinates
     * @param address The address to geocode
     * @return GeoApifyResponse with location data
     */
    public GeoApifyResponse geocode(String address) {
        try {
            logger.info("Geocoding address: {}", address);
            
            String url = String.format("%s/geocode/search?text=%s&apiKey=%s",
                    config.getGeoApifyBaseUrl(),
                    URLEncoder.encode(address, StandardCharsets.UTF_8),
                    config.getGeoApifyApiKey());

            GeoApifyResponse response = restClient.get()
                    .uri(url)
                    .retrieve()
                    .body(GeoApifyResponse.class);

            logger.info("Geocoding successful for address: {}", address);
            return response;
        } catch (RestClientException e) {
            logger.error("Error geocoding address: {}", address, e);
            throw new RuntimeException("Failed to geocode address: " + e.getMessage(), e);
        }
    }

    /**
     * Reverse geocode coordinates to get address
     * @param latitude The latitude
     * @param longitude The longitude
     * @return GeoApifyResponse with address data
     */
    public GeoApifyResponse reverseGeocode(Double latitude, Double longitude) {
        try {
            logger.info("Reverse geocoding coordinates: {}, {}", latitude, longitude);
            
            String url = String.format("%s/geocode/reverse?lat=%s&lon=%s&apiKey=%s",
                    config.getGeoApifyBaseUrl(),
                    latitude,
                    longitude,
                    config.getGeoApifyApiKey());

            GeoApifyResponse response = restClient.get()
                    .uri(url)
                    .retrieve()
                    .body(GeoApifyResponse.class);

            logger.info("Reverse geocoding successful for coordinates: {}, {}", latitude, longitude);
            return response;
        } catch (RestClientException e) {
            logger.error("Error reverse geocoding coordinates: {}, {}", latitude, longitude, e);
            throw new RuntimeException("Failed to reverse geocode coordinates: " + e.getMessage(), e);
        }
    }

    /**
     * Search for places using text query
     * @param query The search query
     * @return GeoApifyResponse with places data
     */
    public GeoApifyResponse searchPlaces(String query) {
        try {
            logger.info("Searching places with query: {}", query);
            
            String url = String.format("%s/geocode/search?text=%s&apiKey=%s",
                    config.getGeoApifyBaseUrl(),
                    URLEncoder.encode(query, StandardCharsets.UTF_8),
                    config.getGeoApifyApiKey());

            GeoApifyResponse response = restClient.get()
                    .uri(url)
                    .retrieve()
                    .body(GeoApifyResponse.class);

            logger.info("Place search successful for query: {}", query);
            return response;
        } catch (RestClientException e) {
            logger.error("Error searching places with query: {}", query, e);
            throw new RuntimeException("Failed to search places: " + e.getMessage(), e);
        }
    }
}

