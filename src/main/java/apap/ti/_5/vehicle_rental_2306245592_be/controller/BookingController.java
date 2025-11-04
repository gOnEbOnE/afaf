package apap.ti._5.vehicle_rental_2306245592_be.controller;

import apap.ti._5.vehicle_rental_2306245592_be.model.RentalBooking;
import apap.ti._5.vehicle_rental_2306245592_be.restdto.BaseResponseDTO;
import apap.ti._5.vehicle_rental_2306245592_be.service.BookingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping
    public ResponseEntity<BaseResponseDTO<List<RentalBooking>>> getAllBookings(
            @RequestParam(required = false) String status) {
        
        List<RentalBooking> bookings;
        
        if (status != null && !status.isEmpty()) {
            bookings = bookingService.getBookingsByStatus(status);
        } else {
            bookings = bookingService.getAllBookings();
        }
        
        BaseResponseDTO<List<RentalBooking>> response = new BaseResponseDTO<>(
            200, "Bookings retrieved successfully (Total: " + bookings.size() + ")", new Date(), bookings
        );
        return ResponseEntity.ok(response);
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
    public ResponseEntity<BaseResponseDTO<RentalBooking>> getBookingById(@PathVariable String id) {
        Optional<RentalBooking> booking = bookingService.getBookingById(id);
        
        if (booking.isPresent()) {
            BaseResponseDTO<RentalBooking> response = new BaseResponseDTO<>(
                200, "Success", new Date(), booking.get()
            );
            return ResponseEntity.ok(response);
        }
        
        BaseResponseDTO<RentalBooking> response = new BaseResponseDTO<>(
            404, "Booking not found", new Date(), null
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @PostMapping
    public ResponseEntity<BaseResponseDTO<RentalBooking>> createBooking(@RequestBody RentalBooking booking) {
        RentalBooking createdBooking = bookingService.createBooking(booking);
        BaseResponseDTO<RentalBooking> response = new BaseResponseDTO<>(
            201, "Booking created successfully", new Date(), createdBooking
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BaseResponseDTO<RentalBooking>> updateBooking(
            @PathVariable String id, 
            @RequestBody RentalBooking booking) {
        try {
            RentalBooking updatedBooking = bookingService.updateBooking(id, booking);
            BaseResponseDTO<RentalBooking> response = new BaseResponseDTO<>(
                200, "Booking updated successfully", new Date(), updatedBooking
            );
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            BaseResponseDTO<RentalBooking> response = new BaseResponseDTO<>(
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