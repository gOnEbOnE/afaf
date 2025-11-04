package apap.ti._5.vehicle_rental_2306245592_be.controller;

import apap.ti._5.vehicle_rental_2306245592_be.model.Vehicle;
import apap.ti._5.vehicle_rental_2306245592_be.restdto.BaseResponseDTO;
import apap.ti._5.vehicle_rental_2306245592_be.service.VehicleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/vehicles")
public class VehicleController {

    private final VehicleService vehicleService;

    public VehicleController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    @GetMapping
    public ResponseEntity<BaseResponseDTO<List<Vehicle>>> getAllVehicles(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String keyword) {
        
        List<Vehicle> vehicles;
        
        if (type != null && !type.isEmpty()) {
            vehicles = vehicleService.filterVehiclesByType(type);
        } else if (keyword != null && !keyword.isEmpty()) {
            vehicles = vehicleService.searchVehicles(keyword);
        } else {
            vehicles = vehicleService.getAllVehicles();
        }
        
        BaseResponseDTO<List<Vehicle>> response = new BaseResponseDTO<>(
            200, "Success", new Date(), vehicles
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponseDTO<Vehicle>> getVehicleById(@PathVariable String id) {
        Optional<Vehicle> vehicle = vehicleService.getVehicleById(id);
        
        if (vehicle.isPresent()) {
            BaseResponseDTO<Vehicle> response = new BaseResponseDTO<>(
                200, "Success", new Date(), vehicle.get()
            );
            return ResponseEntity.ok(response);
        }
        
        BaseResponseDTO<Vehicle> response = new BaseResponseDTO<>(
            404, "Vehicle not found", new Date(), null
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @GetMapping("/count")
    public ResponseEntity<BaseResponseDTO<Integer>> getVehicleCount() {
        int count = vehicleService.getAllVehicles().size();
        
        BaseResponseDTO<Integer> response = new BaseResponseDTO<>(
            200, "Vehicles retrieved successfully", new Date(), count
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/vendor/count")
    public ResponseEntity<BaseResponseDTO<Integer>> getVendorCount() {
        int count = vehicleService.getAllVendors().size();
        
        BaseResponseDTO<Integer> response = new BaseResponseDTO<>(
            200, "Vendors retrieved successfully", new Date(), count
        );
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<BaseResponseDTO<Vehicle>> createVehicle(@RequestBody Vehicle vehicle) {
        Vehicle createdVehicle = vehicleService.createVehicle(vehicle);
        BaseResponseDTO<Vehicle> response = new BaseResponseDTO<>(
            201, "Vehicle created successfully", new Date(), createdVehicle
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BaseResponseDTO<Vehicle>> updateVehicle(
            @PathVariable String id, 
            @RequestBody Vehicle vehicle) {
        try {
            Vehicle updatedVehicle = vehicleService.updateVehicle(id, vehicle);
            BaseResponseDTO<Vehicle> response = new BaseResponseDTO<>(
                200, "Vehicle updated successfully", new Date(), updatedVehicle
            );
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            BaseResponseDTO<Vehicle> response = new BaseResponseDTO<>(
                404, e.getMessage(), new Date(), null
            );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponseDTO<Void>> deleteVehicle(@PathVariable String id) {
        try {
            vehicleService.deleteVehicle(id);
            BaseResponseDTO<Void> response = new BaseResponseDTO<>(
                200, "Vehicle deleted successfully", new Date(), null
            );
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            BaseResponseDTO<Void> response = new BaseResponseDTO<>(
                404, e.getMessage(), new Date(), null
            );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
}
