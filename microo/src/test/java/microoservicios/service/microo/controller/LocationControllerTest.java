package microoservicios.service.microo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import microoservicios.service.microo.dto.external.GeoApifyResponse;
import microoservicios.service.microo.services.external.GeoApifyService;
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

@WebMvcTest(LocationController.class)
@ActiveProfiles("test")
@TestPropertySource(properties = {
    "GEOAPIFY_API_KEY=test-geoapify-key",
    "GEOAPIFY_BASE_URL=http://localhost:8089/mock-geoapify",
    "JWT_JWK_SET_URI=http://localhost:8180/realms/test/protocol/openid-connect/certs",
    "KAFKA_BOOTSTRAP_SERVERS=embedded",
    "KAFKA_BROKERS=embedded"
})
class LocationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GeoApifyService geoApifyService;

    @Autowired
    private ObjectMapper objectMapper;

    private GeoApifyResponse mockResponse;

    @BeforeEach
    void setUp() {
        // Create a mock response for testing
        mockResponse = new GeoApifyResponse();
        // You can set properties of mockResponse here if needed
    }

    @Test
    @WithMockUser
    void testGeocode_ValidAddress_ShouldReturnOk() throws Exception {
        // Given
        String address = "New York";
        when(geoApifyService.geocode(address)).thenReturn(mockResponse);

        // When & Then
        mockMvc.perform(get("/api/v1/location/geocode")
                .param("address", address))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"));
    }

    @Test
    @WithMockUser
    void testGeocode_EmptyAddress_ShouldReturnBadRequest() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/location/geocode")
                .param("address", ""))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void testGeocode_NullAddress_ShouldReturnBadRequest() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/location/geocode"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void testGeocode_ServiceException_ShouldReturnInternalServerError() throws Exception {
        // Given
        String address = "Invalid Address";
        when(geoApifyService.geocode(address)).thenThrow(new RuntimeException("Service error"));

        // When & Then
        mockMvc.perform(get("/api/v1/location/geocode")
                .param("address", address))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @WithMockUser
    void testReverseGeocode_ValidCoordinates_ShouldReturnOk() throws Exception {
        // Given
        Double lat = 40.7128;
        Double lon = -74.0060;
        when(geoApifyService.reverseGeocode(lat, lon)).thenReturn(mockResponse);

        // When & Then
        mockMvc.perform(get("/api/v1/location/reverse-geocode")
                .param("lat", lat.toString())
                .param("lon", lon.toString()))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"));
    }

    @Test
    @WithMockUser
    void testReverseGeocode_NullLatitude_ShouldReturnBadRequest() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/location/reverse-geocode")
                .param("lon", "-74.0060"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void testReverseGeocode_NullLongitude_ShouldReturnBadRequest() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/location/reverse-geocode")
                .param("lat", "40.7128"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void testReverseGeocode_ServiceException_ShouldReturnInternalServerError() throws Exception {
        // Given
        Double lat = 40.7128;
        Double lon = -74.0060;
        when(geoApifyService.reverseGeocode(lat, lon)).thenThrow(new RuntimeException("Service error"));

        // When & Then
        mockMvc.perform(get("/api/v1/location/reverse-geocode")
                .param("lat", lat.toString())
                .param("lon", lon.toString()))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @WithMockUser
    void testSearchPlaces_ValidQuery_ShouldReturnOk() throws Exception {
        // Given
        String query = "restaurant";
        when(geoApifyService.searchPlaces(query)).thenReturn(mockResponse);

        // When & Then
        mockMvc.perform(get("/api/v1/location/places")
                .param("query", query))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"));
    }

    @Test
    @WithMockUser
    void testSearchPlaces_EmptyQuery_ShouldReturnBadRequest() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/location/places")
                .param("query", ""))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void testSearchPlaces_NullQuery_ShouldReturnBadRequest() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/location/places"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void testSearchPlaces_ServiceException_ShouldReturnInternalServerError() throws Exception {
        // Given
        String query = "invalid query";
        when(geoApifyService.searchPlaces(query)).thenThrow(new RuntimeException("Service error"));

        // When & Then
        mockMvc.perform(get("/api/v1/location/places")
                .param("query", query))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void testGeocode_WithoutAuthentication_ShouldReturnUnauthorized() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/location/geocode")
                .param("address", "New York"))
                .andExpect(status().isUnauthorized());
    }
}