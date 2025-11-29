package apap.ti._5.vehicle_rental_2306245592_be.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "customer_loyalty")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CustomerLoyalty {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    
    @NotNull
    @Column(name = "customer_id", nullable = false, unique = true)
    private String customerId;
    
    @NotNull
    @Column(name = "loyalty_points", nullable = false)
    private Integer loyaltyPoints = 0;
    
    @OneToMany(mappedBy = "customerLoyalty", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PurchasedCoupon> purchasedCoupons;
}
