package microoservicios.service.microo.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import microoservicios.service.microo.dto.CommentResponseDto;
import microoservicios.service.microo.services.CommentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Kafka Listener para consumir comentarios del topic comments-response
 * Usa @KafkaListener nativo de Spring Kafka (más simple y directo que Spring
 * Cloud Stream)
 */
@Component
public class CommentKafkaListener {

    private static final Logger logger = LoggerFactory.getLogger(CommentKafkaListener.class);

    private final CommentService commentService;
    private final ObjectMapper objectMapper;

    public CommentKafkaListener(CommentService commentService, ObjectMapper objectMapper) {
        this.commentService = commentService;
        this.objectMapper = objectMapper;
        logger.info("╔════════════════════════════════════════════════════════════════╗");
        logger.info("║   CommentKafkaListener INITIALIZED                            ║");
        logger.info("║   Using native @KafkaListener                                 ║");
        logger.info("║   Topic: comments-response                                     ║");
        logger.info("║   Group: services-comment-consumer-group                       ║");
        logger.info("╚════════════════════════════════════════════════════════════════╝");
    }

    /**
     * Escucha mensajes del topic comments-response
     * Este método se ejecuta automáticamente cuando llega un mensaje
     */
    @KafkaListener(topics = "comments-response", groupId = "services-comment-consumer-group", containerFactory = "kafkaListenerContainerFactory")
    public void consumeComment(String message) {
        logger.info("╔════════════════════════════════════════════════════════════════╗");
        logger.info("║   🎉 MENSAJE RECIBIDO DE KAFKA!                               ║");
        logger.info("╚════════════════════════════════════════════════════════════════╝");
        logger.info("📦 Raw message: {}", message);

        try {
            // Decodificar el mensaje (puede ser Base64 o JSON directo)
            String jsonPayload = decodeMessage(message);
            logger.info("🔓 JSON decodificado: {}", jsonPayload);

            // Deserializar el JSON a CommentResponseDto
            CommentResponseDto commentDto = objectMapper.readValue(jsonPayload, CommentResponseDto.class);

            logger.info("✅ Comentario deserializado correctamente:");
            logger.info("   → Comment ID: {}", commentDto.getCommentId());
            logger.info("   → Service UUID: {}", commentDto.getServiceUuid());
            logger.info("   → Service ID Hash: {}", commentDto.getServiceIdHash());
            logger.info("   → Profile ID: {}", commentDto.getProfileId());
            logger.info("   → Rating: {}", commentDto.getRating());
            logger.info("   → Content: {}", commentDto.getContent());
            logger.info("   → Created At: {}", commentDto.getCreatedAt());

            // Validaciones
            if (commentDto.getServiceId() == null) {
                logger.error("❌ ServiceUuid es null o inválido - mensaje ignorado");
                return;
            }

            if (commentDto.getRating() == null) {
                logger.error("❌ Rating es null - mensaje ignorado");
                return;
            }

            // Procesar el comentario y actualizar el rating
            logger.info("🔄 Procesando comentario y actualizando rating del servicio...");
            commentService.processCommentAndUpdateRating(commentDto);

            logger.info("╔════════════════════════════════════════════════════════════════╗");
            logger.info("║   ✅ COMENTARIO PROCESADO EXITOSAMENTE!                       ║");
            logger.info("║   Comment ID: {}                                          ", commentDto.getCommentId());
            logger.info("║   Service UUID: {}                                        ", commentDto.getServiceUuid());
            logger.info("╚════════════════════════════════════════════════════════════════╝");

        } catch (Exception e) {
            logger.error("╔════════════════════════════════════════════════════════════════╗");
            logger.error("║   ❌ ERROR AL PROCESAR COMENTARIO                              ║");
            logger.error("╚════════════════════════════════════════════════════════════════╝");
            logger.error("❌ Error: {}", e.getMessage());
            logger.error("❌ Mensaje original: {}", message);
            logger.error("❌ Stack trace:", e);
        }
    }

    /**
     * Decodifica el mensaje detectando si es Base64 o JSON directo
     * Basado en el ejemplo del microservicio de comentarios
     */
    private String decodeMessage(String rawMessage) {
        try {
            // Si ya es JSON directo, retornarlo
            if (rawMessage.trim().startsWith("{")) {
                logger.debug("✅ Mensaje detectado como JSON directo");
                return rawMessage;
            }

            // Remover comillas si están presentes
            if (rawMessage.startsWith("\"") && rawMessage.endsWith("\"")) {
                logger.debug("🔧 Removiendo comillas del mensaje");
                rawMessage = rawMessage.substring(1, rawMessage.length() - 1);
            }

            // Intentar decodificar como Base64
            logger.debug("🔓 Intentando decodificar como Base64...");
            byte[] decoded = Base64.getDecoder().decode(rawMessage);
            String decodedStr = new String(decoded, StandardCharsets.UTF_8);

            if (decodedStr.trim().startsWith("{")) {
                logger.debug("✅ Base64 decodificado exitosamente a JSON");
                return decodedStr;
            } else {
                logger.warn("⚠️ Base64 decodificado pero no es JSON válido: {}", decodedStr);
                return rawMessage;
            }

        } catch (IllegalArgumentException e) {
            logger.debug("ℹ️ No es Base64 válido, usando como String directo");
            return rawMessage;
        } catch (Exception e) {
            logger.warn("⚠️ Error en decodificación, usando mensaje original: {}", e.getMessage());
            return rawMessage;
        }
    }
}
