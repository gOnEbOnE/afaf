package apap.ti._5.vehicle_rental_2306245592_be.restdto.response.vendor;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RentalVendorResponseDTO {
    private Integer id;
    private String name;
    private String email;
    private String phone;
    private List<String> listOfLocations;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
