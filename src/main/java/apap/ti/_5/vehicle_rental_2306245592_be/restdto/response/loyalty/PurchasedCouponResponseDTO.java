package apap.ti._5.vehicle_rental_2306245592_be.restdto.response.loyalty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PurchasedCouponResponseDTO {
    private String id;
    private String customerId;
    private String couponId;
    private String couponName;
    private String couponCode;
    private Integer percentOff;
    private Boolean isUsed;
    private LocalDateTime purchasedAt;
    private Integer remainingPoints;
}