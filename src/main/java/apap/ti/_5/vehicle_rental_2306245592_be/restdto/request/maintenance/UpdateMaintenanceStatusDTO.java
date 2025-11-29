package apap.ti._5.vehicle_rental_2306245592_be.restdto.request.maintenance;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateMaintenanceStatusDTO {
    
    @NotBlank(message = "Status is required")
    private String status;
}
