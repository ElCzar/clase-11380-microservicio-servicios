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


// Kafka Listener para consumir comentarios del topic comments-response
@Component
public class CommentKafkaListener {

    private static final Logger logger = LoggerFactory.getLogger(CommentKafkaListener.class);

    private final CommentService commentService;
    private final ObjectMapper objectMapper;

    public CommentKafkaListener(CommentService commentService, ObjectMapper objectMapper) {
        this.commentService = commentService;
        this.objectMapper = objectMapper;
    }

    
    // Escucha mensajes del topic comments-response
    @KafkaListener(topics = "comments-response", groupId = "services-comment-consumer-group", containerFactory = "kafkaListenerContainerFactory")
    public void consumeComment(String message) {
        logger.info("\n\n MENSAJE RECIBIDO DE KAFKA \n\n");
        logger.info("Mensaje: {}", message);

        try {
            // Decodificar el mensaje
            String jsonPayload = decodeMessage(message);
            logger.info("JSON decodificado: {}", jsonPayload);

            // Deserializar el JSON a CommentResponseDto
            CommentResponseDto commentDto = objectMapper.readValue(jsonPayload, CommentResponseDto.class);

            logger.info("Comentario deserializado correctamente:");
            logger.info("   - Comment ID: {}", commentDto.getCommentId());
            logger.info("   - Service UUID: {}", commentDto.getServiceUuid());
            logger.info("   - Service ID Hash: {}", commentDto.getServiceIdHash());
            logger.info("   - Profile ID: {}", commentDto.getProfileId());
            logger.info("   - Rating: {}", commentDto.getRating());
            logger.info("   - Content: {}", commentDto.getContent());
            logger.info("   - Created At: {}", commentDto.getCreatedAt());

            // Validaciones
            if (commentDto.getServiceId() == null) {
                logger.error("ServiceUuid es null o inválido");
                return;
            }

            if (commentDto.getRating() == null) {
                logger.error("Rating es null - mensaje ignorado");
                return;
            }

            // Procesar el comentario y actualizar el rating
            logger.info("Procesando comentario y actualizando rating del servicio...");
            commentService.processCommentAndUpdateRating(commentDto);

            logger.info("Comentario procesado exitosamente!");
            logger.info("Comment ID: {}", commentDto.getCommentId());
            logger.info("Service UUID: {}", commentDto.getServiceUuid());
        } catch (Exception e) {
            logger.error("ERROR AL PROCESAR COMENTARIO");
            logger.error("Error: {}", e.getMessage());
            logger.error("Mensaje original: {}", message);
            logger.error("Stack trace:", e);
        }
    }

    // Decodifica el mensaje detectando si es Base64 o JSON 
    private String decodeMessage(String rawMessage) {
        try {
            // Si ya es JSON directo, retornarlo
            if (rawMessage.trim().startsWith("{")) {
                logger.debug("Mensaje detectado como JSON directo");
                return rawMessage;
            }

            // Remover comillas si están presentes
            if (rawMessage.startsWith("\"") && rawMessage.endsWith("\"")) {
                logger.debug("Removiendo comillas del mensaje");
                rawMessage = rawMessage.substring(1, rawMessage.length() - 1);
            }

            // Decodificar como Base64
            logger.debug("Intentando decodificar como Base64...");
            byte[] decoded = Base64.getDecoder().decode(rawMessage);
            String decodedStr = new String(decoded, StandardCharsets.UTF_8);

            if (decodedStr.trim().startsWith("{")) {
                logger.debug("Base64 decodificado exitosamente a JSON");
                return decodedStr;
            } else {
                logger.warn("Base64 decodificado pero no es JSON válido: {}", decodedStr);
                return rawMessage;
            }

        } catch (IllegalArgumentException e) {
            logger.debug("No es Base64 válido, usando como String directo");
            return rawMessage;
        } catch (Exception e) {
            logger.warn("Error en decodificación, usando mensaje original: {}", e.getMessage());
            return rawMessage;
        }
    }
}
