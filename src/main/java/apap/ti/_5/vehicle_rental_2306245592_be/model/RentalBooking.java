package apap.ti._5.vehicle_rental_2306245592_be.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "rental_booking")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RentalBooking {
    
    @Id
    @Column(nullable = false, unique = true)
    private String id;
    
    @Column(nullable = false)
    private String vehicleId;
    
    @Column(nullable = false)
    private LocalDateTime pickUpTime;
    
    @Column(nullable = false)
    private LocalDateTime dropOffTime;
    
    @Column(nullable = false)
    private String pickUpLocation;
    
    @Column(nullable = false)
    private String dropOffLocation;
    
    @Column(nullable = false)
    private Integer capacityNeeded;
    
    @Column(nullable = false)
    private String transmissionNeeded;
    
    @Column(nullable = false)
    private Double totalPrice;
    
    @Column(nullable = false)
    private Boolean includeDriver;
    
    @Column(nullable = false)
    private String status;
    
    @ManyToMany
    @JoinTable(
        name = "booking_addons",
        joinColumns = @JoinColumn(name = "booking_id"),
        inverseJoinColumns = @JoinColumn(name = "addon_id")
    )
    private List<RentalAddOn> listOfAddOns;
    
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}