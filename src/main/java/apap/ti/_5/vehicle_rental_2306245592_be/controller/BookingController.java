package apap.ti._5.vehicle_rental_2306245592_be.controller;

import apap.ti._5.vehicle_rental_2306245592_be.model.RentalBooking;
import apap.ti._5.vehicle_rental_2306245592_be.restdto.BaseResponseDTO;
import apap.ti._5.vehicle_rental_2306245592_be.restdto.response.booking.RentalBookingResponseDTO;
import apap.ti._5.vehicle_rental_2306245592_be.restdto.response.RentalAddOn.RentalAddOnResponseDTO;
import apap.ti._5.vehicle_rental_2306245592_be.service.BookingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    private RentalBookingResponseDTO convertToDTO(RentalBooking booking) {
        List<RentalAddOnResponseDTO> addOnDTOs = booking.getListOfAddOns() != null ?
            booking.getListOfAddOns().stream()
                .map(addOn -> new RentalAddOnResponseDTO(
                    addOn.getId(),
                    addOn.getName(),
                    addOn.getPrice(),
                    addOn.getCreatedAt(),
                    addOn.getUpdatedAt()
                ))
                .collect(Collectors.toList()) : null;

        return new RentalBookingResponseDTO(
            booking.getId(),
            booking.getVehicle().getId(),
            booking.getVehicle().getBrand(),
            booking.getVehicle().getModel(),
            booking.getPickUpTime(),
            booking.getDropOffTime(),
            booking.getPickUpLocation(),
            booking.getDropOffLocation(),
            booking.getCapacityNeeded(),
            booking.getTransmissionNeeded(),
            booking.getTotalPrice(),
            booking.getIncludeDriver(),
            booking.getStatus(),
            addOnDTOs,
            booking.getCreatedAt(),
            booking.getUpdatedAt()
        );
    }

    @GetMapping
    public ResponseEntity<BaseResponseDTO<List<RentalBookingResponseDTO>>> getAllBookings(
            @RequestParam(required = false) String status) {
        
        List<RentalBooking> bookings;
        
        if (status != null && !status.isEmpty()) {
            bookings = bookingService.getBookingsByStatus(status);
        } else {
            bookings = bookingService.getAllBookings();
        }
        
        List<RentalBookingResponseDTO> response = bookings.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
        
        BaseResponseDTO<List<RentalBookingResponseDTO>> apiResponse = new BaseResponseDTO<>(
            200, "Bookings retrieved successfully (Total: " + bookings.size() + ")", new Date(), response
        );
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/count")
    public ResponseEntity<BaseResponseDTO<Integer>> getBookingCount() {
        int count = bookingService.getAllBookings().size();
        
        BaseResponseDTO<Integer> response = new BaseResponseDTO<>(
            200, "Success", new Date(), count
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponseDTO<RentalBookingResponseDTO>> getBookingById(@PathVariable String id) {
        Optional<RentalBooking> booking = bookingService.getBookingById(id);
        
        if (booking.isPresent()) {
            RentalBookingResponseDTO dto = convertToDTO(booking.get());
            BaseResponseDTO<RentalBookingResponseDTO> response = new BaseResponseDTO<>(
                200, "Success", new Date(), dto
            );
            return ResponseEntity.ok(response);
        }
        
        BaseResponseDTO<RentalBookingResponseDTO> response = new BaseResponseDTO<>(
            404, "Booking not found", new Date(), null
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @PostMapping
    public ResponseEntity<BaseResponseDTO<RentalBookingResponseDTO>> createBooking(@RequestBody RentalBooking booking) {
        RentalBooking createdBooking = bookingService.createBooking(booking);
        RentalBookingResponseDTO dto = convertToDTO(createdBooking);
        BaseResponseDTO<RentalBookingResponseDTO> response = new BaseResponseDTO<>(
            201, "Booking created successfully", new Date(), dto
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BaseResponseDTO<RentalBookingResponseDTO>> updateBooking(
            @PathVariable String id, 
            @RequestBody RentalBooking booking) {
        try {
            RentalBooking updatedBooking = bookingService.updateBooking(id, booking);
            RentalBookingResponseDTO dto = convertToDTO(updatedBooking);
            BaseResponseDTO<RentalBookingResponseDTO> response = new BaseResponseDTO<>(
                200, "Booking updated successfully", new Date(), dto
            );
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            BaseResponseDTO<RentalBookingResponseDTO> response = new BaseResponseDTO<>(
                404, e.getMessage(), new Date(), null
            );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponseDTO<Void>> deleteBooking(@PathVariable String id) {
        try {
            bookingService.deleteBooking(id);
            BaseResponseDTO<Void> response = new BaseResponseDTO<>(
                200, "Booking deleted successfully", new Date(), null
            );
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            BaseResponseDTO<Void> response = new BaseResponseDTO<>(
                404, e.getMessage(), new Date(), null
            );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
}