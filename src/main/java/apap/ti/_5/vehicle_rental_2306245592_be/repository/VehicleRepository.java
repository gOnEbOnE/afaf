package apap.ti._5.vehicle_rental_2306245592_be.repository;

import apap.ti._5.vehicle_rental_2306245592_be.model.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, String> {
    List<Vehicle> findByBrandContainingOrModelContaining(String brand, String model);
    List<Vehicle> findByType(String type);
}   