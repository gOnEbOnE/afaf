package apap.ti._5.vehicle_rental_2306245592_be.restdto.request.booking;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddAddOnsRequestDTO {
    private String vehicleId;
    private List<UUID> selectedAddOnIds;
}
