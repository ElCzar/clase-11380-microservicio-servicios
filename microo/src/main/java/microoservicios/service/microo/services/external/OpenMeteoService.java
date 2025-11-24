package microoservicios.service.microo.services.external;

import microoservicios.service.microo.config.ExternalApiConfig;
import microoservicios.service.microo.dto.external.OpenMeteoResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Service
public class OpenMeteoService {

    private static final Logger logger = LoggerFactory.getLogger(OpenMeteoService.class);
    private final RestClient restClient;
    private final ExternalApiConfig config;

    public OpenMeteoService(RestClient restClient, ExternalApiConfig config) {
        this.restClient = restClient;
        this.config = config;
    }

    /**
     * Get current weather for given coordinates
     * @param latitude The latitude
     * @param longitude The longitude
     * @return OpenMeteoResponse with current weather data
     */
    public OpenMeteoResponse getCurrentWeather(Double latitude, Double longitude) {
        try {
            logger.info("Fetching current weather for coordinates: {}, {}", latitude, longitude);
            
            String url = String.format("%s/forecast?latitude=%s&longitude=%s&current=temperature_2m,relative_humidity_2m,weather_code,wind_speed_10m",
                    config.getOpenMeteoBaseUrl(),
                    latitude,
                    longitude);

            OpenMeteoResponse response = restClient.get()
                    .uri(url)
                    .retrieve()
                    .body(OpenMeteoResponse.class);

            logger.info("Current weather fetched successfully for coordinates: {}, {}", latitude, longitude);
            return response;
        } catch (RestClientException e) {
            logger.error("Error fetching current weather for coordinates: {}, {}", latitude, longitude, e);
            throw new RuntimeException("Failed to fetch current weather: " + e.getMessage(), e);
        }
    }

    /**
     * Get weather forecast for given coordinates
     * @param latitude The latitude
     * @param longitude The longitude
     * @param days Number of days for forecast (default: 7)
     * @return OpenMeteoResponse with forecast data
     */
    public OpenMeteoResponse getForecast(Double latitude, Double longitude, Integer days) {
        try {
            int forecastDays = (days != null && days > 0) ? days : 7;
            logger.info("Fetching {} day forecast for coordinates: {}, {}", forecastDays, latitude, longitude);
            
            String url = String.format("%s/forecast?latitude=%s&longitude=%s&daily=temperature_2m_max,temperature_2m_min,weather_code&timezone=auto",
                    config.getOpenMeteoBaseUrl(),
                    latitude,
                    longitude);

            OpenMeteoResponse response = restClient.get()
                    .uri(url)
                    .retrieve()
                    .body(OpenMeteoResponse.class);

            logger.info("Forecast fetched successfully for coordinates: {}, {}", latitude, longitude);
            return response;
        } catch (RestClientException e) {
            logger.error("Error fetching forecast for coordinates: {}, {}", latitude, longitude, e);
            throw new RuntimeException("Failed to fetch forecast: " + e.getMessage(), e);
        }
    }

    /**
     * Get hourly weather forecast for given coordinates
     * @param latitude The latitude
     * @param longitude The longitude
     * @param hours Number of hours for forecast (default: 48)
     * @return OpenMeteoResponse with hourly forecast data
     */
    public OpenMeteoResponse getHourlyForecast(Double latitude, Double longitude, Integer hours) {
        try {
            int forecastHours = (hours != null && hours > 0) ? hours : 48;
            logger.info("Fetching {} hour forecast for coordinates: {}, {}", forecastHours, latitude, longitude);
            
            String url = String.format("%s/forecast?latitude=%s&longitude=%s&hourly=temperature_2m,relative_humidity_2m,weather_code&forecast_days=%d",
                    config.getOpenMeteoBaseUrl(),
                    latitude,
                    longitude,
                    (forecastHours / 24) + 1); // Convert hours to days (round up)

            OpenMeteoResponse response = restClient.get()
                    .uri(url)
                    .retrieve()
                    .body(OpenMeteoResponse.class);

            logger.info("Hourly forecast fetched successfully for coordinates: {}, {}", latitude, longitude);
            return response;
        } catch (RestClientException e) {
            logger.error("Error fetching hourly forecast for coordinates: {}, {}", latitude, longitude, e);
            throw new RuntimeException("Failed to fetch hourly forecast: " + e.getMessage(), e);
        }
    }
}

