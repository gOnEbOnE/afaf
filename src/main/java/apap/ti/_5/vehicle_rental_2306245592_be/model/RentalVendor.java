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
@Table(name = "rental_vendor")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RentalVendor {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false)
    private String email;
    
    @Column(nullable = false)
    private String phone;
    
    @ElementCollection
    @CollectionTable(name = "vendor_locations", joinColumns = @JoinColumn(name = "vendor_id"))
    @Column(name = "location")
    private List<String> listOfLocations;
    
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    
    @OneToMany(mappedBy = "rentalVendor", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Vehicle> listOfVehicles;
}