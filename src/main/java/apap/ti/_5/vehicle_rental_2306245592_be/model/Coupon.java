package apap.ti._5.vehicle_rental_2306245592_be.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "coupon")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Coupon {
    
    @Id
    @Column(name = "id", nullable = false)
    private String id;
    
    @NotBlank
    @Column(name = "name", nullable = false)
    private String name;
    
    @NotBlank
    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;
    
    @NotNull
    @Column(name = "points", nullable = false)
    private Integer points;
    
    @NotNull
    @Column(name = "percent_off", nullable = false)
    private Integer percentOff;
    
    @OneToMany(mappedBy = "coupon", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PurchasedCoupon> purchasedCoupons;
}
