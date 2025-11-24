package microoservicios.service.microo.services;

import microoservicios.service.microo.dto.CommentResponseDto;
import microoservicios.service.microo.entity.Comment;
import microoservicios.service.microo.entity.ServiceEntity;
import microoservicios.service.microo.repository.CommentRepository;
import microoservicios.service.microo.repository.ServiceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;

@Service
public class CommentService {

    private static final Logger logger = LoggerFactory.getLogger(CommentService.class);

    private final ServiceRepository serviceRepository;
    private final CommentRepository commentRepository;

    public CommentService(ServiceRepository serviceRepository, CommentRepository commentRepository) {
        this.serviceRepository = serviceRepository;
        this.commentRepository = commentRepository;
    }

    /**
     * Procesa un comentario recibido desde Kafka y actualiza el rating promedio del
     * servicio
     */
    @Transactional
    public void processCommentAndUpdateRating(CommentResponseDto commentDto) {
        try {
            // Verificar si el comentario ya fue procesado (evitar duplicados)
            if (commentRepository.existsByCommentId(commentDto.getCommentId())) {
                logger.info("Comment {} already processed, skipping", commentDto.getCommentId());
                return;
            }

            // Obtener el UUID del servicio directamente del DTO
            UUID serviceUuid = commentDto.getServiceId();
            if (serviceUuid == null) {
                logger.error("Invalid serviceUuid in comment {}: {}", commentDto.getCommentId(),
                        commentDto.getServiceUuid());
                throw new RuntimeException("Invalid or missing serviceUuid");
            }

            // Guardar el comentario en la base de datos
            Comment comment = new Comment(
                    commentDto.getCommentId(),
                    serviceUuid,
                    commentDto.getProfileId(),
                    commentDto.getRating(),
                    commentDto.getContent(),
                    commentDto.getCreatedAt());
            commentRepository.save(comment);
            logger.info("Saved comment {} for service {}", commentDto.getCommentId(), serviceUuid);

            // Buscar el servicio
            ServiceEntity service = serviceRepository.findById(serviceUuid)
                    .orElseThrow(() -> new RuntimeException(
                            "Service not found with UUID: " + serviceUuid));

            // Obtener datos actuales del servicio
            Double currentRating = service.getAverageRating() != null ? service.getAverageRating() : 0.0;
            Integer currentCount = service.getCommentCount() != null ? service.getCommentCount() : 0;
            BigDecimal newRating = commentDto.getRating();

            // Calcular el nuevo promedio usando el conteo
            Double updatedRating = calculateNewAverageRating(currentRating, currentCount, newRating);

            // Actualizar el rating y el contador del servicio
            service.setAverageRating(updatedRating);
            service.setCommentCount(currentCount + 1);
            serviceRepository.save(service);

            logger.info("Updated service {}: rating {} -> {}, comments {} -> {}",
                    serviceUuid, currentRating, updatedRating, currentCount, currentCount + 1);

        } catch (Exception e) {
            logger.error("Error processing comment for service {}: {}",
                    commentDto.getServiceUuid(), e.getMessage(), e);
            throw new RuntimeException("Failed to process comment", e);
        }
    }

    /**
     * Obtiene todos los comentarios de un servicio específico
     */
    public List<Comment> getCommentsByServiceId(UUID serviceId) {
        return commentRepository.findByServiceIdOrderByCreatedAtDesc(serviceId);
    }

    /**
     * Cuenta los comentarios de un servicio
     */
    public long countCommentsByServiceId(UUID serviceId) {
        return commentRepository.countByServiceId(serviceId);
    }

    /**
     * Calcula el nuevo rating promedio del servicio usando la fórmula:
     * nuevo_promedio = (promedio_actual * cantidad_actual + nuevo_rating) /
     * (cantidad_actual + 1)
     */
    private Double calculateNewAverageRating(Double currentRating, Integer currentCount, BigDecimal newRating) {
        if (currentCount == 0 || currentRating == 0.0) {
            // Primer comentario
            return newRating.doubleValue();
        }

        // Calcular promedio ponderado
        BigDecimal currentRatingBd = BigDecimal.valueOf(currentRating);
        BigDecimal currentCountBd = BigDecimal.valueOf(currentCount);

        // (currentRating * currentCount + newRating) / (currentCount + 1)
        BigDecimal totalRating = currentRatingBd.multiply(currentCountBd).add(newRating);
        BigDecimal newCount = currentCountBd.add(BigDecimal.ONE);
        BigDecimal average = totalRating.divide(newCount, 2, RoundingMode.HALF_UP);

        return average.doubleValue();
    }
}
