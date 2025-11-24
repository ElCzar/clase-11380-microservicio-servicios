package microoservicios.service.microo.controller;

import microoservicios.service.microo.entity.Comment;
import microoservicios.service.microo.services.CommentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/services/{serviceId}/comments")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    /**
     * Obtiene todos los comentarios de un servicio específico
     */
    @GetMapping
    public ResponseEntity<List<Comment>> getCommentsByServiceId(@PathVariable UUID serviceId) {
        List<Comment> comments = commentService.getCommentsByServiceId(serviceId);
        return ResponseEntity.ok(comments);
    }

    /**
     * Obtiene el conteo de comentarios de un servicio
     */
    @GetMapping("/count")
    public ResponseEntity<Long> getCommentCount(@PathVariable UUID serviceId) {
        long count = commentService.countCommentsByServiceId(serviceId);
        return ResponseEntity.ok(count);
    }
}
