package microoservicios.service.microo.repository;

import microoservicios.service.microo.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    /**
     * Encuentra todos los comentarios de un servicio específico
     */
    List<Comment> findByServiceIdOrderByCreatedAtDesc(UUID serviceId);

    /**
     * Cuenta el número de comentarios de un servicio
     */
    long countByServiceId(UUID serviceId);

    /**
     * Encuentra un comentario por su commentId original (del microservicio de
     * comentarios)
     * para evitar duplicados
     */
    Optional<Comment> findByCommentId(Long commentId);

    /**
     * Verifica si ya existe un comentario con ese commentId
     */
    boolean existsByCommentId(Long commentId);
}
