package apap.ti._5.vehicle_rental_2306245592_be.controller;

import apap.ti._5.vehicle_rental_2306245592_be.model.MaintenanceRecord;
import apap.ti._5.vehicle_rental_2306245592_be.restdto.BaseResponseDTO;
import apap.ti._5.vehicle_rental_2306245592_be.restdto.request.maintenance.CreateMaintenanceRecordRequestDTO;
import apap.ti._5.vehicle_rental_2306245592_be.restdto.request.maintenance.UpdateMaintenanceRecordRequestDTO;
import apap.ti._5.vehicle_rental_2306245592_be.restdto.request.maintenance.UpdateMaintenanceStatusDTO;
import apap.ti._5.vehicle_rental_2306245592_be.restdto.response.maintenance.MaintenanceRecordResponseDTO;
import apap.ti._5.vehicle_rental_2306245592_be.service.AuthService;
import apap.ti._5.vehicle_rental_2306245592_be.service.MaintenanceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/maintenance")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:8080"})
@RequiredArgsConstructor
@Slf4j
public class MaintenanceController {
    
    private final MaintenanceService maintenanceService;
    private final AuthService authService;
    
    /**
     * Mapper method to convert MaintenanceRecord entity to MaintenanceRecordResponseDTO
     */
    private MaintenanceRecordResponseDTO convertToDTO(MaintenanceRecord record) {
        return new MaintenanceRecordResponseDTO(
            record.getId(),
            record.getVehicle().getId(), // <--- Hapus UUID.fromString(), biarkan String            record.getVehicle().getBrand(),
            record.getVehicle().getModel(),
            record.getVehicle().getLicensePlate(),
            record.getServiceDate(),
            record.getDescription(),
            record.getCost(),
            record.getVendorNote(),
            record.getStatus(),
            record.getCreatedAt(),
            record.getUpdatedAt()
        );
    }
    
    /**
     * GET /api/maintenance
     * Retrieve all maintenance records (not soft-deleted)
     * RBAC: Superadmin, Rental Vendor
     */
    @GetMapping
    public ResponseEntity<BaseResponseDTO<List<MaintenanceRecordResponseDTO>>> getAllMaintenanceRecords(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        
        try {
            // Validate Authorization header
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
            
            // RBAC: Only Superadmin and Rental Vendor can access maintenance records
            if (!authService.hasVehicleAccess(token)) {
                log.warn("Unauthorized maintenance access attempt");
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new BaseResponseDTO<>(
                                HttpStatus.FORBIDDEN.value(),
                                "Forbidden: You don't have permission to view maintenance records",
                                new Date(),
                                null
                        ));
            }
            
            List<MaintenanceRecord> records = maintenanceService.getAllMaintenanceRecords();
            
            List<MaintenanceRecordResponseDTO> response = records.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
            
            BaseResponseDTO<List<MaintenanceRecordResponseDTO>> apiResponse = new BaseResponseDTO<>(
                200, 
                "Maintenance records retrieved successfully (Total: " + records.size() + ")", 
                new Date(), 
                response
            );
            return ResponseEntity.ok(apiResponse);
            
        } catch (Exception e) {
            log.error("Error retrieving maintenance records: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponseDTO<>(
                            HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            "Error retrieving maintenance records: " + e.getMessage(),
                            new Date(),
                            null
                    ));
        }
    }
    
    /**
     * GET /api/maintenance/{id}
     * Retrieve a single maintenance record by ID
     * RBAC: Superadmin, Rental Vendor
     */
    @GetMapping("/{id}")
    public ResponseEntity<BaseResponseDTO<MaintenanceRecordResponseDTO>> getMaintenanceRecordById(
            @PathVariable UUID id,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        
        try {
            // Validate Authorization header
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
            
            // RBAC: Only Superadmin and Rental Vendor can access maintenance records
            if (!authService.hasVehicleAccess(token)) {
                log.warn("Unauthorized maintenance access attempt for ID: {}", id);
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new BaseResponseDTO<>(
                                HttpStatus.FORBIDDEN.value(),
                                "Forbidden: You don't have permission to view maintenance record details",
                                new Date(),
                                null
                        ));
            }
            
            Optional<MaintenanceRecord> record = maintenanceService.getMaintenanceRecordById(id);
            
            if (record.isPresent()) {
                MaintenanceRecordResponseDTO dto = convertToDTO(record.get());
                BaseResponseDTO<MaintenanceRecordResponseDTO> response = new BaseResponseDTO<>(
                    200, 
                    "Maintenance record retrieved successfully", 
                    new Date(), 
                    dto
                );
                return ResponseEntity.ok(response);
            }
            
            // Return 404 if not found or soft-deleted
            BaseResponseDTO<MaintenanceRecordResponseDTO> response = new BaseResponseDTO<>(
                404, 
                "Maintenance record not found", 
                new Date(), 
                null
            );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            
        } catch (Exception e) {
            log.error("Error retrieving maintenance record: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponseDTO<>(
                            HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            "Error retrieving maintenance record: " + e.getMessage(),
                            new Date(),
                            null
                    ));
        }
    }
    
    /**
     * POST /api/maintenance
     * Create a new maintenance record
     * RBAC: Superadmin, Rental Vendor
     */
    @PostMapping
    public ResponseEntity<BaseResponseDTO<MaintenanceRecordResponseDTO>> createMaintenanceRecord(
            @Valid @RequestBody CreateMaintenanceRecordRequestDTO request,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        
        try {
            // Validate Authorization header
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
            
            // RBAC: Only Superadmin and Rental Vendor can create maintenance records
            if (!authService.hasVehicleAccess(token)) {
                log.warn("Unauthorized maintenance creation attempt");
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new BaseResponseDTO<>(
                                HttpStatus.FORBIDDEN.value(),
                                "Forbidden: You don't have permission to create maintenance records",
                                new Date(),
                                null
                        ));
            }
            
            // Create maintenance record (includes business validation and vehicle status update)
            MaintenanceRecord createdRecord = maintenanceService.createMaintenanceRecord(request);
            
            MaintenanceRecordResponseDTO dto = convertToDTO(createdRecord);
            
            BaseResponseDTO<MaintenanceRecordResponseDTO> response = new BaseResponseDTO<>(
                201, 
                "Maintenance record created successfully. Vehicle status updated to 'In Maintenance'", 
                new Date(), 
                dto
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (RuntimeException e) {
            log.error("Error creating maintenance record: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new BaseResponseDTO<>(
                            HttpStatus.BAD_REQUEST.value(),
                            e.getMessage(),
                            new Date(),
                            null
                    ));
        } catch (Exception e) {
            log.error("Unexpected error creating maintenance record: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponseDTO<>(
                            HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            "Error creating maintenance record: " + e.getMessage(),
                            new Date(),
                            null
                    ));
        }
    }
    
    /**
     * PUT /api/maintenance/{id}
     * Update maintenance record details
     * RBAC: Superadmin, Rental Vendor
     */
    @PutMapping("/{id}")
    public ResponseEntity<BaseResponseDTO<MaintenanceRecordResponseDTO>> updateMaintenanceRecord(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateMaintenanceRecordRequestDTO request,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        
        try {
            // Validate Authorization header
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
            
            // RBAC: Only Superadmin and Rental Vendor can update maintenance records
            if (!authService.hasVehicleAccess(token)) {
                log.warn("Unauthorized maintenance update attempt for ID: {}", id);
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new BaseResponseDTO<>(
                                HttpStatus.FORBIDDEN.value(),
                                "Forbidden: You don't have permission to update maintenance records",
                                new Date(),
                                null
                        ));
            }
            
            // Update maintenance record
            MaintenanceRecord updatedRecord = maintenanceService.updateMaintenanceRecord(id, request);
            
            MaintenanceRecordResponseDTO dto = convertToDTO(updatedRecord);
            
            BaseResponseDTO<MaintenanceRecordResponseDTO> response = new BaseResponseDTO<>(
                200, 
                "Maintenance record updated successfully", 
                new Date(), 
                dto
            );
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            log.error("Error updating maintenance record: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new BaseResponseDTO<>(
                            HttpStatus.BAD_REQUEST.value(),
                            e.getMessage(),
                            new Date(),
                            null
                    ));
        } catch (Exception e) {
            log.error("Unexpected error updating maintenance record: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponseDTO<>(
                            HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            "Error updating maintenance record: " + e.getMessage(),
                            new Date(),
                            null
                    ));
        }
    }
    
    /**
     * PUT /api/maintenance/{id}/status
     * Update maintenance record status
     * If status is "Completed", automatically set vehicle status to "Available"
     * RBAC: Superadmin, Rental Vendor
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<BaseResponseDTO<MaintenanceRecordResponseDTO>> updateMaintenanceStatus(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateMaintenanceStatusDTO request,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        
        try {
            // Validate Authorization header
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
            
            // RBAC: Only Superadmin and Rental Vendor can update maintenance status
            if (!authService.hasVehicleAccess(token)) {
                log.warn("Unauthorized maintenance status update attempt for ID: {}", id);
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new BaseResponseDTO<>(
                                HttpStatus.FORBIDDEN.value(),
                                "Forbidden: You don't have permission to update maintenance status",
                                new Date(),
                                null
                        ));
            }
            
            // Update maintenance status (includes auto vehicle status update if "Completed")
            MaintenanceRecord updatedRecord = maintenanceService.updateMaintenanceStatus(id, request);
            
            MaintenanceRecordResponseDTO dto = convertToDTO(updatedRecord);
            
            String message = "Completed".equalsIgnoreCase(request.getStatus()) 
                ? "Maintenance record completed. Vehicle status updated to 'Available'"
                : "Maintenance record status updated successfully";
            
            BaseResponseDTO<MaintenanceRecordResponseDTO> response = new BaseResponseDTO<>(
                200, 
                message, 
                new Date(), 
                dto
            );
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            log.error("Error updating maintenance status: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new BaseResponseDTO<>(
                            HttpStatus.BAD_REQUEST.value(),
                            e.getMessage(),
                            new Date(),
                            null
                    ));
        } catch (Exception e) {
            log.error("Unexpected error updating maintenance status: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponseDTO<>(
                            HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            "Error updating maintenance status: " + e.getMessage(),
                            new Date(),
                            null
                    ));
        }
    }
    
    /**
     * DELETE /api/maintenance/{id}
     * Soft delete maintenance record (set deletedAt timestamp)
     * RBAC: Superadmin, Rental Vendor
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponseDTO<Void>> softDeleteMaintenanceRecord(
            @PathVariable UUID id,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        
        try {
            // Validate Authorization header
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
            
            // RBAC: Only Superadmin and Rental Vendor can delete maintenance records
            if (!authService.hasVehicleAccess(token)) {
                log.warn("Unauthorized maintenance deletion attempt for ID: {}", id);
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new BaseResponseDTO<>(
                                HttpStatus.FORBIDDEN.value(),
                                "Forbidden: You don't have permission to delete maintenance records",
                                new Date(),
                                null
                        ));
            }
            
            // Soft delete maintenance record
            maintenanceService.softDeleteMaintenanceRecord(id);
            
            BaseResponseDTO<Void> response = new BaseResponseDTO<>(
                200, 
                "Maintenance record deleted successfully", 
                new Date(), 
                null
            );
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            log.error("Error deleting maintenance record: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new BaseResponseDTO<>(
                            HttpStatus.BAD_REQUEST.value(),
                            e.getMessage(),
                            new Date(),
                            null
                    ));
        } catch (Exception e) {
            log.error("Unexpected error deleting maintenance record: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponseDTO<>(
                            HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            "Error deleting maintenance record: " + e.getMessage(),
                            new Date(),
                            null
                    ));
        }
    }
}
