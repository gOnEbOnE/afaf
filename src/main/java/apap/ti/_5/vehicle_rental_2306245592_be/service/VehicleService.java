package apap.ti._5.vehicle_rental_2306245592_be.service;

import apap.ti._5.vehicle_rental_2306245592_be.model.RentalVendor;
import apap.ti._5.vehicle_rental_2306245592_be.model.Vehicle;
import apap.ti._5.vehicle_rental_2306245592_be.restdto.request.vehicle.CreateVehicleRequestDTO;
import apap.ti._5.vehicle_rental_2306245592_be.restdto.request.vehicle.UpdateVehicleRequestDTO;
import apap.ti._5.vehicle_rental_2306245592_be.restdto.response.vehicle.VehicleResponseDTO;

import java.util.List;
import java.util.Optional;

public interface VehicleService {
    
    // ============ EXISTING METHODS (KEEP) ============
    List<Vehicle> getAllVehicles();
    Optional<Vehicle> getVehicleById(String id);
    Vehicle createVehicle(Vehicle vehicle);
    Vehicle updateVehicle(String id, Vehicle vehicle);
    void deleteVehicle(String id);
    List<Vehicle> filterVehiclesByType(String type);
    List<Vehicle> searchVehicles(String keyword);
    int getVehicleCount();
    List<RentalVendor> getAllVendors();
    VehicleResponseDTO createVehicleFromDTO(CreateVehicleRequestDTO dto);
    VehicleResponseDTO updateVehicleFromDTO(UpdateVehicleRequestDTO dto);
    
    // ============ NEW METHODS FOR SOFT DELETE SUPPORT ============
    
    // PBI-BE-V1: Get all vehicles NOT deleted
    List<Vehicle> getAllVehiclesNotDeleted();
    
    // PBI-BE-V2: Get vehicle by ID NOT deleted
    Optional<Vehicle> getVehicleByIdNotDeleted(String id);
    
    // Soft delete vehicle (set deletedAt timestamp)
    void softDeleteVehicle(String id);
    
    // Check if vehicle can be updated (not being rented)
    boolean canUpdateVehicle(String id);
    String generateVehicleId();
}
