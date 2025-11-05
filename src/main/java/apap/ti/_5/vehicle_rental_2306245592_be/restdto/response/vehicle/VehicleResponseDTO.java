package apap.ti._5.vehicle_rental_2306245592_be.restdto.response.vehicle;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehicleResponseDTO {
    private String id;
    private Integer rentalVendorId;
    private String rentalVendorName;
    private String type;
    private String brand;
    private String model;
    private Integer year;
    private String location;
    private String licensePlate;
    private Integer capacity;
    private String transmission;
    private String fuelType;
    private Double price;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
