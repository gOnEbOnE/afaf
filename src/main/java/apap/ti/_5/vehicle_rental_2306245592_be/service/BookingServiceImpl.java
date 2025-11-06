package apap.ti._5.vehicle_rental_2306245592_be.service;

import apap.ti._5.vehicle_rental_2306245592_be.model.RentalBooking;
import apap.ti._5.vehicle_rental_2306245592_be.repository.RentalBookingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class BookingServiceImpl implements BookingService {

    private final RentalBookingRepository rentalBookingRepository;

    public BookingServiceImpl(RentalBookingRepository rentalBookingRepository) {
        this.rentalBookingRepository = rentalBookingRepository;
    }

    @Override
    public List<RentalBooking> getAllBookings() {
        return rentalBookingRepository.findAll();
    }

    @Override
    public Optional<RentalBooking> getBookingById(String id) {
        return rentalBookingRepository.findById(id);
    }

    @Override
    public RentalBooking createBooking(RentalBooking booking) {
        return rentalBookingRepository.save(booking);
    }

    @Override
    public RentalBooking updateBooking(String id, RentalBooking booking) {
        Optional<RentalBooking> existingBooking = rentalBookingRepository.findById(id);
        if (existingBooking.isPresent()) {
            booking.setId(id);
            return rentalBookingRepository.save(booking);
        }
        throw new RuntimeException("Booking not found with id: " + id);
    }

    @Override
    public void deleteBooking(String id) {
        if (rentalBookingRepository.existsById(id)) {
            rentalBookingRepository.deleteById(id);
        } else {
            throw new RuntimeException("Booking not found with id: " + id);
        }
    }

    @Override
    public List<RentalBooking> getBookingsByStatus(String status) {
        return rentalBookingRepository.findByStatus(status);
    }

    @Override
    public List<RentalBooking> getBookingsByVehicleId(String vehicleId) {
        return rentalBookingRepository.findByVehicleId(vehicleId);
    }

    @Override
    public int getBookingCount() {
        return (int) rentalBookingRepository.count();
    }
}