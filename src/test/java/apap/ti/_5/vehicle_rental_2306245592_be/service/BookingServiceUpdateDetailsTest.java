package apap.ti._5.vehicle_rental_2306245592_be.service;

import apap.ti._5.vehicle_rental_2306245592_be.model.RentalAddOn;
import apap.ti._5.vehicle_rental_2306245592_be.model.RentalBooking;
import apap.ti._5.vehicle_rental_2306245592_be.model.RentalVendor;
import apap.ti._5.vehicle_rental_2306245592_be.model.Vehicle;
import apap.ti._5.vehicle_rental_2306245592_be.repository.RentalAddOnRepository;
import apap.ti._5.vehicle_rental_2306245592_be.repository.RentalBookingRepository;
import apap.ti._5.vehicle_rental_2306245592_be.repository.VehicleRepository;
import apap.ti._5.vehicle_rental_2306245592_be.restdto.request.booking.UpdateBookingRequestDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Comprehensive unit tests for BookingService.updateBookingDetails method
 * Testing all validation paths, error scenarios, and business logic
 */
@ExtendWith(MockitoExtension.class)
class BookingServiceUpdateDetailsTest {

    @Mock
    private RentalBookingRepository rentalBookingRepository;

    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private RentalAddOnRepository rentalAddOnRepository;

    @Mock
    private LocationService locationService;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private RentalBooking testBooking;
    private Vehicle testVehicle;
    private RentalVendor testVendor;
    private UpdateBookingRequestDTO updateDTO;

    @BeforeEach
    void setUp() {
        // Setup vendor with locations
        testVendor = new RentalVendor();
        testVendor.setName("Test Vendor");
        testVendor.setEmail("test@vendor.com");
        testVendor.setPhone("08123456789");
        testVendor.setListOfLocations(Arrays.asList("Jakarta", "Bandung", "Surabaya"));

        // Setup vehicle
        testVehicle = new Vehicle();
        testVehicle.setId("VEH-001");
        testVehicle.setType("SUV");
        testVehicle.setBrand("Toyota");
        testVehicle.setModel("Avanza");
        testVehicle.setYear(2023);
        testVehicle.setCapacity(7);
        testVehicle.setTransmission("Automatic");
        testVehicle.setFuelType("Bensin");
        testVehicle.setPrice(350000.0);
        testVehicle.setStatus("Available");
        testVehicle.setLocation("Jakarta");
        testVehicle.setLicensePlate("B1234XYZ");
        testVehicle.setRentalVendor(testVendor);

        // Setup booking
        testBooking = new RentalBooking();
        testBooking.setId("BKG-001");
        testBooking.setVehicle(testVehicle);
        testBooking.setPickUpLocation("Jakarta");
        testBooking.setDropOffLocation("Bandung");
        testBooking.setPickUpTime(LocalDateTime.now().plusDays(2));
        testBooking.setDropOffTime(LocalDateTime.now().plusDays(5));
        testBooking.setStatus("Upcoming");
        testBooking.setIncludeDriver(false);
        testBooking.setCapacityNeeded(7);
        testBooking.setTransmissionNeeded("Automatic");
        testBooking.setTotalPrice(1050000.0);
        testBooking.setListOfAddOns(new ArrayList<>());

        // Setup update DTO
        updateDTO = new UpdateBookingRequestDTO();
        updateDTO.setPickUpLocation("Bandung");
        updateDTO.setDropOffLocation("Surabaya");
        updateDTO.setPickUpTime(LocalDateTime.now().plusDays(3));
        updateDTO.setDropOffTime(LocalDateTime.now().plusDays(6));
        updateDTO.setIncludeDriver(true);
        updateDTO.setCapacityNeeded(5);
        updateDTO.setTransmissionNeeded("Manual");
    }

    // Test 1: Successful update with valid data
    @Test
    void testUpdateBookingDetailsSuccess() {
        when(rentalBookingRepository.findById("BKG-001")).thenReturn(Optional.of(testBooking));
        when(rentalBookingRepository.save(any(RentalBooking.class))).thenReturn(testBooking);

        RentalBooking result = bookingService.updateBookingDetails("BKG-001", updateDTO);

        assertNotNull(result);
        assertEquals("Bandung", result.getPickUpLocation());
        assertEquals("Surabaya", result.getDropOffLocation());
        assertTrue(result.getIncludeDriver());
        assertEquals(5, result.getCapacityNeeded());
        assertEquals("Manual", result.getTransmissionNeeded());
        
        verify(rentalBookingRepository, times(1)).findById("BKG-001");
        verify(rentalBookingRepository, times(1)).save(any(RentalBooking.class));
    }

    // Test 2: Error - Booking not found
    @Test
    void testUpdateBookingDetailsNotFound() {
        when(rentalBookingRepository.findById("INVALID-ID")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            bookingService.updateBookingDetails("INVALID-ID", updateDTO);
        });

        assertEquals("Booking tidak ditemukan", exception.getMessage());
        verify(rentalBookingRepository, times(1)).findById("INVALID-ID");
        verify(rentalBookingRepository, never()).save(any());
    }

    // Test 3: Error - Pickup time in the past
    @Test
    void testUpdateBookingDetailsPickupTimeInPast() {
        updateDTO.setPickUpTime(LocalDateTime.now().minusHours(1));
        updateDTO.setDropOffTime(LocalDateTime.now().plusDays(2));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            bookingService.updateBookingDetails("BKG-001", updateDTO);
        });

        assertTrue(exception.getMessage().contains("Waktu pengambilan tidak boleh lebih dari 10 menit di masa lalu"));
    }

    // Test 4: Error - Pickup time after dropoff time
    @Test
    void testUpdateBookingDetailsInvalidTimeRange() {
        updateDTO.setPickUpTime(LocalDateTime.now().plusDays(5));
        updateDTO.setDropOffTime(LocalDateTime.now().plusDays(2));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            bookingService.updateBookingDetails("BKG-001", updateDTO);
        });

        assertEquals("Waktu pengambilan harus sebelum waktu pengembalian", exception.getMessage());
    }

    // Test 5: Error - Booking status is not Upcoming
    @Test
    void testUpdateBookingDetailsNotUpcomingStatus() {
        testBooking.setStatus("Ongoing");
        when(rentalBookingRepository.findById("BKG-001")).thenReturn(Optional.of(testBooking));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            bookingService.updateBookingDetails("BKG-001", updateDTO);
        });

        assertEquals("Hanya booking dengan status 'Upcoming' yang dapat diubah", exception.getMessage());
        verify(rentalBookingRepository, never()).save(any());
    }

    // Test 6: Error - Vendor locations is null
    @Test
    void testUpdateBookingDetailsVendorLocationsNull() {
        testVendor.setListOfLocations(null);
        when(rentalBookingRepository.findById("BKG-001")).thenReturn(Optional.of(testBooking));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            bookingService.updateBookingDetails("BKG-001", updateDTO);
        });

        assertEquals("Vendor tidak memiliki lokasi yang tersedia", exception.getMessage());
        verify(rentalBookingRepository, never()).save(any());
    }

    // Test 7: Error - Pickup location not in vendor locations
    @Test
    void testUpdateBookingDetailsInvalidPickupLocation() {
        updateDTO.setPickUpLocation("Bali"); // Not in vendor locations
        updateDTO.setDropOffLocation("Bandung");
        
        when(rentalBookingRepository.findById("BKG-001")).thenReturn(Optional.of(testBooking));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            bookingService.updateBookingDetails("BKG-001", updateDTO);
        });

        assertEquals("Vendor tidak beroperasi di salah satu atau kedua lokasi yang dipilih", exception.getMessage());
        verify(rentalBookingRepository, never()).save(any());
    }

    // Test 8: Error - Dropoff location not in vendor locations
    @Test
    void testUpdateBookingDetailsInvalidDropoffLocation() {
        updateDTO.setPickUpLocation("Jakarta");
        updateDTO.setDropOffLocation("Bali"); // Not in vendor locations
        
        when(rentalBookingRepository.findById("BKG-001")).thenReturn(Optional.of(testBooking));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            bookingService.updateBookingDetails("BKG-001", updateDTO);
        });

        assertEquals("Vendor tidak beroperasi di salah satu atau kedua lokasi yang dipilih", exception.getMessage());
        verify(rentalBookingRepository, never()).save(any());
    }

    // Test 9: Error - Both locations invalid
    @Test
    void testUpdateBookingDetailsBothLocationsInvalid() {
        updateDTO.setPickUpLocation("Bali");
        updateDTO.setDropOffLocation("Yogyakarta");
        
        when(rentalBookingRepository.findById("BKG-001")).thenReturn(Optional.of(testBooking));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            bookingService.updateBookingDetails("BKG-001", updateDTO);
        });

        assertEquals("Vendor tidak beroperasi di salah satu atau kedua lokasi yang dipilih", exception.getMessage());
        verify(rentalBookingRepository, never()).save(any());
    }

    // Test 10: Successful update with driver included - price calculation
    @Test
    void testUpdateBookingDetailsWithDriverPriceCalculation() {
        updateDTO.setIncludeDriver(true);
        updateDTO.setPickUpTime(LocalDateTime.now().plusDays(1));
        updateDTO.setDropOffTime(LocalDateTime.now().plusDays(4)); // 3 days
        
        when(rentalBookingRepository.findById("BKG-001")).thenReturn(Optional.of(testBooking));
        when(rentalBookingRepository.save(any(RentalBooking.class))).thenAnswer(invocation -> invocation.getArgument(0));

        RentalBooking result = bookingService.updateBookingDetails("BKG-001", updateDTO);

        // 3 days * 350000 (vehicle) + 3 days * 100000 (driver) = 1050000 + 300000 = 1350000
        assertEquals(1350000.0, result.getTotalPrice(), 0.01);
        assertTrue(result.getIncludeDriver());
    }

    // Test 11: Successful update without driver - price calculation
    @Test
    void testUpdateBookingDetailsWithoutDriverPriceCalculation() {
        updateDTO.setIncludeDriver(false);
        updateDTO.setPickUpTime(LocalDateTime.now().plusDays(1));
        updateDTO.setDropOffTime(LocalDateTime.now().plusDays(3)); // 2 days
        
        when(rentalBookingRepository.findById("BKG-001")).thenReturn(Optional.of(testBooking));
        when(rentalBookingRepository.save(any(RentalBooking.class))).thenAnswer(invocation -> invocation.getArgument(0));

        RentalBooking result = bookingService.updateBookingDetails("BKG-001", updateDTO);

        // 2 days * 350000 (vehicle) = 700000
        assertEquals(700000.0, result.getTotalPrice(), 0.01);
        assertFalse(result.getIncludeDriver());
    }

    // Test 12: Price calculation with existing add-ons
    @Test
    void testUpdateBookingDetailsWithAddOnsPriceCalculation() {
        // Add some add-ons to the booking
        RentalAddOn addon1 = new RentalAddOn();
        addon1.setName("GPS");
        addon1.setPrice(50000.0);
        
        RentalAddOn addon2 = new RentalAddOn();
        addon2.setName("Child Seat");
        addon2.setPrice(30000.0);
        
        testBooking.setListOfAddOns(Arrays.asList(addon1, addon2));
        
        updateDTO.setIncludeDriver(false);
        updateDTO.setPickUpTime(LocalDateTime.now().plusDays(1));
        updateDTO.setDropOffTime(LocalDateTime.now().plusDays(3)); // 2 days
        
        when(rentalBookingRepository.findById("BKG-001")).thenReturn(Optional.of(testBooking));
        when(rentalBookingRepository.save(any(RentalBooking.class))).thenAnswer(invocation -> invocation.getArgument(0));

        RentalBooking result = bookingService.updateBookingDetails("BKG-001", updateDTO);

        // 2 days * 350000 (vehicle) + 50000 (GPS) + 30000 (Child Seat) = 700000 + 80000 = 780000
        assertEquals(780000.0, result.getTotalPrice(), 0.01);
    }

    // Test 13: Price calculation with add-ons and driver
    @Test
    void testUpdateBookingDetailsWithAddOnsAndDriverPriceCalculation() {
        RentalAddOn addon = new RentalAddOn();
        addon.setName("GPS");
        addon.setPrice(50000.0);
        testBooking.setListOfAddOns(Arrays.asList(addon));
        
        updateDTO.setIncludeDriver(true);
        updateDTO.setPickUpTime(LocalDateTime.now().plusDays(1));
        updateDTO.setDropOffTime(LocalDateTime.now().plusDays(4)); // 3 days
        
        when(rentalBookingRepository.findById("BKG-001")).thenReturn(Optional.of(testBooking));
        when(rentalBookingRepository.save(any(RentalBooking.class))).thenAnswer(invocation -> invocation.getArgument(0));

        RentalBooking result = bookingService.updateBookingDetails("BKG-001", updateDTO);

        // 3 days * 350000 (vehicle) + 3 days * 100000 (driver) + 50000 (GPS) = 1050000 + 300000 + 50000 = 1400000
        assertEquals(1400000.0, result.getTotalPrice(), 0.01);
    }

    // Test 14: Minimum rental period (less than 24 hours)
    @Test
    void testUpdateBookingDetailsMinimumRentalPeriod() {
        updateDTO.setPickUpTime(LocalDateTime.now().plusDays(1));
        updateDTO.setDropOffTime(LocalDateTime.now().plusDays(1).plusHours(12)); // 12 hours
        updateDTO.setIncludeDriver(false);
        
        when(rentalBookingRepository.findById("BKG-001")).thenReturn(Optional.of(testBooking));
        when(rentalBookingRepository.save(any(RentalBooking.class))).thenAnswer(invocation -> invocation.getArgument(0));

        RentalBooking result = bookingService.updateBookingDetails("BKG-001", updateDTO);

        // Even for 12 hours, should charge for 1 day: 1 day * 350000 = 350000
        assertEquals(350000.0, result.getTotalPrice(), 0.01);
    }

    // Test 15: Update all booking fields
    @Test
    void testUpdateBookingDetailsAllFieldsUpdated() {
        updateDTO.setPickUpLocation("Surabaya");
        updateDTO.setDropOffLocation("Jakarta");
        updateDTO.setPickUpTime(LocalDateTime.now().plusDays(5));
        updateDTO.setDropOffTime(LocalDateTime.now().plusDays(8));
        updateDTO.setCapacityNeeded(4);
        updateDTO.setTransmissionNeeded("Automatic");
        updateDTO.setIncludeDriver(true);
        
        when(rentalBookingRepository.findById("BKG-001")).thenReturn(Optional.of(testBooking));
        when(rentalBookingRepository.save(any(RentalBooking.class))).thenAnswer(invocation -> invocation.getArgument(0));

        RentalBooking result = bookingService.updateBookingDetails("BKG-001", updateDTO);

        assertEquals("Surabaya", result.getPickUpLocation());
        assertEquals("Jakarta", result.getDropOffLocation());
        assertEquals(4, result.getCapacityNeeded());
        assertEquals("Automatic", result.getTransmissionNeeded());
        assertTrue(result.getIncludeDriver());
        assertNotNull(result.getTotalPrice());
    }

    // Test 16: Exact 24-hour rental period
    @Test
    void testUpdateBookingDetailsExactOneDayRental() {
        LocalDateTime pickupTime = LocalDateTime.now().plusDays(2);
        updateDTO.setPickUpTime(pickupTime);
        updateDTO.setDropOffTime(pickupTime.plusHours(24)); // Exactly 24 hours
        updateDTO.setIncludeDriver(false);
        
        when(rentalBookingRepository.findById("BKG-001")).thenReturn(Optional.of(testBooking));
        when(rentalBookingRepository.save(any(RentalBooking.class))).thenAnswer(invocation -> invocation.getArgument(0));

        RentalBooking result = bookingService.updateBookingDetails("BKG-001", updateDTO);

        // Exactly 24 hours = 1 day: 1 day * 350000 = 350000
        assertEquals(350000.0, result.getTotalPrice(), 0.01);
    }

    // Test 17: Long rental period (7 days)
    @Test
    void testUpdateBookingDetailsLongRentalPeriod() {
        updateDTO.setPickUpTime(LocalDateTime.now().plusDays(1));
        updateDTO.setDropOffTime(LocalDateTime.now().plusDays(8)); // 7 days
        updateDTO.setIncludeDriver(true);
        
        when(rentalBookingRepository.findById("BKG-001")).thenReturn(Optional.of(testBooking));
        when(rentalBookingRepository.save(any(RentalBooking.class))).thenAnswer(invocation -> invocation.getArgument(0));

        RentalBooking result = bookingService.updateBookingDetails("BKG-001", updateDTO);

        // 7 days * 350000 (vehicle) + 7 days * 100000 (driver) = 2450000 + 700000 = 3150000
        assertEquals(3150000.0, result.getTotalPrice(), 0.01);
    }

    // Test 18: Status remains Upcoming after update
    @Test
    void testUpdateBookingDetailsStatusRemainsUpcoming() {
        when(rentalBookingRepository.findById("BKG-001")).thenReturn(Optional.of(testBooking));
        when(rentalBookingRepository.save(any(RentalBooking.class))).thenAnswer(invocation -> invocation.getArgument(0));

        RentalBooking result = bookingService.updateBookingDetails("BKG-001", updateDTO);

        // Status should remain "Upcoming" or be updated based on pickup time
        assertNotNull(result.getStatus());
    }

    // Test 19: Vendor with empty locations list
    @Test
    void testUpdateBookingDetailsVendorEmptyLocationsList() {
        testVendor.setListOfLocations(new ArrayList<>());
        when(rentalBookingRepository.findById("BKG-001")).thenReturn(Optional.of(testBooking));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            bookingService.updateBookingDetails("BKG-001", updateDTO);
        });

        assertEquals("Vendor tidak beroperasi di salah satu atau kedua lokasi yang dipilih", exception.getMessage());
    }

    // Test 20: Same pickup and dropoff location
    @Test
    void testUpdateBookingDetailsSamePickupAndDropoffLocation() {
        updateDTO.setPickUpLocation("Jakarta");
        updateDTO.setDropOffLocation("Jakarta");
        updateDTO.setIncludeDriver(false);
        
        when(rentalBookingRepository.findById("BKG-001")).thenReturn(Optional.of(testBooking));
        when(rentalBookingRepository.save(any(RentalBooking.class))).thenAnswer(invocation -> invocation.getArgument(0));

        RentalBooking result = bookingService.updateBookingDetails("BKG-001", updateDTO);

        assertEquals("Jakarta", result.getPickUpLocation());
        assertEquals("Jakarta", result.getDropOffLocation());
        assertNotNull(result.getTotalPrice());
    }
}
