package apap.ti._5.vehicle_rental_2306245592_be.controller;

import apap.ti._5.vehicle_rental_2306245592_be.model.MaintenanceRecord;
import apap.ti._5.vehicle_rental_2306245592_be.restdto.BaseResponseDTO;
import apap.ti._5.vehicle_rental_2306245592_be.restdto.response.maintenance.MaintenanceRecordResponseDTO;
import apap.ti._5.vehicle_rental_2306245592_be.service.AuthService;
import apap.ti._5.vehicle_rental_2306245592_be.service.MaintenanceService;
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
            record.getVehicle().getId() != null ? UUID.fromString(record.getVehicle().getId()) : null,
            record.getVehicle().getBrand(),
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
}
