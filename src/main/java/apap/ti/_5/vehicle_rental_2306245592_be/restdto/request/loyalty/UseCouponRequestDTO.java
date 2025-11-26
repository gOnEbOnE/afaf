package apap.ti._5.vehicle_rental_2306245592_be.restdto.request.loyalty;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UseCouponRequestDTO {
    @NotNull(message = "Customer ID is required")
    private String customerId;
    
    @NotNull(message = "Coupon code is required")
    private String couponCode;
}