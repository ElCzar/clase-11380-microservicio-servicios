package microoservicios.service.microo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import microoservicios.service.microo.dto.external.OpenMeteoResponse;
import microoservicios.service.microo.services.external.OpenMeteoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(WeatherController.class)
@ActiveProfiles("test")
@TestPropertySource(properties = {
    "OPENMETEO_BASE_URL=http://localhost:8090/mock-openmeteo",
    "JWT_JWK_SET_URI=http://localhost:8180/realms/test/protocol/openid-connect/certs",
    "KAFKA_BOOTSTRAP_SERVERS=embedded",
    "KAFKA_BROKERS=embedded"
})
class WeatherControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OpenMeteoService openMeteoService;

    @Autowired
    private ObjectMapper objectMapper;

    private OpenMeteoResponse mockResponse;

    @BeforeEach
    void setUp() {
        // Create a mock response for testing
        mockResponse = new OpenMeteoResponse();
        // You can set properties of mockResponse here if needed
    }

    @Test
    @WithMockUser
    void testGetCurrentWeather_ValidCoordinates_ShouldReturnOk() throws Exception {
        // Given
        Double lat = 40.7128;
        Double lon = -74.0060;
        when(openMeteoService.getCurrentWeather(lat, lon)).thenReturn(mockResponse);

        // When & Then
        mockMvc.perform(get("/api/v1/weather/current")
                .param("lat", lat.toString())
                .param("lon", lon.toString()))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"));
    }

    @Test
    @WithMockUser
    void testGetCurrentWeather_NullLatitude_ShouldReturnBadRequest() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/weather/current")
                .param("lon", "-74.0060"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void testGetCurrentWeather_NullLongitude_ShouldReturnBadRequest() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/weather/current")
                .param("lat", "40.7128"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void testGetCurrentWeather_ServiceException_ShouldReturnInternalServerError() throws Exception {
        // Given
        Double lat = 40.7128;
        Double lon = -74.0060;
        when(openMeteoService.getCurrentWeather(lat, lon)).thenThrow(new RuntimeException("Service error"));

        // When & Then
        mockMvc.perform(get("/api/v1/weather/current")
                .param("lat", lat.toString())
                .param("lon", lon.toString()))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @WithMockUser
    void testGetForecast_ValidCoordinates_ShouldReturnOk() throws Exception {
        // Given
        Double lat = 40.7128;
        Double lon = -74.0060;
        Integer days = 5;
        when(openMeteoService.getForecast(lat, lon, days)).thenReturn(mockResponse);

        // When & Then
        mockMvc.perform(get("/api/v1/weather/forecast")
                .param("lat", lat.toString())
                .param("lon", lon.toString())
                .param("days", days.toString()))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"));
    }

    @Test
    @WithMockUser
    void testGetForecast_WithDefaultDays_ShouldReturnOk() throws Exception {
        // Given
        Double lat = 40.7128;
        Double lon = -74.0060;
        when(openMeteoService.getForecast(lat, lon, 7)).thenReturn(mockResponse); // Default is 7 days

        // When & Then
        mockMvc.perform(get("/api/v1/weather/forecast")
                .param("lat", lat.toString())
                .param("lon", lon.toString()))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"));
    }

    @Test
    @WithMockUser
    void testGetForecast_NullCoordinates_ShouldReturnBadRequest() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/weather/forecast")
                .param("days", "5"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void testGetForecast_ServiceException_ShouldReturnInternalServerError() throws Exception {
        // Given
        Double lat = 40.7128;
        Double lon = -74.0060;
        Integer days = 5;
        when(openMeteoService.getForecast(lat, lon, days)).thenThrow(new RuntimeException("Service error"));

        // When & Then
        mockMvc.perform(get("/api/v1/weather/forecast")
                .param("lat", lat.toString())
                .param("lon", lon.toString())
                .param("days", days.toString()))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @WithMockUser
    void testGetHourlyForecast_ValidCoordinates_ShouldReturnOk() throws Exception {
        // Given
        Double lat = 40.7128;
        Double lon = -74.0060;
        Integer hours = 24;
        when(openMeteoService.getHourlyForecast(lat, lon, hours)).thenReturn(mockResponse);

        // When & Then
        mockMvc.perform(get("/api/v1/weather/hourly")
                .param("lat", lat.toString())
                .param("lon", lon.toString())
                .param("hours", hours.toString()))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"));
    }

    @Test
    @WithMockUser
    void testGetHourlyForecast_WithDefaultHours_ShouldReturnOk() throws Exception {
        // Given
        Double lat = 40.7128;
        Double lon = -74.0060;
        when(openMeteoService.getHourlyForecast(lat, lon, 48)).thenReturn(mockResponse); // Default is 48 hours

        // When & Then
        mockMvc.perform(get("/api/v1/weather/hourly")
                .param("lat", lat.toString())
                .param("lon", lon.toString()))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"));
    }

    @Test
    @WithMockUser
    void testGetHourlyForecast_NullCoordinates_ShouldReturnBadRequest() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/weather/hourly")
                .param("hours", "24"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void testGetHourlyForecast_ServiceException_ShouldReturnInternalServerError() throws Exception {
        // Given
        Double lat = 40.7128;
        Double lon = -74.0060;
        Integer hours = 24;
        when(openMeteoService.getHourlyForecast(lat, lon, hours)).thenThrow(new RuntimeException("Service error"));

        // When & Then
        mockMvc.perform(get("/api/v1/weather/hourly")
                .param("lat", lat.toString())
                .param("lon", lon.toString())
                .param("hours", hours.toString()))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void testGetCurrentWeather_WithoutAuthentication_ShouldReturnUnauthorized() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/weather/current")
                .param("lat", "40.7128")
                .param("lon", "-74.0060"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testGetForecast_WithoutAuthentication_ShouldReturnUnauthorized() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/weather/forecast")
                .param("lat", "40.7128")
                .param("lon", "-74.0060"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testGetHourlyForecast_WithoutAuthentication_ShouldReturnUnauthorized() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/weather/hourly")
                .param("lat", "40.7128")
                .param("lon", "-74.0060"))
                .andExpect(status().isUnauthorized());
    }
}