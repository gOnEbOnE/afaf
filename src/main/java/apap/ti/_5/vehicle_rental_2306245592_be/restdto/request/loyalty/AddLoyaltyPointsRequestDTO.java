package apap.ti._5.vehicle_rental_2306245592_be.restdto.request.loyalty;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddLoyaltyPointsRequestDTO {
    @NotNull(message = "Customer ID is required")
    private String customerId;
    
    @NotNull(message = "Points is required")
    @Positive(message = "Points must be positive")
    private Integer points;
}