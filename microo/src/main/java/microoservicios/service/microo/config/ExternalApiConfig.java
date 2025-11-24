package microoservicios.service.microo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class ExternalApiConfig {

    @Value("${external.api.geoapify.api-key}")
    private String geoApifyApiKey;

    @Value("${external.api.geoapify.base-url:https://api.geoapify.com/v1}")
    private String geoApifyBaseUrl;

    @Value("${external.api.openmeteo.base-url:https://api.open-meteo.com/v1}")
    private String openMeteoBaseUrl;

    @Bean
    public RestClient restClient() {
        return RestClient.builder()
                .build();
    }

    public String getGeoApifyApiKey() {
        return geoApifyApiKey;
    }

    public String getGeoApifyBaseUrl() {
        return geoApifyBaseUrl;
    }

    public String getOpenMeteoBaseUrl() {
        return openMeteoBaseUrl;
    }
}

