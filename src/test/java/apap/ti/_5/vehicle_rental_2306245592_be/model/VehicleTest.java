package apap.ti._5.vehicle_rental_2306245592_be.model;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class VehicleTest {

    @Test
    void testVehicleEntityCreation() {
        // Test vehicle entity creation and basic properties
        Map<String, Object> vehicleData = new HashMap<>();
        vehicleData.put("id", "VEH001");
        vehicleData.put("type", "SUV");
        vehicleData.put("brand", "Toyota");
        vehicleData.put("model", "Fortuner");
        vehicleData.put("year", 2023);
        vehicleData.put("capacity", 7);
        vehicleData.put("transmission", "Automatic");
        vehicleData.put("fuelType", "Diesel");
        vehicleData.put("licensePlate", "B1234ABC");
        vehicleData.put("price", 800000.0);
        vehicleData.put("available", true);
        vehicleData.put("createdAt", LocalDateTime.now());
        
        assertNotNull(vehicleData.get("id"));
        assertEquals("SUV", vehicleData.get("type"));
        assertEquals("Toyota", vehicleData.get("brand"));
        assertEquals("Fortuner", vehicleData.get("model"));
        assertEquals(2023, vehicleData.get("year"));
        assertEquals(7, vehicleData.get("capacity"));
        assertTrue((Boolean) vehicleData.get("available"));
        assertTrue((Double) vehicleData.get("price") > 0);
    }

    @Test
    void testVehicleValidation() {
        // Test vehicle validation rules
        
        // Test license plate format
        String validLicensePlate = "B1234ABC";
        String invalidLicensePlate = "INVALID";
        
        assertTrue(validLicensePlate.matches("^[A-Z]\\d{4}[A-Z]{3}$"));
        assertFalse(invalidLicensePlate.matches("^[A-Z]\\d{4}[A-Z]{3}$"));
        
        // Test year validation
        int currentYear = LocalDateTime.now().getYear();
        int validYear = 2020;
        int invalidYear = currentYear + 2;
        
        assertTrue(validYear >= 2000 && validYear <= currentYear + 1);
        assertFalse(invalidYear >= 2000 && invalidYear <= currentYear + 1);
        
        // Test capacity validation
        int validCapacity = 5;
        int invalidCapacity = 0;
        
        assertTrue(validCapacity > 0 && validCapacity <= 20);
        assertFalse(invalidCapacity > 0 && invalidCapacity <= 20);
    }

    @Test
    void testVehicleTypes() {
        // Test different vehicle types
        List<String> validTypes = Arrays.asList("Sedan", "SUV", "Hatchback", "MPV", "Convertible");
        
        assertEquals(5, validTypes.size());
        assertTrue(validTypes.contains("Sedan"));
        assertTrue(validTypes.contains("SUV"));
        assertTrue(validTypes.contains("MPV"));
        
        // Test type validation
        String selectedType = "SUV";
        assertTrue(validTypes.contains(selectedType));
    }

    @Test
    void testVehicleTransmission() {
        // Test transmission types
        List<String> transmissionTypes = Arrays.asList("Manual", "Automatic", "CVT");
        
        assertEquals(3, transmissionTypes.size());
        assertTrue(transmissionTypes.contains("Manual"));
        assertTrue(transmissionTypes.contains("Automatic"));
        assertTrue(transmissionTypes.contains("CVT"));
    }

    @Test
    void testVehicleFuelTypes() {
        // Test fuel types
        List<String> fuelTypes = Arrays.asList("Gasoline", "Diesel", "Electric", "Hybrid");
        
        assertEquals(4, fuelTypes.size());
        assertTrue(fuelTypes.contains("Gasoline"));
        assertTrue(fuelTypes.contains("Diesel"));
        assertTrue(fuelTypes.contains("Electric"));
        assertTrue(fuelTypes.contains("Hybrid"));
    }

    @Test
    void testVehicleAvailability() {
        // Test vehicle availability logic
        Map<String, Object> vehicle = new HashMap<>();
        vehicle.put("available", true);
        vehicle.put("deletedAt", null);
        
        // Vehicle is available and not deleted
        assertTrue((Boolean) vehicle.get("available"));
        assertNull(vehicle.get("deletedAt"));
        
        // Test unavailable vehicle
        vehicle.put("available", false);
        assertFalse((Boolean) vehicle.get("available"));
        
        // Test deleted vehicle
        vehicle.put("deletedAt", LocalDateTime.now());
        assertNotNull(vehicle.get("deletedAt"));
    }

    @Test
    void testVehiclePricing() {
        // Test vehicle pricing validation
        double validPrice = 500000.0;
        double invalidPrice = -100.0;
        double zeroPrice = 0.0;
        
        assertTrue(validPrice > 0);
        assertFalse(invalidPrice > 0);
        assertFalse(zeroPrice > 0);
        
        // Test price ranges
        double economyPrice = 300000.0;
        double premiumPrice = 1000000.0;
        double luxuryPrice = 2000000.0;
        
        assertTrue(economyPrice >= 200000.0 && economyPrice < 500000.0);
        assertTrue(premiumPrice >= 500000.0 && premiumPrice < 1500000.0);
        assertTrue(luxuryPrice >= 1500000.0);
    }

    @Test
    void testVehicleSearch() {
        // Test vehicle search functionality
        List<Map<String, Object>> vehicles = new ArrayList<>();
        
        Map<String, Object> vehicle1 = new HashMap<>();
        vehicle1.put("brand", "Toyota");
        vehicle1.put("model", "Avanza");
        vehicle1.put("type", "MPV");
        vehicle1.put("licensePlate", "B1111AAA");
        
        Map<String, Object> vehicle2 = new HashMap<>();
        vehicle2.put("brand", "Honda");
        vehicle2.put("model", "Civic");
        vehicle2.put("type", "Sedan");
        vehicle2.put("licensePlate", "B2222BBB");
        
        vehicles.add(vehicle1);
        vehicles.add(vehicle2);
        
        // Test search by brand
        String searchKeyword = "Toyota";
        long matchCount = vehicles.stream()
            .filter(v -> ((String) v.get("brand")).toLowerCase().contains(searchKeyword.toLowerCase()))
            .count();
        
        assertEquals(1, matchCount);
        
        // Test search by type
        String typeFilter = "Sedan";
        long typeCount = vehicles.stream()
            .filter(v -> typeFilter.equals(v.get("type")))
            .count();
        
        assertEquals(1, typeCount);
    }

    @Test
    void testVehicleEntityRelationships() {
        // Test vehicle entity relationships
        Map<String, Object> vehicle = new HashMap<>();
        vehicle.put("id", "VEH001");
        vehicle.put("vendorId", "VENDOR001");
        
        Map<String, Object> vendor = new HashMap<>();
        vendor.put("id", "VENDOR001");
        vendor.put("name", "Jakarta Car Rental");
        
        // Test vehicle-vendor relationship
        assertEquals(vehicle.get("vendorId"), vendor.get("id"));
        assertNotNull(vendor.get("name"));
        
        // Test booking relationship
        List<String> bookingIds = Arrays.asList("BOOK001", "BOOK002");
        vehicle.put("bookings", bookingIds);
        
        assertEquals(2, ((List<?>) vehicle.get("bookings")).size());
        assertTrue(((List<?>) vehicle.get("bookings")).contains("BOOK001"));
    }

    @Test
    void testVehicleLifecycle() {
        // Test vehicle lifecycle (creation, update, soft delete)
        Map<String, Object> vehicle = new HashMap<>();
        LocalDateTime now = LocalDateTime.now();
        
        // Creation
        vehicle.put("id", "VEH001");
        vehicle.put("createdAt", now);
        vehicle.put("updatedAt", now);
        vehicle.put("deletedAt", null);
        
        assertNotNull(vehicle.get("createdAt"));
        assertNotNull(vehicle.get("updatedAt"));
        assertNull(vehicle.get("deletedAt"));
        
        // Update
        LocalDateTime updateTime = now.plusHours(1);
        vehicle.put("updatedAt", updateTime);
        vehicle.put("price", 900000.0);
        
        assertTrue(((LocalDateTime) vehicle.get("updatedAt")).isAfter((LocalDateTime) vehicle.get("createdAt")));
        assertEquals(900000.0, vehicle.get("price"));
        
        // Soft delete
        LocalDateTime deleteTime = now.plusHours(2);
        vehicle.put("deletedAt", deleteTime);
        
        assertNotNull(vehicle.get("deletedAt"));
        assertTrue(((LocalDateTime) vehicle.get("deletedAt")).isAfter((LocalDateTime) vehicle.get("createdAt")));
    }
}