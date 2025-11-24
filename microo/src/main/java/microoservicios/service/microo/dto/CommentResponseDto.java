package microoservicios.service.microo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import microoservicios.service.microo.dto.deserializer.FlexibleLocalDateTimeDeserializer;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class CommentResponseDto {

    @JsonProperty("commentId")
    private Long commentId;

    @JsonProperty("serviceUuid")
    private String serviceUuid; // UUID del servicio como String

    @JsonProperty("serviceIdHash")
    private Long serviceIdHash; // Hash numérico del serviceId (si es necesario)

    @JsonProperty("profileId")
    private Long profileId;

    @JsonProperty("rating")
    private BigDecimal rating;

    @JsonProperty("content")
    private String content;

    @JsonProperty("createdAt")
    @JsonDeserialize(using = FlexibleLocalDateTimeDeserializer.class)
    private LocalDateTime createdAt;

    // Constructors
    public CommentResponseDto() {
    }

    public CommentResponseDto(Long commentId, String serviceUuid, Long serviceIdHash, Long profileId,
            BigDecimal rating, String content, LocalDateTime createdAt) {
        this.commentId = commentId;
        this.serviceUuid = serviceUuid;
        this.serviceIdHash = serviceIdHash;
        this.profileId = profileId;
        this.rating = rating;
        this.content = content;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public Long getCommentId() {
        return commentId;
    }

    public void setCommentId(Long commentId) {
        this.commentId = commentId;
    }

    public String getServiceUuid() {
        return serviceUuid;
    }

    public void setServiceUuid(String serviceUuid) {
        this.serviceUuid = serviceUuid;
    }

    /**
     * Convierte el serviceUuid String a UUID
     * Este es el ID del servicio que usamos internamente
     */
    public UUID getServiceId() {
        if (serviceUuid == null || serviceUuid.trim().isEmpty()) {
            return null;
        }
        try {
            return UUID.fromString(serviceUuid);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public Long getServiceIdHash() {
        return serviceIdHash;
    }

    public void setServiceIdHash(Long serviceIdHash) {
        this.serviceIdHash = serviceIdHash;
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

    @Override
    public String toString() {
        return "CommentResponseDto{" +
                "commentId=" + commentId +
                ", serviceUuid='" + serviceUuid + '\'' +
                ", serviceIdHash=" + serviceIdHash +
                ", profileId=" + profileId +
                ", rating=" + rating +
                ", content='" + content + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
