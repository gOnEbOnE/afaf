package apap.ti._5.vehicle_rental_2306245592_be.service;

import apap.ti._5.vehicle_rental_2306245592_be.model.RentalBooking;
import java.util.List;
import java.util.Optional;

public interface BookingService {
    List<RentalBooking> getAllBookings();
    Optional<RentalBooking> getBookingById(String id);
    RentalBooking createBooking(RentalBooking booking);
    RentalBooking updateBooking(String id, RentalBooking booking);
    void deleteBooking(String id);
    List<RentalBooking> getBookingsByStatus(String status);
}