package apap.ti._5.vehicle_rental_2306245592_be.service;

import apap.ti._5.vehicle_rental_2306245592_be.model.RentalAddOn;
import apap.ti._5.vehicle_rental_2306245592_be.model.RentalBooking;
import apap.ti._5.vehicle_rental_2306245592_be.restdto.request.booking.CreateBookingRequestDTO;
import apap.ti._5.vehicle_rental_2306245592_be.restdto.request.booking.UpdateAddOnsRequestDTO;
import apap.ti._5.vehicle_rental_2306245592_be.restdto.request.booking.UpdateBookingRequestDTO;
import apap.ti._5.vehicle_rental_2306245592_be.restdto.request.booking.UpdateBookingStatusDTO;
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
    
    // ✅ NEW: Get booking details for update form
    Optional<RentalBooking> getBookingForUpdate(String id);
    
    // ✅ NEW: Update booking details
    RentalBooking updateBookingDetails(String id, UpdateBookingRequestDTO updateDTO);
    
    // ✅ NEW: Get booking for update status
    Optional<RentalBooking> getBookingForUpdateStatus(String id);
    
    // ✅ NEW: Update booking status
    RentalBooking updateBookingStatus(String id, UpdateBookingStatusDTO updateDTO);
    
    // ✅ Helper: Get available status transitions
    List<String> getAvailableStatusTransitions(String currentStatus, String bookingId);
    
    // ✅ NEW: Get booking for update add-ons
    Optional<RentalBooking> getBookingForUpdateAddOns(String id);
    
    // ✅ NEW: Update booking add-ons
    RentalBooking updateBookingAddOns(String id, UpdateAddOnsRequestDTO updateDTO);
}