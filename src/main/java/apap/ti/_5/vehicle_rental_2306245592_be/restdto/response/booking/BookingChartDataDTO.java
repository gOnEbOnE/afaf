package apap.ti._5.vehicle_rental_2306245592_be.restdto.response.booking;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingChartDataDTO {
    private String period; // "Monthly" or "Quarterly"
    private int year;
    private List<String> labels; // Month names or Quarter names
    private List<Integer> data; // Booking counts
    private String backgroundColor;
    private String borderColor;
}
