package apap.ti._5.vehicle_rental_2306245592_be.repository;

import apap.ti._5.vehicle_rental_2306245592_be.model.RentalBooking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface RentalBookingRepository extends JpaRepository<RentalBooking, String> {
    
    // ✅ NEW: Find by customer ID (like LoyaltyPoints)
    Optional<RentalBooking> findByCustomerId(String customerId);
    List<RentalBooking> findAllByCustomerId(String customerId);
    
    // ✅ NEW: Find by customer ID not deleted
    @Query("SELECT b FROM RentalBooking b WHERE b.customerId = :customerId AND b.deletedAt IS NULL ORDER BY b.createdAt DESC")
    List<RentalBooking> findByCustomerIdNotDeleted(@Param("customerId") String customerId);
    
    // ✅ NEW: Find all not deleted
    @Query("SELECT rb FROM RentalBooking rb WHERE rb.deletedAt IS NULL")
    List<RentalBooking> findAllNotDeleted();

    // ✅ NEW: Find by status not deleted
    @Query("SELECT rb FROM RentalBooking rb WHERE rb.status = :status AND rb.deletedAt IS NULL")
    List<RentalBooking> findByStatusNotDeleted(@Param("status") String status);
    
    // ✅ NEW: Find by vehicle ID not deleted
    @Query("SELECT rb FROM RentalBooking rb WHERE rb.vehicle.id = :vehicleId AND rb.deletedAt IS NULL")
    List<RentalBooking> findByVehicleIdNotDeleted(@Param("vehicleId") String vehicleId);
    
    // ✅ NEW: Find all ordered by ID DESC not deleted
    @Query("SELECT rb FROM RentalBooking rb WHERE rb.deletedAt IS NULL ORDER BY rb.id DESC")
    List<RentalBooking> findAllNotDeletedOrderByIdDesc();
    
    // Existing queries (keep for backward compatibility)
    List<RentalBooking> findByStatus(String status);
    
    List<RentalBooking> findByVehicleId(String vehicleId);
    
    // Find all bookings and sort in Java instead of DB
    @Query("SELECT rb FROM RentalBooking rb ORDER BY rb.id DESC")
    List<RentalBooking> findAllOrderByIdDesc();
    
    @Query("SELECT b FROM RentalBooking b WHERE b.vehicle.id = :vehicleId AND b.status != 'Cancelled' AND ((b.pickUpTime <= :dropOffTime AND b.dropOffTime >= :pickUpTime))")
    List<RentalBooking> findConflictingBookings(
        @Param("vehicleId") String vehicleId,
        @Param("pickUpTime") LocalDateTime pickUpTime,
        @Param("dropOffTime") LocalDateTime dropOffTime
    );
    
    // ============ PBI-BE-V7 & V8: SOFT DELETE ============
    
    // Get all bookings NOT deleted, ordered by created date DESC
    @Query("SELECT b FROM RentalBooking b WHERE b.deletedAt IS NULL ORDER BY b.createdAt DESC")
    List<RentalBooking> findAllNotDeletedOrderByCreatedDesc();
    
    // Get booking by ID NOT deleted
    @Query("SELECT b FROM RentalBooking b WHERE b.id = :id AND b.deletedAt IS NULL")
    Optional<RentalBooking> findByIdNotDeleted(@Param("id") String id);
    
    // Get deleted bookings (for admin purposes)
    @Query("SELECT b FROM RentalBooking b WHERE b.deletedAt IS NOT NULL")
    List<RentalBooking> findDeletedBookings();
}
