package apap.ti._5.vehicle_rental_2306245592_be.controller;

import apap.ti._5.vehicle_rental_2306245592_be.model.Vehicle;
import apap.ti._5.vehicle_rental_2306245592_be.model.RentalVendor;
import apap.ti._5.vehicle_rental_2306245592_be.restdto.BaseResponseDTO;
import apap.ti._5.vehicle_rental_2306245592_be.restdto.request.vehicle.CreateVehicleRequestDTO;
import apap.ti._5.vehicle_rental_2306245592_be.restdto.request.vehicle.UpdateVehicleRequestDTO;
import apap.ti._5.vehicle_rental_2306245592_be.restdto.response.auth.AuthUserDTO;
import apap.ti._5.vehicle_rental_2306245592_be.restdto.response.vehicle.VehicleResponseDTO;
import apap.ti._5.vehicle_rental_2306245592_be.restdto.response.vendor.RentalVendorResponseDTO;
import apap.ti._5.vehicle_rental_2306245592_be.service.AuthService;
import apap.ti._5.vehicle_rental_2306245592_be.service.VehicleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:8080"})
@RequiredArgsConstructor
@Slf4j
public class VehicleController {

    private final VehicleService vehicleService;
    private final AuthService authService;

    // ============ GET ALL VEHICLES (PBI-BE-V1) ============
    @GetMapping
    public ResponseEntity<BaseResponseDTO<List<VehicleResponseDTO>>> getAllVehicles(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String keyword) {
        
        try {
            // Validasi Authorization header
            if (authHeader == null || authHeader.trim().isEmpty() || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new BaseResponseDTO<>(
                                HttpStatus.UNAUTHORIZED.value(),
                                "Unauthorized: Missing or invalid Authorization header",
                                new Date(),
                                null
                        ));
            }
            
            String token = authHeader.substring(7);
            
            // Validasi role: Superadmin atau Rental Vendor
            if (!authService.hasVehicleAccess(token)) {
                log.warn("Unauthorized vehicle access attempt");
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new BaseResponseDTO<>(
                                HttpStatus.FORBIDDEN.value(),
                                "Forbidden: Only Superadmin and Rental Vendor can view vehicles",
                                new Date(),
                                null
                        ));
            }
            
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
            
            log.info("Retrieved {} vehicles", vehicleDTOs.size());
            
            BaseResponseDTO<List<VehicleResponseDTO>> response = new BaseResponseDTO<>(
                200, "Vehicles retrieved successfully (Total: " + vehicleDTOs.size() + ")", new Date(), vehicleDTOs
            );
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error retrieving vehicles: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponseDTO<>(
                            HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            "Error retrieving vehicles: " + e.getMessage(),
                            new Date(),
                            null
                    ));
        }
    }

    // ============ GET VEHICLE BY ID (PBI-BE-V2) ============
    @GetMapping("/{id}")
    public ResponseEntity<BaseResponseDTO<VehicleResponseDTO>> getVehicleById(
            @PathVariable String id,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        
        try {
            // Validasi Authorization header
            if (authHeader == null || authHeader.trim().isEmpty() || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new BaseResponseDTO<>(
                                HttpStatus.UNAUTHORIZED.value(),
                                "Unauthorized: Missing or invalid Authorization header",
                                new Date(),
                                null
                        ));
            }
            
            String token = authHeader.substring(7);
            
            // Validasi role
            if (!authService.hasVehicleAccess(token)) {
                log.warn("Unauthorized vehicle access attempt for ID: {}", id);
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new BaseResponseDTO<>(
                                HttpStatus.FORBIDDEN.value(),
                                "Forbidden: Only Superadmin and Rental Vendor can view vehicles",
                                new Date(),
                                null
                        ));
            }
            
            Optional<Vehicle> vehicle = vehicleService.getVehicleById(id);
            
            if (vehicle.isPresent()) {
                VehicleResponseDTO vehicleResponseDTO = mapToVehicleResponseDTO(vehicle.get());
                BaseResponseDTO<VehicleResponseDTO> response = new BaseResponseDTO<>(
                    200, "Vehicle retrieved successfully", new Date(), vehicleResponseDTO
                );
                return ResponseEntity.ok(response);
            }
            
            log.warn("Vehicle not found: {}", id);
            BaseResponseDTO<VehicleResponseDTO> response = new BaseResponseDTO<>(
                404, "Vehicle not found", new Date(), null
            );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (Exception e) {
            log.error("Error retrieving vehicle: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponseDTO<>(
                            HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            "Error retrieving vehicle: " + e.getMessage(),
                            new Date(),
                            null
                    ));
        }
    }

    // ============ CREATE VEHICLE (PBI-BE-V3) ============
    @PostMapping("/create")
    public ResponseEntity<BaseResponseDTO<VehicleResponseDTO>> createVehicle(
            @Valid @RequestBody CreateVehicleRequestDTO createVehicleRequestDTO,
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            BindingResult bindingResult) {

        var baseResponseDTO = new BaseResponseDTO<VehicleResponseDTO>();

        try {
            // Validasi Authorization header
            if (authHeader == null || authHeader.trim().isEmpty() || !authHeader.startsWith("Bearer ")) {
                baseResponseDTO.setStatus(HttpStatus.UNAUTHORIZED.value());
                baseResponseDTO.setMessage("Unauthorized: Missing or invalid Authorization header");
                baseResponseDTO.setTimestamp(new Date());
                return new ResponseEntity<>(baseResponseDTO, HttpStatus.UNAUTHORIZED);
            }
            
            String token = authHeader.substring(7);
            
            // Validasi role
            if (!authService.hasVehicleAccess(token)) {
                log.warn("Unauthorized vehicle creation attempt");
                baseResponseDTO.setStatus(HttpStatus.FORBIDDEN.value());
                baseResponseDTO.setMessage("Forbidden: Only Superadmin and Rental Vendor can create vehicles");
                baseResponseDTO.setTimestamp(new Date());
                return new ResponseEntity<>(baseResponseDTO, HttpStatus.FORBIDDEN);
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

            // Validasi tahun kendaraan (PBI-BE-V3: Tahun tidak boleh lebih besar dari tahun saat ini)
            int currentYear = java.time.Year.now().getValue();
            if (createVehicleRequestDTO.getYear() > currentYear) {
                baseResponseDTO.setStatus(HttpStatus.BAD_REQUEST.value());
                baseResponseDTO.setMessage("Vehicle year cannot be greater than current year (" + currentYear + ")");
                baseResponseDTO.setTimestamp(new Date());
                return new ResponseEntity<>(baseResponseDTO, HttpStatus.BAD_REQUEST);
            }

            VehicleResponseDTO vehicleResponseDTO = vehicleService.createVehicleFromDTO(createVehicleRequestDTO);

            log.info("Vehicle created successfully: {}", vehicleResponseDTO.getId());
            
            baseResponseDTO.setStatus(HttpStatus.CREATED.value());
            baseResponseDTO.setData(vehicleResponseDTO);
            baseResponseDTO.setMessage("Vehicle created successfully");
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.CREATED);
        } catch (RuntimeException ex) {
            log.error("Error creating vehicle: {}", ex.getMessage());
            baseResponseDTO.setStatus(HttpStatus.BAD_REQUEST.value());
            baseResponseDTO.setMessage("Failed to create vehicle: " + ex.getMessage());
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.BAD_REQUEST);
        } catch (Exception ex) {
            log.error("Unexpected error creating vehicle: {}", ex.getMessage());
            baseResponseDTO.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            baseResponseDTO.setMessage("Unexpected error: " + ex.getMessage());
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // ============ UPDATE VEHICLE (PBI-BE-V4 & V5) ============
    @PutMapping("/{id}/update")
    public ResponseEntity<BaseResponseDTO<VehicleResponseDTO>> updateVehicle(
            @PathVariable String id,
            @Valid @RequestBody UpdateVehicleRequestDTO updateVehicleRequestDTO,
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            BindingResult bindingResult) {

        var baseResponseDTO = new BaseResponseDTO<VehicleResponseDTO>();

        try {
            // Validasi Authorization header
            if (authHeader == null || authHeader.trim().isEmpty() || !authHeader.startsWith("Bearer ")) {
                baseResponseDTO.setStatus(HttpStatus.UNAUTHORIZED.value());
                baseResponseDTO.setMessage("Unauthorized: Missing or invalid Authorization header");
                baseResponseDTO.setTimestamp(new Date());
                return new ResponseEntity<>(baseResponseDTO, HttpStatus.UNAUTHORIZED);
            }
            
            String token = authHeader.substring(7);
            
            // Validasi role
            if (!authService.hasVehicleAccess(token)) {
                log.warn("Unauthorized vehicle update attempt for ID: {}", id);
                baseResponseDTO.setStatus(HttpStatus.FORBIDDEN.value());
                baseResponseDTO.setMessage("Forbidden: Only Superadmin and Rental Vendor can update vehicles");
                baseResponseDTO.setTimestamp(new Date());
                return new ResponseEntity<>(baseResponseDTO, HttpStatus.FORBIDDEN);
            }

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

            // Validasi tahun kendaraan
            int currentYear = java.time.Year.now().getValue();
            if (updateVehicleRequestDTO.getYear() > currentYear) {
                baseResponseDTO.setStatus(HttpStatus.BAD_REQUEST.value());
                baseResponseDTO.setMessage("Vehicle year cannot be greater than current year (" + currentYear + ")");
                baseResponseDTO.setTimestamp(new Date());
                return new ResponseEntity<>(baseResponseDTO, HttpStatus.BAD_REQUEST);
            }

            // Cek apakah vehicle sedang disewa (PBI-BE-V4 & V5)
            Optional<Vehicle> existingVehicle = vehicleService.getVehicleById(id);
            if (existingVehicle.isEmpty()) {
                baseResponseDTO.setStatus(HttpStatus.NOT_FOUND.value());
                baseResponseDTO.setMessage("Vehicle not found");
                baseResponseDTO.setTimestamp(new Date());
                return new ResponseEntity<>(baseResponseDTO, HttpStatus.NOT_FOUND);
            }

            Vehicle vehicle = existingVehicle.get();
            // Gagal update jika status bukan Available atau Maintenance
            if (!"Available".equals(vehicle.getStatus()) && !"Maintenance".equals(vehicle.getStatus())) {
                baseResponseDTO.setStatus(HttpStatus.BAD_REQUEST.value());
                baseResponseDTO.setMessage("Cannot update vehicle while it's being rented. Current status: " + vehicle.getStatus());
                baseResponseDTO.setTimestamp(new Date());
                return new ResponseEntity<>(baseResponseDTO, HttpStatus.BAD_REQUEST);
            }

            VehicleResponseDTO vehicleResponseDTO = vehicleService.updateVehicleFromDTO(updateVehicleRequestDTO);

            log.info("Vehicle updated successfully: {}", id);
            
            baseResponseDTO.setStatus(HttpStatus.OK.value());
            baseResponseDTO.setData(vehicleResponseDTO);
            baseResponseDTO.setMessage("Vehicle updated successfully");
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.OK);
        } catch (RuntimeException ex) {
            log.error("Error updating vehicle: {}", ex.getMessage());
            baseResponseDTO.setStatus(HttpStatus.BAD_REQUEST.value());
            baseResponseDTO.setMessage("Failed to update vehicle: " + ex.getMessage());
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.BAD_REQUEST);
        } catch (Exception ex) {
            log.error("Unexpected error updating vehicle: {}", ex.getMessage());
            baseResponseDTO.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            baseResponseDTO.setMessage("Unexpected error: " + ex.getMessage());
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // ============ DELETE VEHICLE (PBI-BE-V6 - Soft Delete) ============
    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponseDTO<Void>> deleteVehicle(
            @PathVariable String id,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        
        try {
            // Validasi Authorization header
            if (authHeader == null || authHeader.trim().isEmpty() || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new BaseResponseDTO<>(
                                HttpStatus.UNAUTHORIZED.value(),
                                "Unauthorized: Missing or invalid Authorization header",
                                new Date(),
                                null
                        ));
            }
            
            String token = authHeader.substring(7);
            
            // Validasi role
            if (!authService.hasVehicleAccess(token)) {
                log.warn("Unauthorized vehicle deletion attempt for ID: {}", id);
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new BaseResponseDTO<>(
                                HttpStatus.FORBIDDEN.value(),
                                "Forbidden: Only Superadmin and Rental Vendor can delete vehicles",
                                new Date(),
                                null
                        ));
            }
            
            vehicleService.deleteVehicle(id);
            
            log.info("Vehicle soft-deleted successfully: {}", id);
            
            BaseResponseDTO<Void> response = new BaseResponseDTO<>(
                200, "Vehicle deleted successfully (soft delete)", new Date(), null
            );
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            log.error("Error deleting vehicle: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new BaseResponseDTO<>(
                            HttpStatus.NOT_FOUND.value(),
                            e.getMessage(),
                            new Date(),
                            null
                    ));
        } catch (Exception e) {
            log.error("Unexpected error deleting vehicle: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponseDTO<>(
                            HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            "Error deleting vehicle: " + e.getMessage(),
                            new Date(),
                            null
                    ));
        }
    }

    // ============ GET VEHICLE FOR UPDATE ============
    @GetMapping("/{id}/update")
    public ResponseEntity<BaseResponseDTO<VehicleResponseDTO>> getVehicleForUpdate(
            @PathVariable String id,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        
        try {
            // Validasi Authorization header
            if (authHeader == null || authHeader.trim().isEmpty() || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new BaseResponseDTO<>(
                                HttpStatus.UNAUTHORIZED.value(),
                                "Unauthorized: Missing or invalid Authorization header",
                                new Date(),
                                null
                        ));
            }
            
            String token = authHeader.substring(7);
            
            // Validasi role
            if (!authService.hasVehicleAccess(token)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new BaseResponseDTO<>(
                                HttpStatus.FORBIDDEN.value(),
                                "Forbidden: Only Superadmin and Rental Vendor can access vehicles",
                                new Date(),
                                null
                        ));
            }
            
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
        } catch (Exception e) {
            log.error("Error retrieving vehicle for update: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponseDTO<>(
                            HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            "Error retrieving vehicle: " + e.getMessage(),
                            new Date(),
                            null
                    ));
        }
    }

    @GetMapping("/count")
    public ResponseEntity<BaseResponseDTO<Integer>> getVehicleCount() {
        int count = vehicleService.getVehicleCount();
        
        BaseResponseDTO<Integer> response = new BaseResponseDTO<>(
            200, "Vehicle count retrieved successfully", new Date(), count
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/vendor/count")
    public ResponseEntity<BaseResponseDTO<Integer>> getVendorCount() {
        int count = vehicleService.getAllVendors().size();
        
        BaseResponseDTO<Integer> response = new BaseResponseDTO<>(
            200, "Vendor count retrieved successfully", new Date(), count
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/vendors")
    public ResponseEntity<BaseResponseDTO<List<RentalVendorResponseDTO>>> getAllVendors() {
        List<RentalVendor> vendors = vehicleService.getAllVendors();
        
        List<RentalVendorResponseDTO> vendorDTOs = vendors.stream()
                .map(this::mapToRentalVendorResponseDTO)
                .toList();
        
        BaseResponseDTO<List<RentalVendorResponseDTO>> response = new BaseResponseDTO<>(
            200, "Vendors retrieved successfully", new Date(), vendorDTOs
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

    private RentalVendorResponseDTO mapToRentalVendorResponseDTO(RentalVendor vendor) {
        if (vendor == null) {
            return null;
        }

        return RentalVendorResponseDTO.builder()
                .id(vendor.getId())
                .name(vendor.getName())
                .email(vendor.getEmail())
                .phone(vendor.getPhone())
                .listOfLocations(vendor.getListOfLocations())
                .createdAt(vendor.getCreatedAt())
                .updatedAt(vendor.getUpdatedAt())
                .build();
    }
    
    // ============ SSO INTEGRATION - GET OR CREATE VENDOR ============
    @GetMapping("/vendor/me")
    public ResponseEntity<BaseResponseDTO<RentalVendorResponseDTO>> getOrCreateVendor(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        
        try {
            // Validasi Authorization header
            if (authHeader == null || authHeader.trim().isEmpty() || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new BaseResponseDTO<>(
                                HttpStatus.UNAUTHORIZED.value(),
                                "Unauthorized: Missing or invalid Authorization header",
                                new Date(),
                                null
                        ));
            }
            
            String token = authHeader.substring(7);
            
            // Get or create vendor
            RentalVendor vendor = vehicleService.getOrCreateVendor(token);
            
            // Map to DTO
            RentalVendorResponseDTO vendorDTO = mapToRentalVendorResponseDTO(vendor);
            
            BaseResponseDTO<RentalVendorResponseDTO> response = new BaseResponseDTO<>(
                200, 
                "Vendor information retrieved successfully", 
                new Date(), 
                vendorDTO
            );
            
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            log.error("Error getting/creating vendor: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new BaseResponseDTO<>(
                            HttpStatus.FORBIDDEN.value(),
                            e.getMessage(),
                            new Date(),
                            null
                    ));
        } catch (Exception e) {
            log.error("Unexpected error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponseDTO<>(
                            HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            "Internal server error: " + e.getMessage(),
                            new Date(),
                            null
                    ));
        }
    }
}
