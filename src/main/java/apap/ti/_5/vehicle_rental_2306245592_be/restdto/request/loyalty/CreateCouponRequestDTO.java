package apap.ti._5.vehicle_rental_2306245592_be.restdto.request.loyalty;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateCouponRequestDTO {
    
    @NotBlank(message = "Name is required")
    private String name;
    
    @NotBlank(message = "Description is required")
    private String description;
    
    @NotNull(message = "Points is required")
    private Integer points;
    
    @NotNull(message = "Percent off is required")
    private Integer percentOff;
}
