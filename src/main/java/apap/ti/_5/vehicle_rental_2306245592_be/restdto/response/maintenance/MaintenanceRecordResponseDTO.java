package apap.ti._5.vehicle_rental_2306245592_be.restdto.response.maintenance;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MaintenanceRecordResponseDTO {
    private UUID id;
    private UUID vehicleId;
    private String vehicleBrand;
    private String vehicleModel;
    private String vehicleLicensePlate;
    private LocalDateTime serviceDate;
    private String description;
    private BigDecimal cost;
    private String vendorNote;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
