package apap.ti._5.vehicle_rental_2306245592_be.restdto.request.loyalty;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCouponRequestDTO {
    
    private String name;
    
    private String description;
    
    private Integer points;
    
    private Integer percentOff;
}
