package apap.ti._5.vehicle_rental_2306245592_be.restdto.response.loyalty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoyaltyPointsResponseDTO {
    private String customerId;
    private Integer totalPoints;
    private Integer addedPoints;
    private String message;
}