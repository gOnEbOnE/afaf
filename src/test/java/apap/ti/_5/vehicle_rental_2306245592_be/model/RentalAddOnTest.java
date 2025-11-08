package apap.ti._5.vehicle_rental_2306245592_be.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class RentalAddOnTest {

    private RentalAddOn rentalAddOn;
    private RentalBooking booking1;
    private RentalBooking booking2;

    @BeforeEach
    void setUp() {
        // Setup RentalBookings
        booking1 = new RentalBooking();
        booking1.setId("BOOK001");
        booking1.setStatus("Upcoming");

        booking2 = new RentalBooking();
        booking2.setId("BOOK002");
        booking2.setStatus("Ongoing");

        // Setup RentalAddOn
        rentalAddOn = new RentalAddOn();
    }

    @Test
    void testRentalAddOnConstructor() {
        // Test no-args constructor
        RentalAddOn addOn = new RentalAddOn();
        assertNotNull(addOn);
    }

    @Test
    void testRentalAddOnAllArgsConstructor() {
        // Test all-args constructor
        UUID id = UUID.randomUUID();
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();
        List<RentalBooking> bookings = Arrays.asList(booking1, booking2);

        RentalAddOn addOn = new RentalAddOn(
            id,
            "GPS Navigation",
            25000.0,
            bookings,
            createdAt,
            updatedAt
        );

        assertEquals(id, addOn.getId());
        assertEquals("GPS Navigation", addOn.getName());
        assertEquals(25000.0, addOn.getPrice());
        assertEquals(bookings, addOn.getListOfBookings());
        assertEquals(createdAt, addOn.getCreatedAt());
        assertEquals(updatedAt, addOn.getUpdatedAt());
    }

    @Test
    void testSettersAndGetters() {
        UUID id = UUID.randomUUID();
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();
        List<RentalBooking> bookings = Arrays.asList(booking1, booking2);

        rentalAddOn.setId(id);
        rentalAddOn.setName("Child Safety Seat");
        rentalAddOn.setPrice(15000.0);
        rentalAddOn.setListOfBookings(bookings);
        rentalAddOn.setCreatedAt(createdAt);
        rentalAddOn.setUpdatedAt(updatedAt);

        assertEquals(id, rentalAddOn.getId());
        assertEquals("Child Safety Seat", rentalAddOn.getName());
        assertEquals(15000.0, rentalAddOn.getPrice());
        assertEquals(bookings, rentalAddOn.getListOfBookings());
        assertEquals(createdAt, rentalAddOn.getCreatedAt());
        assertEquals(updatedAt, rentalAddOn.getUpdatedAt());
    }

    @Test
    void testAddOnNames() {
        String[] validNames = {
            "GPS Navigation",
            "Child Safety Seat",
            "Additional Driver",
            "Insurance Coverage",
            "WiFi Hotspot",
            "Phone Charger",
            "Cooler Box",
            "Extra Luggage Space"
        };
        
        for (String name : validNames) {
            rentalAddOn.setName(name);
            assertEquals(name, rentalAddOn.getName());
        }
    }

    @Test
    void testAddOnPrices() {
        Double[] validPrices = {
            0.0,      // Free add-on
            5000.0,   // Cheap add-on
            15000.0,  // Medium add-on
            25000.0,  // Expensive add-on
            50000.0,  // Premium add-on
            99999.99  // Max price
        };
        
        for (Double price : validPrices) {
            rentalAddOn.setPrice(price);
            assertEquals(price, rentalAddOn.getPrice());
        }
    }

    @Test
    void testBookingsList() {
        // Test with empty list
        rentalAddOn.setListOfBookings(new ArrayList<>());
        assertNotNull(rentalAddOn.getListOfBookings());
        assertEquals(0, rentalAddOn.getListOfBookings().size());

        // Test adding bookings
        rentalAddOn.getListOfBookings().add(booking1);
        rentalAddOn.getListOfBookings().add(booking2);
        assertEquals(2, rentalAddOn.getListOfBookings().size());
        assertTrue(rentalAddOn.getListOfBookings().contains(booking1));
        assertTrue(rentalAddOn.getListOfBookings().contains(booking2));

        // Test with null list
        rentalAddOn.setListOfBookings(null);
        assertNull(rentalAddOn.getListOfBookings());
    }

    @Test
    void testTimeStamps() {
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now().plusMinutes(1);

        rentalAddOn.setCreatedAt(createdAt);
        rentalAddOn.setUpdatedAt(updatedAt);

        assertEquals(createdAt, rentalAddOn.getCreatedAt());
        assertEquals(updatedAt, rentalAddOn.getUpdatedAt());
        assertTrue(rentalAddOn.getUpdatedAt().isAfter(rentalAddOn.getCreatedAt()));
    }

    @Test
    void testUUIDGeneration() {
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();

        assertNotEquals(id1, id2);

        rentalAddOn.setId(id1);
        assertEquals(id1, rentalAddOn.getId());

        RentalAddOn anotherAddOn = new RentalAddOn();
        anotherAddOn.setId(id2);
        assertEquals(id2, anotherAddOn.getId());

        assertNotEquals(rentalAddOn.getId(), anotherAddOn.getId());
    }

    @Test
    void testAddOnDescriptiveNames() {
        // Test meaningful add-on names
        rentalAddOn.setName("Premium GPS Navigation System");
        assertEquals("Premium GPS Navigation System", rentalAddOn.getName());

        rentalAddOn.setName("Forward-Facing Child Safety Seat (2-4 years)");
        assertEquals("Forward-Facing Child Safety Seat (2-4 years)", rentalAddOn.getName());

        rentalAddOn.setName("Comprehensive Insurance Coverage");
        assertEquals("Comprehensive Insurance Coverage", rentalAddOn.getName());
    }

    @Test
    void testAddOnPriceEdgeCases() {
        // Test minimum price (free)
        rentalAddOn.setPrice(0.0);
        assertEquals(0.0, rentalAddOn.getPrice());

        // Test very small price
        rentalAddOn.setPrice(0.01);
        assertEquals(0.01, rentalAddOn.getPrice());

        // Test decimal precision
        rentalAddOn.setPrice(12345.67);
        assertEquals(12345.67, rentalAddOn.getPrice());
    }

    @Test
    void testBookingAssociations() {
        List<RentalBooking> bookings = Arrays.asList(booking1, booking2);
        rentalAddOn.setListOfBookings(bookings);

        assertEquals(2, rentalAddOn.getListOfBookings().size());
        assertEquals("BOOK001", rentalAddOn.getListOfBookings().get(0).getId());
        assertEquals("BOOK002", rentalAddOn.getListOfBookings().get(1).getId());
        assertEquals("Upcoming", rentalAddOn.getListOfBookings().get(0).getStatus());
        assertEquals("Ongoing", rentalAddOn.getListOfBookings().get(1).getStatus());
    }

    @Test
    void testMultipleAddOnsForSameBooking() {
        // Create multiple add-ons
        RentalAddOn gps = new RentalAddOn();
        gps.setName("GPS Navigation");
        gps.setPrice(25000.0);

        RentalAddOn childSeat = new RentalAddOn();
        childSeat.setName("Child Safety Seat");
        childSeat.setPrice(15000.0);

        RentalAddOn insurance = new RentalAddOn();
        insurance.setName("Insurance Coverage");
        insurance.setPrice(50000.0);

        // Both add-ons can reference the same booking
        List<RentalBooking> sameBookingList = Arrays.asList(booking1);
        
        gps.setListOfBookings(sameBookingList);
        childSeat.setListOfBookings(sameBookingList);
        insurance.setListOfBookings(sameBookingList);

        assertEquals(booking1, gps.getListOfBookings().get(0));
        assertEquals(booking1, childSeat.getListOfBookings().get(0));
        assertEquals(booking1, insurance.getListOfBookings().get(0));
    }

    @Test
    void testNullValues() {
        // Test that getters return null when not set
        assertNull(rentalAddOn.getId());
        assertNull(rentalAddOn.getName());
        assertNull(rentalAddOn.getPrice());
        assertNull(rentalAddOn.getListOfBookings());
        assertNull(rentalAddOn.getCreatedAt());
        assertNull(rentalAddOn.getUpdatedAt());
    }
}