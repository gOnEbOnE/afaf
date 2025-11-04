package apap.ti._5.vehicle_rental_2306245592_be.service;

import apap.ti._5.vehicle_rental_2306245592_be.model.RentalVendor;
import apap.ti._5.vehicle_rental_2306245592_be.model.Vehicle;
import java.util.List;
import java.util.Optional;

public interface VehicleService {
    List<Vehicle> getAllVehicles();
    Optional<Vehicle> getVehicleById(String id);
    Vehicle createVehicle(Vehicle vehicle);
    Vehicle updateVehicle(String id, Vehicle vehicle);
    void deleteVehicle(String id);
    List<Vehicle> searchVehicles(String keyword);
    List<Vehicle> filterVehiclesByType(String type);
    List<RentalVendor> getAllVendors();
}
