package apap.ti._5.vehicle_rental_2306245592_be.repository;

import apap.ti._5.vehicle_rental_2306245592_be.model.RentalBooking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RentalBookingRepository extends JpaRepository<RentalBooking, String> {
    List<RentalBooking> findByStatus(String status);
}
