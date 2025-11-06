package apap.ti._5.vehicle_rental_2306245592_be.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "vehicle")
@Where(clause = "deleted_at IS NULL")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Vehicle {
    
    @Id
    @Column(nullable = false, unique = true)
    private String id;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "rental_vendor_id", nullable = false)
    private RentalVendor rentalVendor;
    
    @Column(nullable = false)
    private String type;
    
    @Column(nullable = false)
    private String brand;
    
    @Column(nullable = false)
    private String model;
    
    @Column(nullable = false)
    private Integer year;
    
    @Column(nullable = false)
    private String location;
    
    @Column(nullable = false)
    private String licensePlate;
    
    @Column(nullable = false)
    private Integer capacity;
    
    @Column(nullable = false)
    private String transmission;
    
    @Column(nullable = false)
    private String fuelType;
    
    @Column(nullable = false)
    private Double price;
    
    @Column(nullable = false)
    private String status;
    
    @OneToMany(mappedBy = "vehicle", fetch = FetchType.LAZY)
    @JsonBackReference
    private List<RentalBooking> listOfBookings;
    
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
}