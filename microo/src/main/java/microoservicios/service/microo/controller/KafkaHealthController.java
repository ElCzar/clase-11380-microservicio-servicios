package microoservicios.service.microo.controller;

import org.springframework.cloud.function.context.FunctionCatalog;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * Controller to verify Kafka consumer registration and health
 */
@RestController
@RequestMapping("/api/v1/kafka")
public class KafkaHealthController {

    private final FunctionCatalog functionCatalog;

    public KafkaHealthController(FunctionCatalog functionCatalog) {
        this.functionCatalog = functionCatalog;
    }

    @GetMapping("/functions")
    public ResponseEntity<Map<String, Object>> getRegisteredFunctions() {
        Map<String, Object> response = new HashMap<>();

        Set<String> functionNames = functionCatalog.getNames(null);
        response.put("registeredFunctions", functionNames);
        response.put("functionCount", functionNames.size());

        Map<String, Boolean> expectedFunctions = new HashMap<>();
        expectedFunctions.put("serviceRequest", functionNames.contains("serviceRequest"));
        expectedFunctions.put("commentResponse", functionNames.contains("commentResponse"));

        response.put("expectedFunctions", expectedFunctions);

        boolean allRegistered = expectedFunctions.values().stream().allMatch(b -> b);
        response.put("status", allRegistered ? "OK" : "MISSING_FUNCTIONS");

        return ResponseEntity.ok(response);
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> kafkaHealth() {
        Map<String, String> health = new HashMap<>();
        health.put("status", "UP");
        health.put("service", "microservicio-servicios");
        health.put("kafka", "configured");
        return ResponseEntity.ok(health);
    }
}
