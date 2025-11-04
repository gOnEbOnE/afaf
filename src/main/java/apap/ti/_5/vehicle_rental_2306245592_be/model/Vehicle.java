package apap.ti._5.vehicle_rental_2306245592_be.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "vehicle")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Vehicle {
    
    @Id
    @Column(nullable = false, unique = true)
    private String id;
    
    @ManyToOne(fetch = FetchType.LAZY)
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
    
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}