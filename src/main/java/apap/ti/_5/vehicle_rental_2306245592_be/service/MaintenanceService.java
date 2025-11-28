package apap.ti._5.vehicle_rental_2306245592_be.service;

import apap.ti._5.vehicle_rental_2306245592_be.model.MaintenanceRecord;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MaintenanceService {
    List<MaintenanceRecord> getAllMaintenanceRecords();
    Optional<MaintenanceRecord> getMaintenanceRecordById(UUID id);
}
