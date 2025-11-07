package apap.ti._5.vehicle_rental_2306245592_be.restdto.request.booking;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateAddOnsRequestDTO {
    
    @NotBlank(message = "Booking ID is required")
    private String bookingId;
    
    private List<String> selectedAddOnIds;
}
