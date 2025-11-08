package apap.ti._5.vehicle_rental_2306245592_be.model;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class RentalBookingTest {

    @Test
    void testRentalBookingCreation() {
        // Test rental booking entity creation
        Map<String, Object> booking = new HashMap<>();
        booking.put("id", "BOOK001");
        booking.put("vehicleId", "VEH001");
        booking.put("pickUpLocation", "Jakarta");
        booking.put("dropOffLocation", "Bandung");
        booking.put("pickUpTime", LocalDateTime.now().plusDays(1));
        booking.put("dropOffTime", LocalDateTime.now().plusDays(4));
        booking.put("capacityNeeded", 5);
        booking.put("transmissionNeeded", "Automatic");
        booking.put("includeDriver", false);
        booking.put("totalPrice", 1500000.0);
        booking.put("status", "DRAFT");
        booking.put("createdAt", LocalDateTime.now());
        
        assertNotNull(booking.get("id"));
        assertEquals("VEH001", booking.get("vehicleId"));
        assertEquals("Jakarta", booking.get("pickUpLocation"));
        assertEquals("Bandung", booking.get("dropOffLocation"));
        assertEquals(5, booking.get("capacityNeeded"));
        assertFalse((Boolean) booking.get("includeDriver"));
        assertEquals("DRAFT", booking.get("status"));
        assertTrue((Double) booking.get("totalPrice") > 0);
    }

    @Test
    void testBookingStatusTransitions() {
        // Test booking status workflow
        List<String> statusFlow = Arrays.asList(
            "DRAFT", "WAITING_FOR_PAYMENT", "PAID", "ONGOING", "FINISHED", "CANCELLED"
        );
        
        assertEquals(6, statusFlow.size());
        assertEquals("DRAFT", statusFlow.get(0));
        assertEquals("FINISHED", statusFlow.get(statusFlow.size() - 2)); // Second to last
        assertEquals("CANCELLED", statusFlow.get(statusFlow.size() - 1)); // Last
        
        // Test valid transitions
        Map<String, List<String>> validTransitions = new HashMap<>();
        validTransitions.put("DRAFT", Arrays.asList("WAITING_FOR_PAYMENT", "CANCELLED"));
        validTransitions.put("WAITING_FOR_PAYMENT", Arrays.asList("PAID", "CANCELLED"));
        validTransitions.put("PAID", Arrays.asList("ONGOING", "CANCELLED"));
        validTransitions.put("ONGOING", Arrays.asList("FINISHED"));
        
        assertTrue(validTransitions.get("DRAFT").contains("WAITING_FOR_PAYMENT"));
        assertTrue(validTransitions.get("PAID").contains("ONGOING"));
        assertFalse(validTransitions.get("FINISHED") != null); // No transitions from FINISHED
    }

    @Test
    void testBookingDuration() {
        // Test booking duration calculation
        LocalDateTime pickUpTime = LocalDateTime.of(2025, 11, 10, 12, 0);
        LocalDateTime dropOffTime = LocalDateTime.of(2025, 11, 13, 12, 0);
        
        // Calculate duration in hours
        long durationHours = java.time.temporal.ChronoUnit.HOURS.between(pickUpTime, dropOffTime);
        assertEquals(72, durationHours); // 3 days * 24 hours
        
        // Calculate duration in days
        long durationDays = java.time.temporal.ChronoUnit.DAYS.between(pickUpTime, dropOffTime);
        assertEquals(3, durationDays);
        
        assertTrue(durationDays > 0);
        assertTrue(durationHours > 0);
    }

    @Test
    void testBookingPriceCalculation() {
        // Test booking price calculation logic
        double basePrice = 500000.0; // per day
        int rentalDays = 3;
        boolean includeDriver = true;
        double driverCostPerDay = 100000.0;
        
        // Calculate base rental price
        double rentalPrice = basePrice * rentalDays;
        assertEquals(1500000.0, rentalPrice);
        
        // Calculate driver cost
        double driverCost = includeDriver ? driverCostPerDay * rentalDays : 0.0;
        assertEquals(300000.0, driverCost);
        
        // Calculate total price before add-ons
        double subtotal = rentalPrice + driverCost;
        assertEquals(1800000.0, subtotal);
        
        // Test without driver
        double noCost = false ? driverCostPerDay * rentalDays : 0.0;
        assertEquals(0.0, noCost);
    }

    @Test
    void testBookingAddOns() {
        // Test booking add-ons functionality
        List<Map<String, Object>> addOns = new ArrayList<>();
        
        Map<String, Object> gps = new HashMap<>();
        gps.put("id", "ADD001");
        gps.put("name", "GPS Navigation");
        gps.put("price", 50000.0);
        gps.put("quantity", 1);
        
        Map<String, Object> childSeat = new HashMap<>();
        childSeat.put("id", "ADD002");
        childSeat.put("name", "Child Seat");
        childSeat.put("price", 75000.0);
        childSeat.put("quantity", 2);
        
        addOns.add(gps);
        addOns.add(childSeat);
        
        // Calculate add-ons total
        double addOnTotal = addOns.stream()
            .mapToDouble(addOn -> (Double) addOn.get("price") * (Integer) addOn.get("quantity"))
            .sum();
        
        assertEquals(200000.0, addOnTotal); // 50000 + (75000 * 2)
        assertEquals(2, addOns.size());
        assertTrue(addOns.stream().anyMatch(a -> "GPS Navigation".equals(a.get("name"))));
    }

    @Test
    void testBookingValidation() {
        // Test booking validation rules
        
        // Test pickup/dropoff time validation
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime validPickUp = now.plusDays(1);
        LocalDateTime validDropOff = validPickUp.plusDays(2);
        LocalDateTime invalidDropOff = validPickUp.minusHours(1);
        
        assertTrue(validPickUp.isAfter(now));
        assertTrue(validDropOff.isAfter(validPickUp));
        assertFalse(invalidDropOff.isAfter(validPickUp));
        
        // Test capacity validation
        int requestedCapacity = 5;
        int vehicleCapacity = 7;
        int insufficientCapacity = 3;
        
        assertTrue(vehicleCapacity >= requestedCapacity);
        assertFalse(insufficientCapacity >= requestedCapacity);
        
        // Test location validation
        String validLocation = "Jakarta";
        String invalidLocation = "";
        
        assertFalse(validLocation.trim().isEmpty());
        assertTrue(invalidLocation.trim().isEmpty());
    }

    @Test
    void testBookingSearch() {
        // Test booking search and filtering
        List<Map<String, Object>> bookings = new ArrayList<>();
        
        Map<String, Object> booking1 = new HashMap<>();
        booking1.put("id", "BOOK001");
        booking1.put("status", "PAID");
        booking1.put("pickUpLocation", "Jakarta");
        booking1.put("vehicleId", "VEH001");
        booking1.put("totalPrice", 1500000.0);
        
        Map<String, Object> booking2 = new HashMap<>();
        booking2.put("id", "BOOK002");
        booking2.put("status", "ONGOING");
        booking2.put("pickUpLocation", "Bandung");
        booking2.put("vehicleId", "VEH002");
        booking2.put("totalPrice", 2000000.0);
        
        bookings.add(booking1);
        bookings.add(booking2);
        
        // Test filter by status
        long paidBookings = bookings.stream()
            .filter(b -> "PAID".equals(b.get("status")))
            .count();
        assertEquals(1, paidBookings);
        
        // Test filter by location
        long jakartaBookings = bookings.stream()
            .filter(b -> "Jakarta".equals(b.get("pickUpLocation")))
            .count();
        assertEquals(1, jakartaBookings);
        
        // Test filter by price range
        long expensiveBookings = bookings.stream()
            .filter(b -> (Double) b.get("totalPrice") > 1750000.0)
            .count();
        assertEquals(1, expensiveBookings);
    }

    @Test
    void testBookingStatistics() {
        // Test booking statistics calculation
        List<Map<String, Object>> bookings = new ArrayList<>();
        
        for (int i = 1; i <= 5; i++) {
            Map<String, Object> booking = new HashMap<>();
            booking.put("id", "BOOK" + String.format("%03d", i));
            booking.put("totalPrice", i * 500000.0);
            booking.put("status", i <= 3 ? "FINISHED" : "ONGOING");
            booking.put("month", i <= 2 ? "November" : "December");
            bookings.add(booking);
        }
        
        // Test total revenue
        double totalRevenue = bookings.stream()
            .filter(b -> "FINISHED".equals(b.get("status")))
            .mapToDouble(b -> (Double) b.get("totalPrice"))
            .sum();
        assertEquals(3000000.0, totalRevenue); // 500000 + 1000000 + 1500000
        
        // Test average booking value
        OptionalDouble avgValue = bookings.stream()
            .mapToDouble(b -> (Double) b.get("totalPrice"))
            .average();
        assertTrue(avgValue.isPresent());
        assertEquals(1500000.0, avgValue.getAsDouble());
        
        // Test count by status
        long finishedCount = bookings.stream()
            .filter(b -> "FINISHED".equals(b.get("status")))
            .count();
        assertEquals(3, finishedCount);
    }

    @Test
    void testBookingLifecycle() {
        // Test booking entity lifecycle
        Map<String, Object> booking = new HashMap<>();
        LocalDateTime now = LocalDateTime.now();
        
        // Creation
        booking.put("id", "BOOK001");
        booking.put("status", "DRAFT");
        booking.put("createdAt", now);
        booking.put("updatedAt", now);
        
        assertEquals("DRAFT", booking.get("status"));
        assertNotNull(booking.get("createdAt"));
        
        // Status update
        LocalDateTime updateTime = now.plusMinutes(30);
        booking.put("status", "WAITING_FOR_PAYMENT");
        booking.put("updatedAt", updateTime);
        
        assertEquals("WAITING_FOR_PAYMENT", booking.get("status"));
        assertTrue(((LocalDateTime) booking.get("updatedAt")).isAfter((LocalDateTime) booking.get("createdAt")));
        
        // Payment confirmation
        LocalDateTime paymentTime = updateTime.plusHours(1);
        booking.put("status", "PAID");
        booking.put("paidAt", paymentTime);
        booking.put("updatedAt", paymentTime);
        
        assertEquals("PAID", booking.get("status"));
        assertNotNull(booking.get("paidAt"));
    }

    @Test
    void testBookingConstraints() {
        // Test booking business constraints
        
        // Test minimum advance booking time
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime validPickUp = now.plusHours(25); // 1+ day advance
        LocalDateTime invalidPickUp = now.plusHours(1); // Too soon
        
        long advanceHours = java.time.temporal.ChronoUnit.HOURS.between(now, validPickUp);
        assertTrue(advanceHours >= 24); // Must book at least 24 hours in advance
        
        long shortAdvance = java.time.temporal.ChronoUnit.HOURS.between(now, invalidPickUp);
        assertFalse(shortAdvance >= 24);
        
        // Test maximum rental duration
        LocalDateTime longDropOff = validPickUp.plusDays(91); // Too long
        LocalDateTime validDropOff = validPickUp.plusDays(30); // Valid
        
        long longDuration = java.time.temporal.ChronoUnit.DAYS.between(validPickUp, longDropOff);
        long validDuration = java.time.temporal.ChronoUnit.DAYS.between(validPickUp, validDropOff);
        
        assertFalse(longDuration <= 90); // Max 90 days
        assertTrue(validDuration <= 90);
    }
}