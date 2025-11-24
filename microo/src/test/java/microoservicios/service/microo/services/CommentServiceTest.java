// package microoservicios.service.microo.services;

// import microoservicios.service.microo.dto.CommentResponseDto;
// import microoservicios.service.microo.entity.ServiceEntity;
// import microoservicios.service.microo.repository.ServiceRepository;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.extension.ExtendWith;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.mockito.junit.jupiter.MockitoExtension;

// import java.math.BigDecimal;
// import java.time.LocalDateTime;
// import java.util.Optional;
// import java.util.UUID;

// import static org.junit.jupiter.api.Assertions.*;
// import static org.mockito.ArgumentMatchers.any;
// import static org.mockito.Mockito.*;

// @ExtendWith(MockitoExtension.class)
// class CommentServiceTest {

//     @Mock
//     private ServiceRepository serviceRepository;

//     @InjectMocks
//     private CommentService commentService;

//     private ServiceEntity testService;
//     private UUID testServiceUuid;

//     @BeforeEach
//     void setUp() {
//         testServiceUuid = UUID.randomUUID();
//         testService = new ServiceEntity();
//         testService.setId(testServiceUuid);
//         testService.setTitle("Test Service");
//         testService.setAverageRating(0.0);
//         testService.setCommentCount(0);
//     }

//     @Test
//     void processCommentAndUpdateRating_FirstComment_ShouldSetRatingToNewValue() {
//         // Arrange
//         CommentResponseDto commentDto = new CommentResponseDto();
//         commentDto.setCommentId(1L);
//         commentDto.setServiceId(testServiceUuid.getMostSignificantBits());
//         commentDto.setRating(BigDecimal.valueOf(4.5));
//         commentDto.setContent("Great service!");
//         commentDto.setCreatedAt(LocalDateTime.now());

//         when(serviceRepository.findById(any(UUID.class))).thenReturn(Optional.of(testService));
//         when(serviceRepository.save(any(ServiceEntity.class))).thenReturn(testService);

//         // Act
//         commentService.processCommentAndUpdateRating(commentDto);

//         // Assert
//         verify(serviceRepository).save(argThat(service -> service.getAverageRating() == 4.5 &&
//                 service.getCommentCount() == 1));
//     }

//     @Test
//     void processCommentAndUpdateRating_SecondComment_ShouldCalculateAverage() {
//         // Arrange
//         testService.setAverageRating(4.0);
//         testService.setCommentCount(1);

//         CommentResponseDto commentDto = new CommentResponseDto();
//         commentDto.setCommentId(2L);
//         commentDto.setServiceId(testServiceUuid.getMostSignificantBits());
//         commentDto.setRating(BigDecimal.valueOf(5.0));
//         commentDto.setContent("Excellent!");
//         commentDto.setCreatedAt(LocalDateTime.now());

//         when(serviceRepository.findById(any(UUID.class))).thenReturn(Optional.of(testService));
//         when(serviceRepository.save(any(ServiceEntity.class))).thenReturn(testService);

//         // Act
//         commentService.processCommentAndUpdateRating(commentDto);

//         // Assert
//         verify(serviceRepository).save(argThat(service -> {
//             // Expected: (4.0 * 1 + 5.0) / 2 = 4.5
//             return Math.abs(service.getAverageRating() - 4.5) < 0.01 &&
//                     service.getCommentCount() == 2;
//         }));
//     }

//     @Test
//     void processCommentAndUpdateRating_MultipleComments_ShouldCalculateCorrectAverage() {
//         // Arrange
//         testService.setAverageRating(4.2);
//         testService.setCommentCount(5);

//         CommentResponseDto commentDto = new CommentResponseDto();
//         commentDto.setCommentId(6L);
//         commentDto.setServiceId(testServiceUuid.getMostSignificantBits());
//         commentDto.setRating(BigDecimal.valueOf(3.0));
//         commentDto.setContent("Good but could be better");
//         commentDto.setCreatedAt(LocalDateTime.now());

//         when(serviceRepository.findById(any(UUID.class))).thenReturn(Optional.of(testService));
//         when(serviceRepository.save(any(ServiceEntity.class))).thenReturn(testService);

//         // Act
//         commentService.processCommentAndUpdateRating(commentDto);

//         // Assert
//         verify(serviceRepository).save(argThat(service -> {
//             // Expected: (4.2 * 5 + 3.0) / 6 = 24.0 / 6 = 4.0
//             return Math.abs(service.getAverageRating() - 4.0) < 0.01 &&
//                     service.getCommentCount() == 6;
//         }));
//     }

//     @Test
//     void processCommentAndUpdateRating_ServiceNotFound_ShouldThrowException() {
//         // Arrange
//         CommentResponseDto commentDto = new CommentResponseDto();
//         commentDto.setCommentId(1L);
//         commentDto.setServiceId(999L);
//         commentDto.setRating(BigDecimal.valueOf(4.5));

//         when(serviceRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

//         // Act & Assert
//         assertThrows(RuntimeException.class, () -> commentService.processCommentAndUpdateRating(commentDto));
//     }
// }
