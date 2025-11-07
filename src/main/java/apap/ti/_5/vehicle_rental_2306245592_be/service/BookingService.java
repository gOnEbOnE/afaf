package apap.ti._5.vehicle_rental_2306245592_be.service;

import apap.ti._5.vehicle_rental_2306245592_be.model.RentalAddOn;
import apap.ti._5.vehicle_rental_2306245592_be.model.RentalBooking;
import apap.ti._5.vehicle_rental_2306245592_be.restdto.request.booking.CreateBookingRequestDTO;
import apap.ti._5.vehicle_rental_2306245592_be.restdto.request.booking.AddAddOnsRequestDTO;
import apap.ti._5.vehicle_rental_2306245592_be.restdto.response.booking.SearchVehiclesResponseDTO;

import java.util.List;
import java.util.Optional;

public interface BookingService {
    List<RentalBooking> getAllBookings();
    Optional<RentalBooking> getBookingById(String id);
    RentalBooking createBooking(RentalBooking booking);
    RentalBooking updateBooking(String id, RentalBooking booking);
    void deleteBooking(String id);
    List<RentalBooking> getBookingsByStatus(String status);
    List<RentalBooking> getBookingsByVehicleId(String vehicleId);
    int getBookingCount();
    
    // New methods for create booking flow
    SearchVehiclesResponseDTO searchAvailableVehicles(CreateBookingRequestDTO criteria);
    RentalBooking createBookingWithAddOns(CreateBookingRequestDTO bookingDTO, AddAddOnsRequestDTO addOnsDTO);
    String generateBookingId();
    List<RentalAddOn> getAllAddOns();
    List<String> getAllProvinces();
}