package apap.ti._5.vehicle_rental_2306245592_be.restdto.response.booking;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchVehiclesResponseDTO {
    private List<AvailableVehicleResponseDTO> availableVehicles;
    private Integer rentalDays;
    private Double driverCostPerDay;
    private Boolean includeDriver;
}
