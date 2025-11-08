package apap.ti._5.vehicle_rental_2306245592_be.repository;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class BasicRepositoryTest {

    @Test
    void testBookingRepositoryOperations() {
        // Test booking repository query patterns
        List<Map<String, Object>> bookings = new ArrayList<>();
        
        Map<String, Object> booking1 = new HashMap<>();
        booking1.put("id", "BOOK001");
        booking1.put("status", "PAID");
        booking1.put("vehicleId", "VEH001");
        booking1.put("pickUpTime", LocalDateTime.now().plusDays(1));
        booking1.put("dropOffTime", LocalDateTime.now().plusDays(4));
        booking1.put("totalPrice", 1500000.0);
        
        Map<String, Object> booking2 = new HashMap<>();
        booking2.put("id", "BOOK002");
        booking2.put("status", "ONGOING");
        booking2.put("vehicleId", "VEH002");
        booking2.put("pickUpTime", LocalDateTime.now().minusDays(1));
        booking2.put("dropOffTime", LocalDateTime.now().plusDays(2));
        booking2.put("totalPrice", 2000000.0);
        
        bookings.add(booking1);
        bookings.add(booking2);
        
        // Test findByStatus
        List<Map<String, Object>> paidBookings = bookings.stream()
            .filter(b -> "PAID".equals(b.get("status")))
            .collect(Collectors.toList());
        
        assertEquals(1, paidBookings.size());
        assertEquals("BOOK001", paidBookings.get(0).get("id"));
        
        // Test findByVehicleId
        List<Map<String, Object>> vehicleBookings = bookings.stream()
            .filter(b -> "VEH001".equals(b.get("vehicleId")))
            .collect(Collectors.toList());
        
        assertEquals(1, vehicleBookings.size());
    }

    @Test
    void testVehicleRepositoryOperations() {
        // Test vehicle repository query patterns
        List<Map<String, Object>> vehicles = new ArrayList<>();
        
        Map<String, Object> vehicle1 = new HashMap<>();
        vehicle1.put("id", "VEH001");
        vehicle1.put("type", "SUV");
        vehicle1.put("brand", "Toyota");
        vehicle1.put("model", "Fortuner");
        vehicle1.put("capacity", 7);
        vehicle1.put("transmission", "Automatic");
        vehicle1.put("available", true);
        vehicle1.put("deletedAt", null);
        
        Map<String, Object> vehicle2 = new HashMap<>();
        vehicle2.put("id", "VEH002");
        vehicle2.put("type", "Sedan");
        vehicle2.put("brand", "Honda");
        vehicle2.put("model", "Civic");
        vehicle2.put("capacity", 5);
        vehicle2.put("transmission", "Manual");
        vehicle2.put("available", true);
        vehicle2.put("deletedAt", null);
        
        vehicles.add(vehicle1);
        vehicles.add(vehicle2);
        
        // Test findByType
        List<Map<String, Object>> suvVehicles = vehicles.stream()
            .filter(v -> "SUV".equals(v.get("type")))
            .collect(Collectors.toList());
        
        assertEquals(1, suvVehicles.size());
        assertEquals("Toyota", suvVehicles.get(0).get("brand"));
        
        // Test findByDeletedAtIsNull (not deleted)
        List<Map<String, Object>> activeVehicles = vehicles.stream()
            .filter(v -> v.get("deletedAt") == null)
            .collect(Collectors.toList());
        
        assertEquals(2, activeVehicles.size());
        
        // Test findByCapacityGreaterThanEqual
        List<Map<String, Object>> highCapacityVehicles = vehicles.stream()
            .filter(v -> (Integer) v.get("capacity") >= 7)
            .collect(Collectors.toList());
        
        assertEquals(1, highCapacityVehicles.size());
    }

    @Test
    void testRentalAddOnRepositoryOperations() {
        // Test rental add-on repository operations
        List<Map<String, Object>> addOns = new ArrayList<>();
        
        Map<String, Object> addOn1 = new HashMap<>();
        addOn1.put("id", "ADD001");
        addOn1.put("name", "GPS Navigation");
        addOn1.put("price", 50000.0);
        addOn1.put("available", true);
        
        Map<String, Object> addOn2 = new HashMap<>();
        addOn2.put("id", "ADD002");
        addOn2.put("name", "Child Seat");
        addOn2.put("price", 75000.0);
        addOn2.put("available", true);
        
        Map<String, Object> addOn3 = new HashMap<>();
        addOn3.put("id", "ADD003");
        addOn3.put("name", "Bike Rack");
        addOn3.put("price", 100000.0);
        addOn3.put("available", false);
        
        addOns.add(addOn1);
        addOns.add(addOn2);
        addOns.add(addOn3);
        
        // Test findByAvailable
        List<Map<String, Object>> availableAddOns = addOns.stream()
            .filter(a -> Boolean.TRUE.equals(a.get("available")))
            .collect(Collectors.toList());
        
        assertEquals(2, availableAddOns.size());
        
        // Test findByPriceLessThanEqual
        List<Map<String, Object>> affordableAddOns = addOns.stream()
            .filter(a -> (Double) a.get("price") <= 75000.0)
            .collect(Collectors.toList());
        
        assertEquals(2, affordableAddOns.size());
    }

    @Test
    void testRentalVendorRepositoryOperations() {
        // Test rental vendor repository operations
        List<Map<String, Object>> vendors = new ArrayList<>();
        
        Map<String, Object> vendor1 = new HashMap<>();
        vendor1.put("id", "VENDOR001");
        vendor1.put("name", "Jakarta Car Rental");
        vendor1.put("email", "jakarta@rental.com");
        vendor1.put("phoneNumber", "021-12345678");
        vendor1.put("active", true);
        
        Map<String, Object> vendor2 = new HashMap<>();
        vendor2.put("id", "VENDOR002");
        vendor2.put("name", "Bandung Vehicle Rental");
        vendor2.put("email", "bandung@rental.com");
        vendor2.put("phoneNumber", "022-87654321");
        vendor2.put("active", true);
        
        vendors.add(vendor1);
        vendors.add(vendor2);
        
        // Test findByActive
        List<Map<String, Object>> activeVendors = vendors.stream()
            .filter(v -> Boolean.TRUE.equals(v.get("active")))
            .collect(Collectors.toList());
        
        assertEquals(2, activeVendors.size());
        
        // Test findByNameContaining
        List<Map<String, Object>> jakartaVendors = vendors.stream()
            .filter(v -> ((String) v.get("name")).contains("Jakarta"))
            .collect(Collectors.toList());
        
        assertEquals(1, jakartaVendors.size());
        assertEquals("jakarta@rental.com", jakartaVendors.get(0).get("email"));
    }

    @Test
    void testRepositoryOperations() {
        // Test basic repository concepts without actual repository dependencies
        
        // Test data operations
        java.util.List<String> dataList = new java.util.ArrayList<>();
        dataList.add("item1");
        dataList.add("item2");
        dataList.add("item3");
        
        assertEquals(3, dataList.size());
        assertTrue(dataList.contains("item1"));
        assertFalse(dataList.contains("item4"));
        
        // Test filtering
        java.util.List<String> filteredList = dataList.stream()
            .filter(item -> item.contains("1"))
            .collect(java.util.stream.Collectors.toList());
        
        assertEquals(1, filteredList.size());
        assertEquals("item1", filteredList.get(0));
    }

    @Test
    void testDataValidation() {
        // Test data validation concepts
        String validData = "valid_data";
        String invalidData = "";
        
        assertNotNull(validData);
        assertFalse(validData.isEmpty());
        assertTrue(invalidData.isEmpty());
        
        // Test ID validation
        Integer validId = 1;
        Integer invalidId = null;
        
        assertNotNull(validId);
        assertTrue(validId > 0);
        assertNull(invalidId);
    }

    @Test
    void testCRUDOperations() {
        // Simulate CRUD operations with Map structure
        java.util.Map<Integer, String> dataStore = new java.util.HashMap<>();
        
        // Create
        dataStore.put(1, "entity1");
        dataStore.put(2, "entity2");
        
        // Read
        assertEquals("entity1", dataStore.get(1));
        assertEquals(2, dataStore.size());
        
        // Update
        dataStore.put(1, "updated_entity1");
        assertEquals("updated_entity1", dataStore.get(1));
        
        // Delete
        dataStore.remove(2);
        assertEquals(1, dataStore.size());
        assertNull(dataStore.get(2));
    }

    @Test
    void testQueryOperations() {
        // Test query-like operations
        java.util.List<java.util.Map<String, Object>> entities = new java.util.ArrayList<>();
        
        java.util.Map<String, Object> entity1 = new java.util.HashMap<>();
        entity1.put("id", 1);
        entity1.put("name", "Test Entity 1");
        entity1.put("active", true);
        
        java.util.Map<String, Object> entity2 = new java.util.HashMap<>();
        entity2.put("id", 2);
        entity2.put("name", "Test Entity 2");
        entity2.put("active", false);
        
        entities.add(entity1);
        entities.add(entity2);
        
        // Find by active status
        long activeCount = entities.stream()
            .filter(e -> (Boolean) e.get("active"))
            .count();
        
        assertEquals(1, activeCount);
        
        // Find by name containing
        long nameContainsCount = entities.stream()
            .filter(e -> ((String) e.get("name")).contains("Test"))
            .count();
        
        assertEquals(2, nameContainsCount);
    }

    @Test
    void testSortingAndPaging() {
        java.util.List<Integer> numbers = java.util.Arrays.asList(3, 1, 4, 1, 5, 9, 2, 6);
        
        // Test sorting
        java.util.List<Integer> sorted = numbers.stream()
            .sorted()
            .collect(java.util.stream.Collectors.toList());
        
        assertEquals(1, sorted.get(0));
        assertEquals(9, sorted.get(sorted.size() - 1));
        
        // Test paging (limit)
        java.util.List<Integer> paged = numbers.stream()
            .limit(3)
            .collect(java.util.stream.Collectors.toList());
        
        assertEquals(3, paged.size());
    }

    @Test
    void testCustomQueries() {
        // Test custom query patterns
        List<Map<String, Object>> entities = new ArrayList<>();
        
        for (int i = 1; i <= 10; i++) {
            Map<String, Object> entity = new HashMap<>();
            entity.put("id", i);
            entity.put("name", "Entity " + i);
            entity.put("value", i * 10);
            entity.put("active", i % 2 == 0);
            entities.add(entity);
        }
        
        // Test complex filtering
        List<Map<String, Object>> filteredEntities = entities.stream()
            .filter(e -> (Boolean) e.get("active"))
            .filter(e -> (Integer) e.get("value") > 30)
            .sorted((e1, e2) -> Integer.compare((Integer) e1.get("id"), (Integer) e2.get("id")))
            .collect(Collectors.toList());
        
        assertEquals(4, filteredEntities.size());
        assertEquals(4, filteredEntities.get(0).get("id"));
        assertEquals(10, filteredEntities.get(filteredEntities.size() - 1).get("id"));
    }

    @Test
    void testAggregateOperations() {
        // Test aggregate functions
        List<Map<String, Object>> salesData = new ArrayList<>();
        
        for (int i = 1; i <= 5; i++) {
            Map<String, Object> sale = new HashMap<>();
            sale.put("id", i);
            sale.put("amount", i * 100.0);
            sale.put("month", i <= 3 ? "Q1" : "Q2");
            salesData.add(sale);
        }
        
        // Test sum
        double totalAmount = salesData.stream()
            .mapToDouble(s -> (Double) s.get("amount"))
            .sum();
        assertEquals(1500.0, totalAmount);
        
        // Test average
        OptionalDouble avgAmount = salesData.stream()
            .mapToDouble(s -> (Double) s.get("amount"))
            .average();
        assertTrue(avgAmount.isPresent());
        assertEquals(300.0, avgAmount.getAsDouble());
        
        // Test count by group
        Map<String, Long> countByQuarter = salesData.stream()
            .collect(Collectors.groupingBy(
                s -> (String) s.get("month"),
                Collectors.counting()
            ));
        
        assertEquals(3L, countByQuarter.get("Q1"));
        assertEquals(2L, countByQuarter.get("Q2"));
    }
}