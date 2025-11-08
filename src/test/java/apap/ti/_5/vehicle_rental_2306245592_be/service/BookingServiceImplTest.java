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

    // @Test
    // void testGenerateBookingId() {
    //     String bookingId = bookingService.generateBookingId();
    //     assertNotNull(bookingId);
    //     assertTrue(bookingId.startsWith("BKG-"));
    // }

    // @Test
    // void testUpdateBookingStatus() {
    //     UpdateBookingStatusDTO statusDTO = new UpdateBookingStatusDTO();
    //     statusDTO.setBookingId(testBooking.getId());
    //     statusDTO.setNewStatus("Confirmed");

    //     RentalBooking updatedBooking = bookingService.updateBookingStatus(testBooking.getId(), statusDTO);
    //     assertNotNull(updatedBooking);
    //     assertEquals("Confirmed", updatedBooking.getStatus());
    // }

    // @Test
    // void testCancelBooking() {
    //     RentalBooking cancelledBooking = bookingService.cancelBooking(testBooking.getId());
    //     assertNotNull(cancelledBooking);
    // }

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

    // @Test
    // void testGetBookingForUpdate() {
    //     Optional<RentalBooking> booking = bookingService.getBookingForUpdate(testBooking.getId());
    //     assertTrue(booking.isPresent());
    // }

    // @Test
    // void testUpdateBookingDetails() {
    //     UpdateBookingRequestDTO updateDTO = new UpdateBookingRequestDTO();
    //     updateDTO.setId(testBooking.getId());
    //     updateDTO.setPickUpLocation("Surabaya");
    //     updateDTO.setDropOffLocation("Malang");
    //     updateDTO.setPickUpTime(LocalDateTime.now().plusDays(2));
    //     updateDTO.setDropOffTime(LocalDateTime.now().plusDays(4));
    //     updateDTO.setCapacityNeeded(5);
    //     updateDTO.setTransmissionNeeded("Manual");
    //     updateDTO.setIncludeDriver(false);

    //     RentalBooking updated = bookingService.updateBookingDetails(testBooking.getId(), updateDTO);
    //     assertNotNull(updated);
    // }

    @Test
    void testGetBookingForUpdateStatus() {
        Optional<RentalBooking> booking = bookingService.getBookingForUpdateStatus(testBooking.getId());
        assertTrue(booking.isPresent());
    }

    // @Test
    // void testGetBookingForUpdateAddOns() {
    //     Optional<RentalBooking> booking = bookingService.getBookingForUpdateAddOns(testBooking.getId());
    //     assertTrue(booking.isPresent());
    // }

    // @Test
    // void testUpdateBookingAddOns() {
    //     UpdateAddOnsRequestDTO updateDTO = new UpdateAddOnsRequestDTO();
    //     updateDTO.setBookingId(testBooking.getId());
    //     updateDTO.setSelectedAddOnIds(Arrays.asList("1", "2"));

    //     RentalBooking updated = bookingService.updateBookingAddOns(testBooking.getId(), updateDTO);
    //     assertNotNull(updated);
    // }

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

    @Test
    void testSearchAvailableVehiclesNearbyOnly() {
        CreateBookingRequestDTO request = new CreateBookingRequestDTO();
        request.setPickUpLocation("Jakarta");
        request.setDropOffLocation("Jakarta"); // Same location
        request.setPickUpTime(LocalDateTime.now().plusDays(15));
        request.setDropOffTime(LocalDateTime.now().plusDays(17));
        request.setCapacityNeeded(5);
        request.setTransmissionNeeded("Manual");
        request.setIncludeDriver(false);

        SearchVehiclesResponseDTO response = bookingService.searchAvailableVehicles(request);
        assertNotNull(response);
    }

    @Test
    void testSearchAvailableVehiclesHighCapacity() {
        CreateBookingRequestDTO request = new CreateBookingRequestDTO();
        request.setPickUpLocation("Jakarta");
        request.setDropOffLocation("Bandung");
        request.setPickUpTime(LocalDateTime.now().plusDays(20));
        request.setDropOffTime(LocalDateTime.now().plusDays(22));
        request.setCapacityNeeded(7);
        request.setTransmissionNeeded("Automatic");
        request.setIncludeDriver(true);

        SearchVehiclesResponseDTO response = bookingService.searchAvailableVehicles(request);
        assertNotNull(response);
        assertNotNull(response.getAvailableVehicles());
    }

    @Test
    void testSearchAvailableVehiclesLowCapacity() {
        CreateBookingRequestDTO request = new CreateBookingRequestDTO();
        request.setPickUpLocation("Jakarta");
        request.setDropOffLocation("Surabaya");
        request.setPickUpTime(LocalDateTime.now().plusDays(25));
        request.setDropOffTime(LocalDateTime.now().plusDays(27));
        request.setCapacityNeeded(2);
        request.setTransmissionNeeded("Automatic");
        request.setIncludeDriver(false);

        SearchVehiclesResponseDTO response = bookingService.searchAvailableVehicles(request);
        assertNotNull(response);
    }

    // @Test
    // void testCreateBookingWithAddOnsEmptyList() {
    //     CreateBookingRequestDTO bookingDTO = new CreateBookingRequestDTO();
    //     bookingDTO.setPickUpLocation("Jakarta");
    //     bookingDTO.setDropOffLocation("Bandung");
    //     bookingDTO.setPickUpTime(LocalDateTime.now().plusDays(30));
    //     bookingDTO.setDropOffTime(LocalDateTime.now().plusDays(32));
    //     bookingDTO.setIncludeDriver(false);
    //     bookingDTO.setCapacityNeeded(5);
    //     bookingDTO.setTransmissionNeeded("Automatic");

    //     apap.ti._5.vehicle_rental_2306245592_be.restdto.request.booking.AddAddOnsRequestDTO addOnsDTO = 
    //         new apap.ti._5.vehicle_rental_2306245592_be.restdto.request.booking.AddAddOnsRequestDTO();
    //     addOnsDTO.setVehicleId(testVehicle.getId());
    //     addOnsDTO.setSelectedAddOnIds(new java.util.ArrayList<>());

    //     RentalBooking booking = bookingService.createBookingWithAddOns(bookingDTO, addOnsDTO);
    //     assertNotNull(booking);
    //     assertNotNull(booking.getId());
    //     assertEquals("Pending", booking.getStatus());
    // }

    @Test
    void testGetBookingCountPositive() {
        int count = bookingService.getBookingCount();
        assertTrue(count >= 0);
    }

    // @Test
    // void testGetAllRentalAddOns() {
    //     List<RentalAddOn> addOns = bookingService.getAllRentalAddOns();
    //     assertNotNull(addOns);
    // }

    @Test
    void testGetAllProvincesNotEmpty() {
        List<String> provinces = bookingService.getAllProvinces();
        assertNotNull(provinces);
        assertFalse(provinces.isEmpty());
        assertTrue(provinces.size() > 0);
    }

    @Test
    void testGetBookingChartDataMonthlyCurrentYear() {
        BookingChartDataDTO chartData = bookingService.getBookingChartData("Monthly", java.time.LocalDate.now().getYear());
        assertNotNull(chartData);
        assertNotNull(chartData.getLabels());
        // assertNotNull(chartData.getCounts());
    }

    @Test
    void testGetBookingChartDataMonthlyPreviousYear() {
        BookingChartDataDTO chartData = bookingService.getBookingChartData("Monthly", java.time.LocalDate.now().getYear() - 1);
        assertNotNull(chartData);
    }

    @Test
    void testGetBookingChartDataQuarterlyCurrentYear() {
        BookingChartDataDTO chartData = bookingService.getBookingChartData("Quarterly", java.time.LocalDate.now().getYear());
        assertNotNull(chartData);
        assertNotNull(chartData.getLabels());
    }

    @Test
    void testGetBookingsByVehicleIdEmpty() {
        List<RentalBooking> bookings = bookingService.getBookingsByVehicleId("NON-EXISTENT-ID");
        assertNotNull(bookings);
    }

    @Test
    void testGetBookingsByStatusPending() {
        List<RentalBooking> bookings = bookingService.getBookingsByStatus("Pending");
        assertNotNull(bookings);
    }

    @Test
    void testGetBookingsByStatusCompleted() {
        List<RentalBooking> bookings = bookingService.getBookingsByStatus("Completed");
        assertNotNull(bookings);
    }

    @Test
    void testGetBookingsByStatusCancelled() {
        List<RentalBooking> bookings = bookingService.getBookingsByStatus("Cancelled");
        assertNotNull(bookings);
    }

    @Test
    void testGetAvailableStatusTransitionsFromPending() {
        List<String> transitions = bookingService.getAvailableStatusTransitions("Pending", testBooking.getId());
        assertNotNull(transitions);
    }

    @Test
    void testGetAvailableStatusTransitionsFromConfirmed() {
        List<String> transitions = bookingService.getAvailableStatusTransitions("Confirmed", testBooking.getId());
        assertNotNull(transitions);
    }

    @Test
    void testGetAvailableStatusTransitionsFromInProgress() {
        List<String> transitions = bookingService.getAvailableStatusTransitions("In Progress", testBooking.getId());
        assertNotNull(transitions);
    }

    @Test
    void testGetBookingForUpdateStatusExists() {
        Optional<RentalBooking> booking = bookingService.getBookingForUpdateStatus(testBooking.getId());
        assertTrue(booking.isPresent());
    }

    // @Test
    // void testGetBookingForUpdateStatusNotFound() {
    //     Optional<RentalBooking> booking = bookingService.getBookingForUpdateStatus("NON-EXISTENT");
    //     assertFalse(booking.isPresent());
    // }

    @Test
    void testCreateBookingDirectly() {
        RentalBooking newBooking = new RentalBooking();
        newBooking.setId("BKG-NEW-" + System.currentTimeMillis());
        newBooking.setVehicle(testVehicle);
        newBooking.setPickUpLocation("Surabaya");
        newBooking.setDropOffLocation("Malang");
        newBooking.setPickUpTime(LocalDateTime.now().plusDays(5));
        newBooking.setDropOffTime(LocalDateTime.now().plusDays(7));
        newBooking.setTotalPrice(1600000.0);
        newBooking.setStatus("Pending");
        newBooking.setIncludeDriver(false);
        newBooking.setCapacityNeeded(5);
        newBooking.setTransmissionNeeded("Automatic");

        RentalBooking created = bookingService.createBooking(newBooking);
        assertNotNull(created);
        assertNotNull(created.getId());
    }

    @Test
    void testUpdateBookingDirectly() {
        testBooking.setPickUpLocation("Surabaya");
        testBooking.setDropOffLocation("Malang");
        
        RentalBooking updated = bookingService.updateBooking(testBooking.getId(), testBooking);
        assertNotNull(updated);
        assertEquals("Surabaya", updated.getPickUpLocation());
    }

    @Test
    void testUpdateBookingNotFound() {
        RentalBooking newBooking = new RentalBooking();
        newBooking.setId("NON-EXISTENT");
        
        try {
            bookingService.updateBooking("NON-EXISTENT", newBooking);
            fail("Should throw exception");
        } catch (RuntimeException e) {
            assertTrue(e.getMessage().contains("not found"));
        }
    }

    @Test
    void testDeleteBookingNotFound() {
        try {
            bookingService.deleteBooking("NON-EXISTENT");
            fail("Should throw exception");
        } catch (RuntimeException e) {
            assertTrue(e.getMessage().contains("not found"));
        }
    }

    @Test
    void testGetBookingByIdNotFound() {
        Optional<RentalBooking> booking = bookingService.getBookingById("NON-EXISTENT");
        assertFalse(booking.isPresent());
    }

    @Test
    void testSearchAvailableVehiclesShortDuration() {
        CreateBookingRequestDTO request = new CreateBookingRequestDTO();
        request.setPickUpLocation("Jakarta");
        request.setDropOffLocation("Jakarta");
        request.setPickUpTime(LocalDateTime.now().plusDays(40));
        request.setDropOffTime(LocalDateTime.now().plusDays(41)); // 1 day
        request.setCapacityNeeded(4);
        request.setTransmissionNeeded("Manual");
        request.setIncludeDriver(false);

        SearchVehiclesResponseDTO response = bookingService.searchAvailableVehicles(request);
        assertNotNull(response);
    }

    @Test
    void testSearchAvailableVehiclesLongDuration() {
        CreateBookingRequestDTO request = new CreateBookingRequestDTO();
        request.setPickUpLocation("Jakarta");
        request.setDropOffLocation("Bali");
        request.setPickUpTime(LocalDateTime.now().plusDays(45));
        request.setDropOffTime(LocalDateTime.now().plusDays(55)); // 10 days
        request.setCapacityNeeded(6);
        request.setTransmissionNeeded("Automatic");
        request.setIncludeDriver(true);

        SearchVehiclesResponseDTO response = bookingService.searchAvailableVehicles(request);
        assertNotNull(response);
        // assertNotNull(response.getPickUpLocations());
        // assertNotNull(response.getDropOffLocations());
    }
}
