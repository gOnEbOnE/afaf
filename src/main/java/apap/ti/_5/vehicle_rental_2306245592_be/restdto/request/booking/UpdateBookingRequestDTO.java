package apap.ti._5.vehicle_rental_2306245592_be.restdto.request.booking;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateBookingRequestDTO {
    
    @NotBlank(message = "Booking ID is required")
    private String id;
    
    @NotBlank(message = "Pick-up location is required")
    private String pickUpLocation;
    
    @NotBlank(message = "Drop-off location is required")
    private String dropOffLocation;
    
    @NotNull(message = "Pick-up time is required")
    private LocalDateTime pickUpTime;
    
    @NotNull(message = "Drop-off time is required")
    private LocalDateTime dropOffTime;
    
    @NotNull(message = "Capacity is required")
    @Min(value = 1, message = "Capacity must be at least 1")
    private Integer capacityNeeded;
    
    @NotBlank(message = "Transmission is required")
    private String transmissionNeeded;
    
    @NotNull(message = "Include driver status is required")
    private Boolean includeDriver;
}
