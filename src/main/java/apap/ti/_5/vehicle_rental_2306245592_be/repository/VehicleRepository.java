package apap.ti._5.vehicle_rental_2306245592_be.repository;

import apap.ti._5.vehicle_rental_2306245592_be.model.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, String> {
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
}