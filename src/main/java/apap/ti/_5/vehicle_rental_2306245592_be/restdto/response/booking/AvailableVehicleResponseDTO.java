package apap.ti._5.vehicle_rental_2306245592_be.restdto.response.booking;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AvailableVehicleResponseDTO {
    private String id;
    private String type;
    private String brand;
    private String model;
    private String transmission;
    private String fuelType;
    private Integer capacity;
    private Double pricePerDay;
    private Double totalPrice;
    private Integer rentalDays;
}