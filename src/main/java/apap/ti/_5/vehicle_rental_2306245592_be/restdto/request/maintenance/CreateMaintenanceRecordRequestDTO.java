package apap.ti._5.vehicle_rental_2306245592_be.restdto.request.maintenance;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateMaintenanceRecordRequestDTO {
    
    @NotBlank(message = "Vehicle ID is required")
    private String vehicleId;
    
    @NotNull(message = "Service date is required")
    private LocalDateTime serviceDate;
    
    @NotBlank(message = "Description is required")
    private String description;
    
    @NotNull(message = "Cost is required")
    @Min(value = 0, message = "Cost must be greater than or equal to 0")
    private Double cost;
    
    private String vendorNote;
}
