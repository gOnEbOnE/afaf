package apap.ti._5.vehicle_rental_2306245592_be.restdto.response.RentalAddOn;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RentalAddOnResponseDTO {
    private UUID id;
    private String name;
    private Double price;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}