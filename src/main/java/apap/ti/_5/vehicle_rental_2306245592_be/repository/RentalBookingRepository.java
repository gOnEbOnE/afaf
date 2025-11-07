package apap.ti._5.vehicle_rental_2306245592_be.repository;

import apap.ti._5.vehicle_rental_2306245592_be.model.RentalBooking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RentalBookingRepository extends JpaRepository<RentalBooking, String> {
    List<RentalBooking> findByStatus(String status);
    
    List<RentalBooking> findByVehicleId(String vehicleId);
    
    @Query("SELECT rb FROM RentalBooking rb WHERE rb.vehicle.id = :vehicleId " +
           "AND rb.status IN ('Upcoming', 'Ongoing') " +
           "AND NOT (rb.dropOffTime <= :pickUpTime OR rb.pickUpTime >= :dropOffTime)")
    List<RentalBooking> findConflictingBookings(
        @Param("vehicleId") String vehicleId,
        @Param("pickUpTime") LocalDateTime pickUpTime,
        @Param("dropOffTime") LocalDateTime dropOffTime
    );
    
    // Find all bookings and sort in Java instead of DB
    @Query("SELECT rb FROM RentalBooking rb ORDER BY rb.id DESC")
    List<RentalBooking> findAllOrderByIdDesc();
}
