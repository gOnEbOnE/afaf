package apap.ti._5.vehicle_rental_2306245592_be.repository;

import apap.ti._5.vehicle_rental_2306245592_be.model.RentalAddOn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RentalAddOnRepository extends JpaRepository<RentalAddOn, UUID> {
    
    @Query("SELECT a FROM RentalAddOn a WHERE a.id IN :ids")
    List<RentalAddOn> findAllById(@Param("ids") List<UUID> ids);
}