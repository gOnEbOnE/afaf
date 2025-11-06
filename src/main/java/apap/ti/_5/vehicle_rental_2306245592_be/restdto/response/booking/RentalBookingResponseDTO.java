package apap.ti._5.vehicle_rental_2306245592_be.restdto.response.booking;

import apap.ti._5.vehicle_rental_2306245592_be.restdto.response.RentalAddOn.RentalAddOnResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RentalBookingResponseDTO {
    private String id;
    private String vehicleId;
    private String vehicleBrand;
    private String vehicleModel;
    private LocalDateTime pickUpTime;
    private LocalDateTime dropOffTime;
    private String pickUpLocation;
    private String dropOffLocation;
    private Integer capacityNeeded;
    private String transmissionNeeded;
    private Double totalPrice;
    private Boolean includeDriver;
    private String status;
    private List<RentalAddOnResponseDTO> listOfAddOns;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
