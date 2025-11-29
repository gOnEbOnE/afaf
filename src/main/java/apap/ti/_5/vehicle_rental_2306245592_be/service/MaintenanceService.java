package apap.ti._5.vehicle_rental_2306245592_be.service;

import apap.ti._5.vehicle_rental_2306245592_be.model.MaintenanceRecord;
import apap.ti._5.vehicle_rental_2306245592_be.restdto.request.maintenance.CreateMaintenanceRecordRequestDTO;
import apap.ti._5.vehicle_rental_2306245592_be.restdto.request.maintenance.UpdateMaintenanceRecordRequestDTO;
import apap.ti._5.vehicle_rental_2306245592_be.restdto.request.maintenance.UpdateMaintenanceStatusDTO;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MaintenanceService {
    List<MaintenanceRecord> getAllMaintenanceRecords();
    Optional<MaintenanceRecord> getMaintenanceRecordById(UUID id);
    MaintenanceRecord createMaintenanceRecord(CreateMaintenanceRecordRequestDTO request);
    MaintenanceRecord updateMaintenanceRecord(UUID id, UpdateMaintenanceRecordRequestDTO request);
    MaintenanceRecord updateMaintenanceStatus(UUID id, UpdateMaintenanceStatusDTO request);
    void softDeleteMaintenanceRecord(UUID id);
}
