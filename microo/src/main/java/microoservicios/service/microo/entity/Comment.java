package microoservicios.service.microo.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "comments")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "comment_id", nullable = false)
    private Long commentId; // ID del comentario en el microservicio de comentarios

    @Column(name = "service_id", nullable = false)
    private UUID serviceId;

    @Column(name = "profile_id")
    private Long profileId;

    @Column(nullable = false)
    private BigDecimal rating;

    @Column(length = 2000)
    private String content;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "received_at")
    private LocalDateTime receivedAt;

    // Constructors
    public Comment() {
        this.receivedAt = LocalDateTime.now();
    }

    public Comment(Long commentId, UUID serviceId, Long profileId, BigDecimal rating,
            String content, LocalDateTime createdAt) {
        this.commentId = commentId;
        this.serviceId = serviceId;
        this.profileId = profileId;
        this.rating = rating;
        this.content = content;
        this.createdAt = createdAt;
        this.receivedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCommentId() {
        return commentId;
    }

    public void setCommentId(Long commentId) {
        this.commentId = commentId;
    }

    public UUID getServiceId() {
        return serviceId;
    }

    public void setServiceId(UUID serviceId) {
        this.serviceId = serviceId;
    }

    public Long getProfileId() {
        return profileId;
    }

    public void setProfileId(Long profileId) {
        this.profileId = profileId;
    }

    public BigDecimal getRating() {
        return rating;
    }

    public void setRating(BigDecimal rating) {
        this.rating = rating;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getReceivedAt() {
        return receivedAt;
    }

    public void setReceivedAt(LocalDateTime receivedAt) {
        this.receivedAt = receivedAt;
    }
}
