package apap.ti._5.vehicle_rental_2306245592_be.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SoftDelete;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "vehicle")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SoftDelete // ✅ Hibernate 6.2+ soft delete
public class Vehicle {
    
    @Id
    @Column(nullable = false, unique = true)
    private String id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rental_vendor_id", nullable = false)
    @JsonBackReference
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
    
    // ✅ Soft delete column (Hibernate akan mengelola otomatis)
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
}