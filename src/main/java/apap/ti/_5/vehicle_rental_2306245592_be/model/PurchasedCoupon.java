package apap.ti._5.vehicle_rental_2306245592_be.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "purchased_coupon")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PurchasedCoupon {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_loyalty_id", nullable = false)
    private CustomerLoyalty customerLoyalty;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_id", nullable = false)
    private Coupon coupon;
    
    @NotNull
    @Column(name = "purchased_at", nullable = false)
    private LocalDateTime purchasedAt;
}
