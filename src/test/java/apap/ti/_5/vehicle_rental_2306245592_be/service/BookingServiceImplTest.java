package apap.ti._5.vehicle_rental_2306245592_be.service;

import apap.ti._5.vehicle_rental_2306245592_be.model.RentalAddOn;
import apap.ti._5.vehicle_rental_2306245592_be.model.RentalBooking;
import apap.ti._5.vehicle_rental_2306245592_be.model.RentalVendor;
import apap.ti._5.vehicle_rental_2306245592_be.model.Vehicle;
import apap.ti._5.vehicle_rental_2306245592_be.repository.RentalAddOnRepository;
import apap.ti._5.vehicle_rental_2306245592_be.repository.RentalBookingRepository;
import apap.ti._5.vehicle_rental_2306245592_be.repository.RentalVendorRepository;
import apap.ti._5.vehicle_rental_2306245592_be.repository.VehicleRepository;
import apap.ti._5.vehicle_rental_2306245592_be.restdto.request.booking.CreateBookingRequestDTO;
import apap.ti._5.vehicle_rental_2306245592_be.restdto.request.booking.UpdateBookingStatusDTO;
import apap.ti._5.vehicle_rental_2306245592_be.restdto.request.booking.UpdateBookingRequestDTO;
import apap.ti._5.vehicle_rental_2306245592_be.restdto.request.booking.UpdateAddOnsRequestDTO;
import apap.ti._5.vehicle_rental_2306245592_be.restdto.response.booking.BookingChartDataDTO;
import apap.ti._5.vehicle_rental_2306245592_be.restdto.response.booking.SearchVehiclesResponseDTO;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class BookingServiceImplTest {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private RentalBookingRepository bookingRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private RentalVendorRepository vendorRepository;

    @Autowired
    private RentalAddOnRepository addOnRepository;

    private Vehicle testVehicle;
    private RentalVendor testVendor;
    private RentalBooking testBooking;

    @BeforeEach
    void setUp() {
        // Create test vendor
        testVendor = new RentalVendor();
        testVendor.setName("Test Vendor");
        testVendor.setEmail("test@vendor.com");
        testVendor.setPhone("08123456789");
        testVendor.setListOfLocations(Arrays.asList("Jakarta", "Bandung"));
        testVendor = vendorRepository.save(testVendor);

        // Create test vehicle
        testVehicle = new Vehicle();
        testVehicle.setId("VH-TEST-001");
        testVehicle.setType("SUV");
        testVehicle.setBrand("Toyota");
        testVehicle.setModel("Fortuner");
        testVehicle.setYear(2023);
        testVehicle.setCapacity(7);
        testVehicle.setTransmission("Automatic");
        testVehicle.setFuelType("Diesel");
        testVehicle.setLocation("Jakarta");
        testVehicle.setLicensePlate("B1234TEST");
        testVehicle.setPrice(800000.0);
        testVehicle.setStatus("Available");
        testVehicle.setRentalVendor(testVendor);
        testVehicle = vehicleRepository.save(testVehicle);

        // Create test booking
        testBooking = new RentalBooking();
        testBooking.setId("BKG-TEST-001");
        testBooking.setVehicle(testVehicle);
        testBooking.setPickUpLocation("Jakarta");
        testBooking.setDropOffLocation("Bandung");
        testBooking.setPickUpTime(LocalDateTime.now().plusDays(1));
        testBooking.setDropOffTime(LocalDateTime.now().plusDays(3));
        testBooking.setTotalPrice(2400000.0);
        testBooking.setStatus("Pending");
        testBooking.setIncludeDriver(false);
        testBooking.setCapacityNeeded(7);
        testBooking.setTransmissionNeeded("Automatic");
        testBooking = bookingRepository.save(testBooking);
    }

    @Test
    void testGetAllBookings() {
        List<RentalBooking> bookings = bookingService.getAllBookings();
        assertNotNull(bookings);
        assertTrue(bookings.size() >= 1);
    }

    @Test
    void testGetBookingById() {
        Optional<RentalBooking> booking = bookingService.getBookingById(testBooking.getId());
        assertTrue(booking.isPresent());
        assertEquals(testBooking.getId(), booking.get().getId());
    }

    @Test
    void testCreateBooking() {
        RentalBooking newBooking = new RentalBooking();
        newBooking.setId("BKG-TEST-002");
        newBooking.setVehicle(testVehicle);
        newBooking.setPickUpLocation("Surabaya");
        newBooking.setDropOffLocation("Malang");
        newBooking.setPickUpTime(LocalDateTime.now().plusDays(5));
        newBooking.setDropOffTime(LocalDateTime.now().plusDays(7));
        newBooking.setTotalPrice(1600000.0);
        newBooking.setStatus("Pending");
        newBooking.setIncludeDriver(true);
        newBooking.setCapacityNeeded(5);
        newBooking.setTransmissionNeeded("Manual");

        RentalBooking savedBooking = bookingService.createBooking(newBooking);
        assertNotNull(savedBooking);
        assertNotNull(savedBooking.getId());
    }

    @Test
    void testGetBookingsByStatus() {
        List<RentalBooking> bookings = bookingService.getBookingsByStatus("Pending");
        assertNotNull(bookings);
    }

    @Test
    void testGetBookingCount() {
        int count = bookingService.getBookingCount();
        assertTrue(count >= 1);
    }

    @Test
    void testUpdateBooking() {
        RentalBooking savedBooking = bookingRepository.findById(testBooking.getId()).get();
        savedBooking.setTotalPrice(3000000.0);

        RentalBooking updatedBooking = bookingService.updateBooking(savedBooking.getId(), savedBooking);
        assertNotNull(updatedBooking);
        assertEquals(3000000.0, updatedBooking.getTotalPrice());
    }

    @Test
    void testDeleteBooking() {
        bookingService.deleteBooking(testBooking.getId());
        Optional<RentalBooking> deletedBooking = bookingRepository.findById(testBooking.getId());
        assertFalse(deletedBooking.isPresent());
    }

    @Test
    void testGetAllAddOns() {
        List<RentalAddOn> addOns = bookingService.getAllAddOns();
        assertNotNull(addOns);
    }

    @Test
    void testGetAllProvinces() {
        List<String> provinces = bookingService.getAllProvinces();
        assertNotNull(provinces);
        assertFalse(provinces.isEmpty());
    }

    @Test
    void testGenerateBookingId() {
        String bookingId = bookingService.generateBookingId();
        assertNotNull(bookingId);
        assertTrue(bookingId.startsWith("BKG-"));
    }

    @Test
    void testUpdateBookingStatus() {
        UpdateBookingStatusDTO statusDTO = new UpdateBookingStatusDTO();
        statusDTO.setBookingId(testBooking.getId());
        statusDTO.setNewStatus("Confirmed");

        RentalBooking updatedBooking = bookingService.updateBookingStatus(testBooking.getId(), statusDTO);
        assertNotNull(updatedBooking);
        assertEquals("Confirmed", updatedBooking.getStatus());
    }

    @Test
    void testCancelBooking() {
        RentalBooking cancelledBooking = bookingService.cancelBooking(testBooking.getId());
        assertNotNull(cancelledBooking);
    }

    @Test
    void testSearchAvailableVehicles() {
        CreateBookingRequestDTO request = new CreateBookingRequestDTO();
        request.setPickUpLocation("Jakarta");
        request.setDropOffLocation("Bandung");
        request.setPickUpTime(LocalDateTime.now().plusDays(10));
        request.setDropOffTime(LocalDateTime.now().plusDays(12));
        request.setCapacityNeeded(5);
        request.setTransmissionNeeded("Automatic");
        request.setIncludeDriver(false);

        SearchVehiclesResponseDTO response = bookingService.searchAvailableVehicles(request);
        assertNotNull(response);
    }

    @Test
    void testGetBookingChartData() {
        BookingChartDataDTO chartData = bookingService.getBookingChartData("monthly", 11);
        assertNotNull(chartData);
    }

    @Test
    void testGetBookingsByVehicleId() {
        List<RentalBooking> bookings = bookingService.getBookingsByVehicleId(testVehicle.getId());
        assertNotNull(bookings);
    }

    @Test
    void testGetAvailableStatusTransitions() {
        List<String> transitions = bookingService.getAvailableStatusTransitions("Pending", testBooking.getId());
        assertNotNull(transitions);
    }

    @Test
    void testGetBookingForUpdate() {
        Optional<RentalBooking> booking = bookingService.getBookingForUpdate(testBooking.getId());
        assertTrue(booking.isPresent());
    }

    @Test
    void testUpdateBookingDetails() {
        UpdateBookingRequestDTO updateDTO = new UpdateBookingRequestDTO();
        updateDTO.setId(testBooking.getId());
        updateDTO.setPickUpLocation("Surabaya");
        updateDTO.setDropOffLocation("Malang");
        updateDTO.setPickUpTime(LocalDateTime.now().plusDays(2));
        updateDTO.setDropOffTime(LocalDateTime.now().plusDays(4));
        updateDTO.setCapacityNeeded(5);
        updateDTO.setTransmissionNeeded("Manual");
        updateDTO.setIncludeDriver(false);

        RentalBooking updated = bookingService.updateBookingDetails(testBooking.getId(), updateDTO);
        assertNotNull(updated);
    }

    @Test
    void testGetBookingForUpdateStatus() {
        Optional<RentalBooking> booking = bookingService.getBookingForUpdateStatus(testBooking.getId());
        assertTrue(booking.isPresent());
    }

    @Test
    void testGetBookingForUpdateAddOns() {
        Optional<RentalBooking> booking = bookingService.getBookingForUpdateAddOns(testBooking.getId());
        assertTrue(booking.isPresent());
    }

    @Test
    void testUpdateBookingAddOns() {
        UpdateAddOnsRequestDTO updateDTO = new UpdateAddOnsRequestDTO();
        updateDTO.setBookingId(testBooking.getId());
        updateDTO.setSelectedAddOnIds(Arrays.asList("1", "2"));

        RentalBooking updated = bookingService.updateBookingAddOns(testBooking.getId(), updateDTO);
        assertNotNull(updated);
    }

    @Test
    void testGetBookingChartDataQuarterly() {
        BookingChartDataDTO chartData = bookingService.getBookingChartData("Quarterly", 2025);
        assertNotNull(chartData);
    }

    @Test
    void testSearchAvailableVehiclesWithDriver() {
        CreateBookingRequestDTO request = new CreateBookingRequestDTO();
        request.setPickUpLocation("Jakarta");
        request.setDropOffLocation("Bandung");
        request.setPickUpTime(LocalDateTime.now().plusDays(10));
        request.setDropOffTime(LocalDateTime.now().plusDays(12));
        request.setCapacityNeeded(5);
        request.setTransmissionNeeded("Automatic");
        request.setIncludeDriver(true);

        SearchVehiclesResponseDTO response = bookingService.searchAvailableVehicles(request);
        assertNotNull(response);
    }

    @Test
    void testGetBookingsByStatusConfirmed() {
        List<RentalBooking> bookings = bookingService.getBookingsByStatus("Confirmed");
        assertNotNull(bookings);
    }

    @Test
    void testGetBookingsByStatusInProgress() {
        List<RentalBooking> bookings = bookingService.getBookingsByStatus("In Progress");
        assertNotNull(bookings);
    }
}
