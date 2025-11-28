package apap.ti._5.vehicle_rental_2306245592_be.repository;

import apap.ti._5.vehicle_rental_2306245592_be.model.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, String> {
    
    // ============ EXISTING METHODS (KEEP) ============
    // ✅ Default queries sudah include soft delete filter otomatis
    List<Vehicle> findByType(String type);
    List<Vehicle> findByBrandContainingOrModelContaining(String brand, String model);
    Optional<Vehicle> findByLicensePlate(String licensePlate);
    
    // ✅ Query untuk mencari deleted vehicles jika diperlukan
    @Query("SELECT v FROM Vehicle v WHERE v.deletedAt IS NOT NULL")
    List<Vehicle> findDeletedVehicles();
    
    // ✅ Query untuk restore vehicle
    @Query("SELECT v FROM Vehicle v WHERE v.id = :id AND v.deletedAt IS NOT NULL")
    Optional<Vehicle> findDeletedVehicleById(@Param("id") String id);

    // ✅ Query untuk booking search (existing - used by BookingService)
    @Query("SELECT v FROM Vehicle v WHERE v.transmission = :transmission AND v.capacity >= :capacity AND v.status = 'Available'")
    List<Vehicle> findAvailableVehicles(
        @Param("transmission") String transmission,
        @Param("capacity") Integer capacity,
        @Param("pickUpTime") LocalDateTime pickUpTime,
        @Param("dropOffTime") LocalDateTime dropOffTime
    );
    
    // ✅ Alternative: Get all available vehicles (for testing - existing)
    @Query("SELECT v FROM Vehicle v WHERE v.status = 'Available'")
    List<Vehicle> findAllAvailable();
    
    // ============ NEW METHODS FOR RBAC & SOFT DELETE ============
    
    // PBI-BE-V1: Get all vehicles NOT deleted
    @Query("SELECT v FROM Vehicle v WHERE v.deletedAt IS NULL")
    List<Vehicle> findAllNotDeleted();
    
    // PBI-BE-V2: Get vehicle by ID NOT deleted
    @Query("SELECT v FROM Vehicle v WHERE v.id = :id AND v.deletedAt IS NULL")
    Optional<Vehicle> findByIdNotDeleted(@Param("id") String id);
    
    // Filter by type NOT deleted (untuk VehicleService.filterVehiclesByType)
    @Query("SELECT v FROM Vehicle v WHERE v.type = :type AND v.deletedAt IS NULL")
    List<Vehicle> findByTypeNotDeleted(@Param("type") String type);
    
    // Search by brand or model NOT deleted (untuk VehicleService.searchVehicles)
    @Query("SELECT v FROM Vehicle v WHERE (LOWER(v.brand) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(v.model) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND v.deletedAt IS NULL")
    List<Vehicle> findByBrandOrModelContainingNotDeleted(@Param("keyword") String keyword);
    
    // PBI-BE-V3: Check unique license plate (exclude soft deleted)
    @Query("SELECT v FROM Vehicle v WHERE v.licensePlate = :licensePlate AND v.deletedAt IS NULL")
    Optional<Vehicle> findByLicensePlateNotDeleted(@Param("licensePlate") String licensePlate);
    
    // Get all available vehicles NOT deleted (enhanced version)
    @Query("SELECT v FROM Vehicle v WHERE v.status = 'Available' AND v.deletedAt IS NULL")
    List<Vehicle> findAllAvailableNotDeleted();
}