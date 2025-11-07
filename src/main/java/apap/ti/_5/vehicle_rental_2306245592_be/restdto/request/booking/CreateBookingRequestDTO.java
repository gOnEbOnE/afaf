package apap.ti._5.vehicle_rental_2306245592_be.restdto.request.booking;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateBookingRequestDTO {
    private Boolean includeDriver;
    private String pickUpLocation;
    private String dropOffLocation;
    private LocalDateTime pickUpTime;
    private LocalDateTime dropOffTime;
    private Integer capacityNeeded;
    private String transmissionNeeded;
}
