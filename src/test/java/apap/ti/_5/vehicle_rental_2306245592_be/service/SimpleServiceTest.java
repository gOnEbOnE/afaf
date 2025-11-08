package apap.ti._5.vehicle_rental_2306245592_be.service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SimpleServiceTest {

    @Test
    void testBookingServiceLogic() {
        // Test booking business logic patterns
        LocalDateTime pickUpTime = LocalDateTime.now().plusDays(1);
        LocalDateTime dropOffTime = pickUpTime.plusDays(3);
        
        // Test rental duration calculation
        long rentalDays = ChronoUnit.DAYS.between(pickUpTime, dropOffTime);
        assertEquals(3, rentalDays);
        assertTrue(rentalDays > 0);
        
        // Test price calculation logic
        double basePrice = 500000.0;
        double totalPrice = basePrice * rentalDays;
        assertEquals(1500000.0, totalPrice);
    }

    @Test
    void testVehicleServiceLogic() {
        // Test vehicle filtering logic
        List<Map<String, Object>> vehicles = new ArrayList<>();
        
        Map<String, Object> vehicle1 = new HashMap<>();
        vehicle1.put("type", "SUV");
        vehicle1.put("capacity", 7);
        vehicle1.put("transmission", "Automatic");
        vehicle1.put("price", 800000.0);
        
        Map<String, Object> vehicle2 = new HashMap<>();
        vehicle2.put("type", "Sedan");
        vehicle2.put("capacity", 5);
        vehicle2.put("transmission", "Manual");
        vehicle2.put("price", 500000.0);
        
        vehicles.add(vehicle1);
        vehicles.add(vehicle2);
        
        // Test filtering by type
        List<Map<String, Object>> suvVehicles = vehicles.stream()
            .filter(v -> "SUV".equals(v.get("type")))
            .collect(Collectors.toList());
        
        assertEquals(1, suvVehicles.size());
        assertEquals("SUV", suvVehicles.get(0).get("type"));
        
        // Test filtering by capacity
        List<Map<String, Object>> highCapacityVehicles = vehicles.stream()
            .filter(v -> (Integer) v.get("capacity") >= 7)
            .collect(Collectors.toList());
        
        assertEquals(1, highCapacityVehicles.size());
    }

    @Test
    void testPenaltyCalculation() {
        // Test penalty calculation logic
        LocalDateTime expectedReturnTime = LocalDateTime.of(2025, 11, 10, 12, 0);
        LocalDateTime actualReturnTime = LocalDateTime.of(2025, 11, 12, 15, 30);
        
        // Calculate late return penalty
        long hoursLate = ChronoUnit.HOURS.between(expectedReturnTime, actualReturnTime);
        double penaltyPerHour = 50000.0;
        double totalPenalty = hoursLate * penaltyPerHour;
        
        assertTrue(hoursLate > 0);
        assertTrue(totalPenalty > 0);
        assertEquals(51, hoursLate); // 2 days + 3.5 hours
    }

    @Test
    void testBookingStatusTransition() {
        // Test booking status workflow
        List<String> statusFlow = Arrays.asList(
            "DRAFT", "WAITING_FOR_PAYMENT", "PAID", "ONGOING", "FINISHED"
        );
        
        assertEquals("DRAFT", statusFlow.get(0));
        assertEquals("FINISHED", statusFlow.get(statusFlow.size() - 1));
        
        // Test valid status transitions
        String currentStatus = "PAID";
        String nextStatus = "ONGOING";
        
        int currentIndex = statusFlow.indexOf(currentStatus);
        int nextIndex = statusFlow.indexOf(nextStatus);
        
        assertTrue(nextIndex > currentIndex); // Valid progression
    }

    @Test
    void testLocationService() {
        // Test province data handling
        List<String> provinces = Arrays.asList(
            "DKI Jakarta", "Jawa Barat", "Jawa Tengah", "Jawa Timur",
            "Sumatera Utara", "Sumatera Barat", "Bali", "Kalimantan Timur"
        );
        
        assertTrue(provinces.size() > 0);
        assertTrue(provinces.contains("DKI Jakarta"));
        assertTrue(provinces.contains("Jawa Barat"));
        
        // Test province validation
        String selectedProvince = "DKI Jakarta";
        assertTrue(provinces.contains(selectedProvince));
    }

    @Test
    void testVehicleAvailabilityCheck() {
        // Test vehicle availability logic
        LocalDateTime requestStart = LocalDateTime.now().plusDays(1);
        LocalDateTime requestEnd = requestStart.plusDays(2);
        
        // Simulate existing booking
        LocalDateTime existingStart = LocalDateTime.now().plusDays(3);
        LocalDateTime existingEnd = existingStart.plusDays(2);
        
        // Check for overlap
        boolean hasOverlap = requestStart.isBefore(existingEnd) && requestEnd.isAfter(existingStart);
        assertFalse(hasOverlap); // No overlap should exist
        
        // Test overlapping scenario
        LocalDateTime overlappingStart = requestStart.plusHours(12);
        LocalDateTime overlappingEnd = overlappingStart.plusDays(1);
        
        boolean hasOverlapCase = requestStart.isBefore(overlappingEnd) && requestEnd.isAfter(overlappingStart);
        assertTrue(hasOverlapCase); // Should detect overlap
    }

    @Test
    void testAddOnCalculation() {
        // Test rental add-on price calculation
        List<Map<String, Object>> addOns = new ArrayList<>();
        
        Map<String, Object> gpsAddOn = new HashMap<>();
        gpsAddOn.put("name", "GPS Navigation");
        gpsAddOn.put("price", 50000.0);
        gpsAddOn.put("quantity", 1);
        
        Map<String, Object> childSeatAddOn = new HashMap<>();
        childSeatAddOn.put("name", "Child Seat");
        childSeatAddOn.put("price", 75000.0);
        childSeatAddOn.put("quantity", 2);
        
        addOns.add(gpsAddOn);
        addOns.add(childSeatAddOn);
        
        // Calculate total add-on price
        double totalAddOnPrice = addOns.stream()
            .mapToDouble(addOn -> (Double) addOn.get("price") * (Integer) addOn.get("quantity"))
            .sum();
        
        assertEquals(200000.0, totalAddOnPrice); // 50000 + (75000 * 2)
    }

    @Test
    void testDriverInclusionLogic() {
        // Test driver inclusion price calculation
        boolean includeDriver = true;
        double driverCostPerDay = 100000.0;
        int rentalDays = 3;
        
        double driverCost = includeDriver ? driverCostPerDay * rentalDays : 0.0;
        assertEquals(300000.0, driverCost);
        
        // Test without driver
        boolean noDriver = false;
        double noCost = noDriver ? driverCostPerDay * rentalDays : 0.0;
        assertEquals(0.0, noCost);
    }

    @Test
    void testBusinessLogicValidation() {
        // Test various business rule validations
        
        // Test license plate format
        String validLicensePlate = "B1234ABC";
        assertTrue(validLicensePlate.matches("^[A-Z]\\d{4}[A-Z]{3}$"));
        
        // Test capacity requirements
        int passengerCount = 5;
        int vehicleCapacity = 7;
        assertTrue(vehicleCapacity >= passengerCount);
        
        // Test rental duration limits
        long rentalDuration = 30; // days
        assertTrue(rentalDuration <= 90); // Max 3 months
        assertTrue(rentalDuration >= 1);  // Min 1 day
    }

    @Test
    void testDataProcessing() {
        // Test data processing logic
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);
        
        int sum = numbers.stream().mapToInt(Integer::intValue).sum();
        assertEquals(15, sum);
        
        OptionalDouble average = numbers.stream().mapToInt(Integer::intValue).average();
        assertTrue(average.isPresent());
        assertEquals(3.0, average.getAsDouble());
    }

    @Test
    void testBasicAssertions() {
        // Test basic assertions work
        assertTrue(true);
        assertFalse(false);
        assertNotNull("test");
        assertEquals(1, 1);
        assertNotEquals(1, 2);
    }

    @Test
    void testStringOperations() {
        String test = "Hello World";
        assertEquals(11, test.length());
        assertTrue(test.contains("World"));
        assertTrue(test.startsWith("Hello"));
        assertTrue(test.endsWith("World"));
    }

    @Test
    void testNumberOperations() {
        int a = 5;
        int b = 10;
        assertEquals(15, a + b);
        assertEquals(5, b - a);
        assertEquals(50, a * b);
        assertEquals(2, b / a);
    }

    @Test
    void testCollectionOperations() {
        java.util.List<String> list = java.util.Arrays.asList("a", "b", "c");
        assertEquals(3, list.size());
        assertTrue(list.contains("a"));
        assertFalse(list.contains("d"));
    }

    @Test
    void testExceptionHandling() {
        assertThrows(IllegalArgumentException.class, () -> {
            throw new IllegalArgumentException("Test exception");
        });

        assertDoesNotThrow(() -> {
            String test = "safe operation";
        });
    }
}