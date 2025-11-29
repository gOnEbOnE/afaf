package apap.ti._5.vehicle_rental_2306245592_be.restdto.request.loyalty;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCouponRequestDTO {
    private String name;
    
    private String description;
    
    @Min(value = 1, message = "Points must be at least 1")
    private Integer points;
    
    @Min(value = 1, message = "Percent off must be between 1 and 100")
    private Integer percentOff;
}
