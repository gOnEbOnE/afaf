package apap.ti._5.vehicle_rental_2306245592_be.service;

import apap.ti._5.vehicle_rental_2306245592_be.model.MaintenanceRecord;
import apap.ti._5.vehicle_rental_2306245592_be.model.Vehicle;
import apap.ti._5.vehicle_rental_2306245592_be.repository.MaintenanceRecordRepository;
import apap.ti._5.vehicle_rental_2306245592_be.repository.VehicleRepository;
import apap.ti._5.vehicle_rental_2306245592_be.restdto.request.maintenance.CreateMaintenanceRecordRequestDTO;
import apap.ti._5.vehicle_rental_2306245592_be.restdto.request.maintenance.UpdateMaintenanceRecordRequestDTO;
import apap.ti._5.vehicle_rental_2306245592_be.restdto.request.maintenance.UpdateMaintenanceStatusDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class MaintenanceServiceImpl implements MaintenanceService {
    
    private final MaintenanceRecordRepository maintenanceRecordRepository;
    private final VehicleRepository vehicleRepository;
    
    @Override
    public List<MaintenanceRecord> getAllMaintenanceRecords() {
        log.info("Fetching all maintenance records (not deleted)");
        return maintenanceRecordRepository.findAllNotDeleted();
    }
    
    @Override
    public Optional<MaintenanceRecord> getMaintenanceRecordById(UUID id) {
        log.info("Fetching maintenance record by ID: {}", id);
        return maintenanceRecordRepository.findByIdNotDeleted(id);
    }
    
    @Override
    @Transactional
    public MaintenanceRecord createMaintenanceRecord(CreateMaintenanceRecordRequestDTO request) {
        log.info("Creating maintenance record for vehicle ID: {}", request.getVehicleId());
        
        // 1. Validate vehicle exists and not soft-deleted
        Optional<Vehicle> vehicleOpt = vehicleRepository.findById(request.getVehicleId());
        if (vehicleOpt.isEmpty() || vehicleOpt.get().getDeletedAt() != null) {
            log.error("Vehicle not found or has been deleted: {}", request.getVehicleId());
            throw new RuntimeException("Vehicle not found or has been deleted");
        }
        
        Vehicle vehicle = vehicleOpt.get();
        
        // 2. Validate vehicle status - MUST be "Available"
        if (!"Available".equalsIgnoreCase(vehicle.getStatus())) {
            log.error("Vehicle is not available for maintenance. Current status: {}", vehicle.getStatus());
            throw new RuntimeException("Vehicle must be in 'Available' status to schedule maintenance. Current status: " + vehicle.getStatus());
        }
        
        // 3. Create maintenance record with status "In Process"
        MaintenanceRecord record = new MaintenanceRecord();
        record.setVehicle(vehicle);
        record.setServiceDate(request.getServiceDate());
        record.setDescription(request.getDescription());
        record.setCost(BigDecimal.valueOf(request.getCost()));
        record.setVendorNote(request.getVendorNote());
        record.setStatus("In Process");
        
        MaintenanceRecord savedRecord = maintenanceRecordRepository.save(record);
        log.info("Maintenance record created with ID: {}", savedRecord.getId());
        
        // 4. AUTO UPDATE: Change vehicle status to "In Maintenance"
        vehicle.setStatus("In Maintenance");
        vehicleRepository.save(vehicle);
        log.info("Vehicle status updated to 'In Maintenance' for vehicle ID: {}", vehicle.getId());
        
        return savedRecord;
    }
    
    @Override
    @Transactional
    public MaintenanceRecord updateMaintenanceRecord(UUID id, UpdateMaintenanceRecordRequestDTO request) {
        log.info("Updating maintenance record ID: {}", id);
        
        // 1. Validate maintenance record exists and not soft-deleted
        Optional<MaintenanceRecord> recordOpt = maintenanceRecordRepository.findByIdNotDeleted(id);
        if (recordOpt.isEmpty()) {
            log.error("Maintenance record not found or has been deleted: {}", id);
            throw new RuntimeException("Maintenance record not found or has been deleted");
        }
        
        MaintenanceRecord record = recordOpt.get();
        
        // 2. Update fields
        record.setServiceDate(request.getServiceDate());
        record.setDescription(request.getDescription());
        record.setCost(BigDecimal.valueOf(request.getCost()));
        record.setVendorNote(request.getVendorNote());
        
        MaintenanceRecord updatedRecord = maintenanceRecordRepository.save(record);
        log.info("Maintenance record updated successfully: {}", id);
        
        return updatedRecord;
    }
    
    @Override
    @Transactional
    public MaintenanceRecord updateMaintenanceStatus(UUID id, UpdateMaintenanceStatusDTO request) {
        log.info("Updating maintenance record status. ID: {}, New Status: {}", id, request.getStatus());
        
        // 1. Validate maintenance record exists and not soft-deleted
        Optional<MaintenanceRecord> recordOpt = maintenanceRecordRepository.findByIdNotDeleted(id);
        if (recordOpt.isEmpty()) {
            log.error("Maintenance record not found or has been deleted: {}", id);
            throw new RuntimeException("Maintenance record not found or has been deleted");
        }
        
        MaintenanceRecord record = recordOpt.get();
        Vehicle vehicle = record.getVehicle();
        
        // 2. Update status
        String newStatus = request.getStatus();
        record.setStatus(newStatus);
        MaintenanceRecord updatedRecord = maintenanceRecordRepository.save(record);
        log.info("Maintenance record status updated to: {}", newStatus);
        
        // 3. CRITICAL: If status is "Completed", auto-update vehicle status to "Available"
        if ("Completed".equalsIgnoreCase(newStatus)) {
            vehicle.setStatus("Available");
            vehicleRepository.save(vehicle);
            log.info("Vehicle status auto-updated to 'Available' for vehicle ID: {}", vehicle.getId());
        }
        
        return updatedRecord;
    }
    
    @Override
    @Transactional
    public void softDeleteMaintenanceRecord(UUID id) {
        log.info("Soft deleting maintenance record ID: {}", id);
        
        // 1. Validate maintenance record exists and not already soft-deleted
        Optional<MaintenanceRecord> recordOpt = maintenanceRecordRepository.findByIdNotDeleted(id);
        if (recordOpt.isEmpty()) {
            log.error("Maintenance record not found or has been deleted: {}", id);
            throw new RuntimeException("Maintenance record not found or has been deleted");
        }
        
        MaintenanceRecord record = recordOpt.get();
        
        // 2. Perform soft delete by setting deletedAt timestamp
        record.setDeletedAt(LocalDateTime.now());
        maintenanceRecordRepository.save(record);
        log.info("Maintenance record soft deleted successfully: {}", id);
    }
}
