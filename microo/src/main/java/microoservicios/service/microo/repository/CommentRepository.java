package microoservicios.service.microo.repository;

import microoservicios.service.microo.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByServiceIdOrderByCreatedAtDesc(UUID serviceId);
    long countByServiceId(UUID serviceId);
    Optional<Comment> findByCommentId(Long commentId);
    boolean existsByCommentId(Long commentId);
}
