package apap.ti._5.vehicle_rental_2306245592_be.restdto.request.vehicle;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CreateVehicleRequestDTO {
    
    @NotNull(message = "Rental Vendor ID is required")
    private Integer rentalVendorId;
    
    @NotBlank(message = "Vehicle type is required")
    private String type; // Sedan, SUV, MPV, Luxury
    
    @NotBlank(message = "Brand is required")
    private String brand;
    
    @NotBlank(message = "Model is required")
    private String model;
    
    @NotNull(message = "Year is required")
    @Min(value = 1900, message = "Year must be valid")
    private Integer year;
    
    @NotBlank(message = "Location is required")
    private String location;
    
    @NotBlank(message = "License plate is required")
    private String licensePlate;
    
    @NotNull(message = "Capacity is required")
    @Min(value = 1, message = "Capacity must be at least 1")
    private Integer capacity;
    
    @NotBlank(message = "Transmission is required")
    private String transmission; // Manual, Automatic
    
    @NotBlank(message = "Fuel type is required")
    private String fuelType; // Bensin, Diesel, Listrik, Hybrid
    
    @NotNull(message = "Price is required")
    @Min(value = 0, message = "Price must be greater than 0")
    private Double price;
}
