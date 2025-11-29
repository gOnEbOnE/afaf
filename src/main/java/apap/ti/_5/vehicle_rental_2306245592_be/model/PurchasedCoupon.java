package apap.ti._5.vehicle_rental_2306245592_be.model;

import jakarta.persistence.*;
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
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    
    @Column(nullable = false)
    private String customerId;
    
    @ManyToOne
    @JoinColumn(name = "coupon_id", nullable = false)
    private Coupon coupon;
    
    @Column(nullable = false, unique = true)
    private String couponCode;
    
    @Column(nullable = false)
    private Boolean isUsed = false;
    
    @Column(nullable = false)
    private LocalDateTime purchasedAt = LocalDateTime.now();
    
    @Column
    private LocalDateTime usedAt;
}
