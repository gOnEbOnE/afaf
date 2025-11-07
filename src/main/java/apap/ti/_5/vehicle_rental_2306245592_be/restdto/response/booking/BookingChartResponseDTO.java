package apap.ti._5.vehicle_rental_2306245592_be.restdto.response.booking;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingChartResponseDTO {
    private int status;
    private String message;
    private String timestamp;
    private BookingChartDataDTO data;
}