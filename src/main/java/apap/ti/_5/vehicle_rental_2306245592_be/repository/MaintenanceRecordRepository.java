package apap.ti._5.vehicle_rental_2306245592_be.repository;

import apap.ti._5.vehicle_rental_2306245592_be.model.MaintenanceRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MaintenanceRecordRepository extends JpaRepository<MaintenanceRecord, UUID> {
    
    @Query("SELECT m FROM MaintenanceRecord m WHERE m.deletedAt IS NULL")
    List<MaintenanceRecord> findAllNotDeleted();
    
    @Query("SELECT m FROM MaintenanceRecord m WHERE m.id = :id AND m.deletedAt IS NULL")
    Optional<MaintenanceRecord> findByIdNotDeleted(@Param("id") UUID id);
}
