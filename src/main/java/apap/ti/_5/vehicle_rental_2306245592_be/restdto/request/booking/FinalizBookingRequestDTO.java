package apap.ti._5.vehicle_rental_2306245592_be.restdto.request.booking;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FinalizBookingRequestDTO {
    private CreateBookingRequestDTO bookingDTO;
    private AddAddOnsRequestDTO addOnsDTO;
}
