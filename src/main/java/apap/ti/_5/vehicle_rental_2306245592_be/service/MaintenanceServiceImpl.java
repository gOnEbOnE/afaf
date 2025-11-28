package apap.ti._5.vehicle_rental_2306245592_be.service;

import apap.ti._5.vehicle_rental_2306245592_be.model.MaintenanceRecord;
import apap.ti._5.vehicle_rental_2306245592_be.repository.MaintenanceRecordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class MaintenanceServiceImpl implements MaintenanceService {
    
    private final MaintenanceRecordRepository maintenanceRecordRepository;
    
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
}
