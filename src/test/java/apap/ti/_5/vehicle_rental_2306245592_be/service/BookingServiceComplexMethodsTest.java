package apap.ti._5.vehicle_rental_2306245592_be.service;

import apap.ti._5.vehicle_rental_2306245592_be.model.RentalBooking;
import apap.ti._5.vehicle_rental_2306245592_be.model.Vehicle;
import apap.ti._5.vehicle_rental_2306245592_be.model.RentalAddOn;
import apap.ti._5.vehicle_rental_2306245592_be.repository.RentalBookingRepository;
import apap.ti._5.vehicle_rental_2306245592_be.repository.VehicleRepository;
import apap.ti._5.vehicle_rental_2306245592_be.repository.RentalAddOnRepository;
import apap.ti._5.vehicle_rental_2306245592_be.restdto.request.booking.UpdateBookingRequestDTO;
import apap.ti._5.vehicle_rental_2306245592_be.restdto.request.booking.UpdateBookingStatusDTO;
import apap.ti._5.vehicle_rental_2306245592_be.restdto.request.booking.UpdateAddOnsRequestDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class BookingServiceComplexMethodsTest {

    @Autowired
    private BookingServiceImpl bookingService;

    @Autowired
    private RentalBookingRepository bookingRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private RentalAddOnRepository addOnRepository;

    private Vehicle testVehicle;
    private RentalBooking testBooking;
    private RentalAddOn testAddOn;

    @BeforeEach
    void setUp() {
        // Ambil vehicle yang sudah ada
        testVehicle = vehicleRepository.findAll().stream().findFirst().orElse(null);
        assertNotNull(testVehicle, "Test requires at least one vehicle in database");

        // Ambil addOn yang sudah ada
        List<RentalAddOn> addOns = addOnRepository.findAll();
        testAddOn = addOns.isEmpty() ? null : addOns.get(0);
    }

    @Test
    void testGenerateBookingId() {
        String id1 = bookingService.generateBookingId();
        String id2 = bookingService.generateBookingId();

        assertNotNull(id1);
        assertNotNull(id2);
        assertTrue(id1.startsWith("VR"));
        assertTrue(id2.startsWith("VR"));
        assertEquals(8, id1.length());
        assertEquals(8, id2.length());
    }

    @Test
    void testGenerateBookingIdIncrementsSequentially() {
        // Create a booking to establish baseline
        RentalBooking booking = createAndSaveTestBooking("VR000100");
        
        String nextId = bookingService.generateBookingId();
        
        assertTrue(nextId.compareTo("VR000100") > 0);
        assertEquals(8, nextId.length());
    }

    // @Test
    // void testUpdateBookingDetailsSuccess() {
    //     // Create test booking dengan status Upcoming
    //     RentalBooking booking = createAndSaveTestBooking(null);
    //     booking.setStatus("Upcoming");
    //     booking = bookingRepository.save(booking);

    //     UpdateBookingRequestDTO updateDTO = new UpdateBookingRequestDTO();
    //     updateDTO.setPickUpTime(LocalDateTime.now().plusDays(2));
    //     updateDTO.setDropOffTime(LocalDateTime.now().plusDays(5));
    //     updateDTO.setPickUpLocation(testVehicle.getLocation());
    //     updateDTO.setDropOffLocation("New Drop Location");
    //     updateDTO.setIncludeDriver(true);
    //     updateDTO.setCapacityNeeded(5);
    //     updateDTO.setTransmissionNeeded("Manual");

    //     RentalBooking updated = bookingService.updateBookingDetails(booking.getId(), updateDTO);

    //     assertNotNull(updated);
    //     assertEquals("Upcoming", updated.getStatus());
    //     assertEquals("New Drop Location", updated.getDropOffLocation());
    // }

    @Test
    void testUpdateBookingDetailsWithInvalidStatus() {
        RentalBooking booking = createAndSaveTestBooking(null);
        booking.setStatus("Ongoing");
        booking = bookingRepository.save(booking);
        final String bookingId = booking.getId();

        UpdateBookingRequestDTO updateDTO = new UpdateBookingRequestDTO();
        updateDTO.setPickUpTime(LocalDateTime.now().plusDays(1));
        updateDTO.setDropOffTime(LocalDateTime.now().plusDays(3));
        updateDTO.setPickUpLocation(testVehicle.getLocation());
        updateDTO.setDropOffLocation("Location");
        updateDTO.setIncludeDriver(false);
        updateDTO.setCapacityNeeded(5);
        updateDTO.setTransmissionNeeded("Manual");

        assertThrows(RuntimeException.class, () -> 
            bookingService.updateBookingDetails(bookingId, updateDTO)
        );
    }

    @Test
    void testUpdateBookingDetailsWithPastPickupTime() {
        RentalBooking booking = createAndSaveTestBooking(null);
        booking.setStatus("Upcoming");
        booking = bookingRepository.save(booking);
        final String bookingId = booking.getId();

        UpdateBookingRequestDTO updateDTO = new UpdateBookingRequestDTO();
        updateDTO.setPickUpTime(LocalDateTime.now().minusHours(1)); // Past time
        updateDTO.setDropOffTime(LocalDateTime.now().plusDays(3));
        updateDTO.setPickUpLocation(testVehicle.getLocation());
        updateDTO.setDropOffLocation("Location");
        updateDTO.setIncludeDriver(false);
        updateDTO.setCapacityNeeded(5);
        updateDTO.setTransmissionNeeded("Manual");

        assertThrows(RuntimeException.class, () -> 
            bookingService.updateBookingDetails(bookingId, updateDTO)
        );
    }

    @Test
    void testUpdateBookingDetailsWithInvalidTimeRange() {
        RentalBooking booking = createAndSaveTestBooking(null);
        booking.setStatus("Upcoming");
        booking = bookingRepository.save(booking);
        final String bookingId = booking.getId();

        UpdateBookingRequestDTO updateDTO = new UpdateBookingRequestDTO();
        updateDTO.setPickUpTime(LocalDateTime.now().plusDays(5));
        updateDTO.setDropOffTime(LocalDateTime.now().plusDays(3)); // Before pickup
        updateDTO.setPickUpLocation(testVehicle.getLocation());
        updateDTO.setDropOffLocation("Location");
        updateDTO.setIncludeDriver(false);
        updateDTO.setCapacityNeeded(5);
        updateDTO.setTransmissionNeeded("Manual");

        assertThrows(RuntimeException.class, () -> 
            bookingService.updateBookingDetails(bookingId, updateDTO)
        );
    }

    @Test
    void testGetBookingForUpdate() {
        RentalBooking booking = createAndSaveTestBooking(null);
        booking.setStatus("Upcoming");
        booking = bookingRepository.save(booking);

        Optional<RentalBooking> result = bookingService.getBookingForUpdate(booking.getId());

        assertTrue(result.isPresent());
        assertEquals(booking.getId(), result.get().getId());
    }

    @Test
    void testGetBookingForUpdateWithInvalidStatus() {
        RentalBooking booking = createAndSaveTestBooking(null);
        booking.setStatus("Ongoing");
        booking = bookingRepository.save(booking);
        final String bookingId = booking.getId();

        assertThrows(RuntimeException.class, () -> 
            bookingService.getBookingForUpdate(bookingId)
        );
    }

    @Test
    void testGetBookingForUpdateNotFound() {
        assertThrows(RuntimeException.class, () -> 
            bookingService.getBookingForUpdate("NON-EXISTENT-ID")
        );
    }

    @Test
    void testGetBookingForUpdateStatus() {
        RentalBooking booking = createAndSaveTestBooking(null);
        booking.setStatus("Upcoming");
        booking = bookingRepository.save(booking);

        Optional<RentalBooking> result = bookingService.getBookingForUpdateStatus(booking.getId());

        assertTrue(result.isPresent());
        assertEquals(booking.getId(), result.get().getId());
    }

    @Test
    void testUpdateBookingStatusUpcomingToOngoing() {
        RentalBooking booking = createAndSaveTestBooking(null);
        booking.setStatus("Upcoming");
        booking.setPickUpTime(LocalDateTime.now().minusHours(1));
        booking.setDropOffTime(LocalDateTime.now().plusDays(3));
        booking.setPickUpLocation(testVehicle.getLocation());
        booking = bookingRepository.save(booking);

        // Set vehicle as available
        testVehicle.setStatus("Available");
        testVehicle.setLocation(booking.getPickUpLocation());
        vehicleRepository.save(testVehicle);

        UpdateBookingStatusDTO statusDTO = new UpdateBookingStatusDTO();
        statusDTO.setNewStatus("Ongoing");

        RentalBooking updated = bookingService.updateBookingStatus(booking.getId(), statusDTO);

        assertEquals("Ongoing", updated.getStatus());
    }

    @Test
    void testUpdateBookingStatusOngoingToDone() {
        RentalBooking booking = createAndSaveTestBooking(null);
        booking.setStatus("Ongoing");
        booking.setDropOffTime(LocalDateTime.now().minusHours(1)); // Already passed
        booking = bookingRepository.save(booking);

        UpdateBookingStatusDTO statusDTO = new UpdateBookingStatusDTO();
        statusDTO.setNewStatus("Done");

        RentalBooking updated = bookingService.updateBookingStatus(booking.getId(), statusDTO);

        assertEquals("Done", updated.getStatus());
    }

    @Test
    void testUpdateBookingStatusWithInvalidTransition() {
        RentalBooking booking = createAndSaveTestBooking(null);
        booking.setStatus("Done");
        booking = bookingRepository.save(booking);
        final String bookingId = booking.getId();

        UpdateBookingStatusDTO statusDTO = new UpdateBookingStatusDTO();
        statusDTO.setNewStatus("Upcoming");

        assertThrows(RuntimeException.class, () -> 
            bookingService.updateBookingStatus(bookingId, statusDTO)
        );
    }

    // @Test
    // void testUpdateBookingStatusUpcomingToCancelled() {
    //     RentalBooking booking = createAndSaveTestBooking(null);
    //     booking.setStatus("Upcoming");
    //     booking = bookingRepository.save(booking);

    //     UpdateBookingStatusDTO statusDTO = new UpdateBookingStatusDTO();
    //     statusDTO.setNewStatus("Cancelled");

    //     RentalBooking updated = bookingService.updateBookingStatus(booking.getId(), statusDTO);

    //     assertEquals("Cancelled", updated.getStatus());
    // }

    @Test
    void testGetBookingForUpdateAddOns() {
        RentalBooking booking = createAndSaveTestBooking(null);
        booking.setStatus("Upcoming");
        booking = bookingRepository.save(booking);

        Optional<RentalBooking> result = bookingService.getBookingForUpdateAddOns(booking.getId());

        assertTrue(result.isPresent());
        assertEquals(booking.getId(), result.get().getId());
    }

    @Test
    void testGetBookingForUpdateAddOnsWithInvalidStatus() {
        RentalBooking booking = createAndSaveTestBooking(null);
        booking.setStatus("Ongoing");
        booking = bookingRepository.save(booking);
        final String bookingId = booking.getId();

        assertThrows(RuntimeException.class, () -> 
            bookingService.getBookingForUpdateAddOns(bookingId)
        );
    }

    @Test
    void testUpdateBookingAddOnsSuccess() {
        if (testAddOn == null) {
            return; // Skip if no addons available
        }

        RentalBooking booking = createAndSaveTestBooking(null);
        booking.setStatus("Upcoming");
        booking = bookingRepository.save(booking);

        UpdateAddOnsRequestDTO updateDTO = new UpdateAddOnsRequestDTO();
        updateDTO.setSelectedAddOnIds(List.of(testAddOn.getId().toString()));

        RentalBooking updated = bookingService.updateBookingAddOns(booking.getId(), updateDTO);

        assertNotNull(updated);
        assertEquals("Upcoming", updated.getStatus());
    }

    @Test
    void testUpdateBookingAddOnsWithEmptyList() {
        RentalBooking booking = createAndSaveTestBooking(null);
        booking.setStatus("Upcoming");
        booking = bookingRepository.save(booking);

        UpdateAddOnsRequestDTO updateDTO = new UpdateAddOnsRequestDTO();
        updateDTO.setSelectedAddOnIds(new ArrayList<>());

        RentalBooking updated = bookingService.updateBookingAddOns(booking.getId(), updateDTO);

        assertNotNull(updated);
        assertEquals("Upcoming", updated.getStatus());
    }

    @Test
    void testUpdateBookingAddOnsWithInvalidStatus() {
        RentalBooking booking = createAndSaveTestBooking(null);
        booking.setStatus("Done");
        booking = bookingRepository.save(booking);
        final String bookingId = booking.getId();

        UpdateAddOnsRequestDTO updateDTO = new UpdateAddOnsRequestDTO();
        updateDTO.setSelectedAddOnIds(new ArrayList<>());

        assertThrows(RuntimeException.class, () -> 
            bookingService.updateBookingAddOns(bookingId, updateDTO)
        );
    }

    // @Test
    // void testGetAvailableStatusTransitionsFromUpcoming() {
    //     RentalBooking booking = createAndSaveTestBooking(null);
    //     booking.setStatus("Upcoming");
    //     booking.setPickUpTime(LocalDateTime.now().plusHours(2));
    //     booking = bookingRepository.save(booking);

    //     List<String> transitions = bookingService.getAvailableStatusTransitions("Upcoming", booking.getId());

    //     assertNotNull(transitions);
    //     assertTrue(transitions.contains("Cancelled"));
    // }

    @Test
    void testGetAvailableStatusTransitionsFromOngoing() {
        RentalBooking booking = createAndSaveTestBooking(null);
        booking.setStatus("Ongoing");
        booking = bookingRepository.save(booking);

        List<String> transitions = bookingService.getAvailableStatusTransitions("Ongoing", booking.getId());

        assertNotNull(transitions);
        assertTrue(transitions.contains("Done"));
    }

    @Test
    void testGetAvailableStatusTransitionsFromDone() {
        RentalBooking booking = createAndSaveTestBooking(null);
        booking.setStatus("Done");
        booking = bookingRepository.save(booking);

        List<String> transitions = bookingService.getAvailableStatusTransitions("Done", booking.getId());

        assertNotNull(transitions);
        assertTrue(transitions.isEmpty());
    }

    @Test
    void testGetAvailableStatusTransitionsFromCancelled() {
        RentalBooking booking = createAndSaveTestBooking(null);
        booking.setStatus("Cancelled");
        booking = bookingRepository.save(booking);

        List<String> transitions = bookingService.getAvailableStatusTransitions("Cancelled", booking.getId());

        assertNotNull(transitions);
        assertTrue(transitions.isEmpty());
    }

    // Helper method
    private RentalBooking createAndSaveTestBooking(String id) {
        RentalBooking booking = new RentalBooking();
        if (id != null) {
            booking.setId(id);
        } else {
            booking.setId("TEST-" + UUID.randomUUID().toString().substring(0, 8));
        }
        booking.setVehicle(testVehicle);
        booking.setPickUpTime(LocalDateTime.now().plusDays(1));
        booking.setDropOffTime(LocalDateTime.now().plusDays(3));
        booking.setPickUpLocation(testVehicle.getLocation());
        booking.setDropOffLocation("Test Drop Location");
        booking.setStatus("Pending");
        booking.setTotalPrice(1000000.0);
        booking.setDeletedAt(null);
        booking.setIncludeDriver(false);
        booking.setCapacityNeeded(5);
        booking.setTransmissionNeeded("Manual");
        return bookingRepository.save(booking);
    }
}
