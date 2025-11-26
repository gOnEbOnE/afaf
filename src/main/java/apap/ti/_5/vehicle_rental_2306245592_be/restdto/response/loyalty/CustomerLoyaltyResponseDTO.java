package apap.ti._5.vehicle_rental_2306245592_be.restdto.response.loyalty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerLoyaltyResponseDTO {
    private String customerId;
    private Integer loyaltyPoints;
}