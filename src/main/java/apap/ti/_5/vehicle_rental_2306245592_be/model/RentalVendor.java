package apap.ti._5.vehicle_rental_2306245592_be.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
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
    
    @OneToMany(mappedBy = "rentalVendor", fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<Vehicle> listOfVehicles;
    
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}