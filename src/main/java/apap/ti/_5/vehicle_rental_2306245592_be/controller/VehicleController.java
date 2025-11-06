package apap.ti._5.vehicle_rental_2306245592_be.controller;

import apap.ti._5.vehicle_rental_2306245592_be.model.Vehicle;
import apap.ti._5.vehicle_rental_2306245592_be.model.RentalVendor;
import apap.ti._5.vehicle_rental_2306245592_be.restdto.BaseResponseDTO;
import apap.ti._5.vehicle_rental_2306245592_be.restdto.request.vehicle.CreateVehicleRequestDTO;
import apap.ti._5.vehicle_rental_2306245592_be.restdto.request.vehicle.UpdateVehicleRequestDTO;
import apap.ti._5.vehicle_rental_2306245592_be.restdto.response.vehicle.VehicleResponseDTO;
import apap.ti._5.vehicle_rental_2306245592_be.service.VehicleService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
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
    public ResponseEntity<BaseResponseDTO<List<VehicleResponseDTO>>> getAllVehicles(
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
        
        // Convert to DTO
        List<VehicleResponseDTO> vehicleDTOs = vehicles.stream()
                .map(this::mapToVehicleResponseDTO)
                .toList();
        
        BaseResponseDTO<List<VehicleResponseDTO>> response = new BaseResponseDTO<>(
            200, "Success", new Date(), vehicleDTOs
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponseDTO<VehicleResponseDTO>> getVehicleById(@PathVariable String id) {
        Optional<Vehicle> vehicle = vehicleService.getVehicleById(id);
        
        if (vehicle.isPresent()) {
            VehicleResponseDTO vehicleResponseDTO = mapToVehicleResponseDTO(vehicle.get());
            BaseResponseDTO<VehicleResponseDTO> response = new BaseResponseDTO<>(
                200, "Success", new Date(), vehicleResponseDTO
            );
            return ResponseEntity.ok(response);
        }
        
        BaseResponseDTO<VehicleResponseDTO> response = new BaseResponseDTO<>(
            404, "Vehicle not found", new Date(), null
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @PostMapping("/create")
    public ResponseEntity<BaseResponseDTO<VehicleResponseDTO>> createVehicle(
            @Valid @RequestBody CreateVehicleRequestDTO createVehicleRequestDTO,
            BindingResult bindingResult) {

        var baseResponseDTO = new BaseResponseDTO<VehicleResponseDTO>();

        if (bindingResult.hasFieldErrors()) {
            StringBuilder errorMessages = new StringBuilder();
            List<FieldError> errors = bindingResult.getFieldErrors();
            for (FieldError error : errors) {
                errorMessages.append(error.getDefaultMessage()).append("; ");
            }

            baseResponseDTO.setStatus(HttpStatus.BAD_REQUEST.value());
            baseResponseDTO.setMessage(errorMessages.toString());
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.BAD_REQUEST);
        }

        try {
            VehicleResponseDTO vehicleResponseDTO = vehicleService.createVehicleFromDTO(createVehicleRequestDTO);

            baseResponseDTO.setStatus(HttpStatus.CREATED.value());
            baseResponseDTO.setData(vehicleResponseDTO);
            baseResponseDTO.setMessage("Vehicle created successfully");
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.CREATED);
        } catch (RuntimeException ex) {
            baseResponseDTO.setStatus(HttpStatus.BAD_REQUEST.value());
            baseResponseDTO.setMessage("Failed to create vehicle: " + ex.getMessage());
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{id}/update")
    public ResponseEntity<BaseResponseDTO<VehicleResponseDTO>> getVehicleForUpdate(@PathVariable String id) {
        Optional<Vehicle> vehicle = vehicleService.getVehicleById(id);
        
        if (vehicle.isPresent()) {
            VehicleResponseDTO vehicleResponseDTO = mapToVehicleResponseDTO(vehicle.get());
            BaseResponseDTO<VehicleResponseDTO> response = new BaseResponseDTO<>(
                200, "Vehicle retrieved successfully for update", new Date(), vehicleResponseDTO
            );
            return ResponseEntity.ok(response);
        }
        
        BaseResponseDTO<VehicleResponseDTO> response = new BaseResponseDTO<>(
            404, "Vehicle not found", new Date(), null
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @PutMapping("/{id}/update")
    public ResponseEntity<BaseResponseDTO<VehicleResponseDTO>> updateVehicle(
            @PathVariable String id,
            @Valid @RequestBody UpdateVehicleRequestDTO updateVehicleRequestDTO,
            BindingResult bindingResult) {

        var baseResponseDTO = new BaseResponseDTO<VehicleResponseDTO>();

        // Validate path variable matches request body
        if (!id.equals(updateVehicleRequestDTO.getId())) {
            baseResponseDTO.setStatus(HttpStatus.BAD_REQUEST.value());
            baseResponseDTO.setMessage("Vehicle ID in path does not match request body");
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.BAD_REQUEST);
        }

        if (bindingResult.hasFieldErrors()) {
            StringBuilder errorMessages = new StringBuilder();
            List<FieldError> errors = bindingResult.getFieldErrors();
            for (FieldError error : errors) {
                errorMessages.append(error.getDefaultMessage()).append("; ");
            }

            baseResponseDTO.setStatus(HttpStatus.BAD_REQUEST.value());
            baseResponseDTO.setMessage(errorMessages.toString());
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.BAD_REQUEST);
        }

        try {
            VehicleResponseDTO vehicleResponseDTO = vehicleService.updateVehicleFromDTO(updateVehicleRequestDTO);

            baseResponseDTO.setStatus(HttpStatus.OK.value());
            baseResponseDTO.setData(vehicleResponseDTO);
            baseResponseDTO.setMessage("Vehicle updated successfully");
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.OK);
        } catch (RuntimeException ex) {
            baseResponseDTO.setStatus(HttpStatus.BAD_REQUEST.value());
            baseResponseDTO.setMessage("Failed to update vehicle: " + ex.getMessage());
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/count")
    public ResponseEntity<BaseResponseDTO<Integer>> getVehicleCount() {
        int count = vehicleService.getVehicleCount();
        
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

    @GetMapping("/vendors")
    public ResponseEntity<BaseResponseDTO<List<RentalVendor>>> getAllVendors() {
        List<RentalVendor> vendors = vehicleService.getAllVendors();
        
        BaseResponseDTO<List<RentalVendor>> response = new BaseResponseDTO<>(
            200, "Vendors retrieved successfully", new Date(), vendors
        );
        return ResponseEntity.ok(response);
    }

    private VehicleResponseDTO mapToVehicleResponseDTO(Vehicle vehicle) {
        if (vehicle == null) {
            return null;
        }

        String vendorName = "";
        if (vehicle.getRentalVendor() != null) {
            vendorName = vehicle.getRentalVendor().getName();
        }

        return VehicleResponseDTO.builder()
                .id(vehicle.getId())
                .rentalVendorId(vehicle.getRentalVendor() != null ? vehicle.getRentalVendor().getId() : null)
                .rentalVendorName(vendorName)
                .type(vehicle.getType())
                .brand(vehicle.getBrand())
                .model(vehicle.getModel())
                .year(vehicle.getYear())
                .location(vehicle.getLocation())
                .licensePlate(vehicle.getLicensePlate())
                .capacity(vehicle.getCapacity())
                .transmission(vehicle.getTransmission())
                .fuelType(vehicle.getFuelType())
                .price(vehicle.getPrice())
                .status(vehicle.getStatus())
                .createdAt(vehicle.getCreatedAt())
                .updatedAt(vehicle.getUpdatedAt())
                .build();
    }
}
