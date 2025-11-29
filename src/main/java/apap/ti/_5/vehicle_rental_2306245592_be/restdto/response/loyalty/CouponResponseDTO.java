package apap.ti._5.vehicle_rental_2306245592_be.restdto.response.loyalty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CouponResponseDTO {
    private String id;
    private String name;
    private String description;
    private Integer points;
    private Integer percentOff;
}
