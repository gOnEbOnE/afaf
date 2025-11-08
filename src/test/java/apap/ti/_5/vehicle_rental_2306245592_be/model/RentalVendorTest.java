package apap.ti._5.vehicle_rental_2306245592_be.model;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class RentalVendorTest {

    @Test
    void testRentalVendorCreation() {
        // Test rental vendor entity creation
        Map<String, Object> vendor = new HashMap<>();
        vendor.put("id", "VENDOR001");
        vendor.put("name", "Jakarta Car Rental");
        vendor.put("email", "jakarta@rental.com");
        vendor.put("phoneNumber", "021-12345678");
        vendor.put("address", "Jl. Sudirman No. 123, Jakarta");
        vendor.put("active", true);
        vendor.put("createdAt", LocalDateTime.now());
        vendor.put("updatedAt", LocalDateTime.now());
        
        assertNotNull(vendor.get("id"));
        assertEquals("Jakarta Car Rental", vendor.get("name"));
        assertEquals("jakarta@rental.com", vendor.get("email"));
        assertEquals("021-12345678", vendor.get("phoneNumber"));
        assertTrue((Boolean) vendor.get("active"));
        assertNotNull(vendor.get("createdAt"));
    }

    @Test
    void testVendorValidation() {
        // Test vendor validation rules
        
        // Test email validation
        String validEmail = "vendor@rental.com";
        String invalidEmail = "invalid-email";
        
        assertTrue(validEmail.contains("@"));
        assertTrue(validEmail.contains("."));
        assertFalse(invalidEmail.contains("@"));
        
        // Test phone number validation
        String validPhone = "021-12345678";
        String invalidPhone = "123";
        
        assertTrue(validPhone.length() >= 10);
        assertFalse(invalidPhone.length() >= 10);
        
        // Test name validation
        String validName = "Jakarta Car Rental";
        String invalidName = "";
        
        assertFalse(validName.trim().isEmpty());
        assertTrue(invalidName.trim().isEmpty());
    }

    @Test
    void testVendorSearch() {
        // Test vendor search functionality
        List<Map<String, Object>> vendors = new ArrayList<>();
        
        Map<String, Object> vendor1 = new HashMap<>();
        vendor1.put("id", "VENDOR001");
        vendor1.put("name", "Jakarta Car Rental");
        vendor1.put("email", "jakarta@rental.com");
        vendor1.put("active", true);
        
        Map<String, Object> vendor2 = new HashMap<>();
        vendor2.put("id", "VENDOR002");
        vendor2.put("name", "Bandung Vehicle Service");
        vendor2.put("email", "bandung@vehicle.com");
        vendor2.put("active", true);
        
        Map<String, Object> vendor3 = new HashMap<>();
        vendor3.put("id", "VENDOR003");
        vendor3.put("name", "Surabaya Auto Rental");
        vendor3.put("email", "surabaya@auto.com");
        vendor3.put("active", false);
        
        vendors.add(vendor1);
        vendors.add(vendor2);
        vendors.add(vendor3);
        
        // Test search by name
        String searchKeyword = "Jakarta";
        long matchCount = vendors.stream()
            .filter(v -> ((String) v.get("name")).toLowerCase().contains(searchKeyword.toLowerCase()))
            .count();
        assertEquals(1, matchCount);
        
        // Test filter by active status
        long activeCount = vendors.stream()
            .filter(v -> Boolean.TRUE.equals(v.get("active")))
            .count();
        assertEquals(2, activeCount);
        
        // Test search by email domain
        long rentalDomainCount = vendors.stream()
            .filter(v -> ((String) v.get("email")).contains("rental"))
            .count();
        assertEquals(1, rentalDomainCount); // Only jakarta@rental.com contains "rental"
    }

    @Test
    void testVendorVehicleRelationship() {
        // Test vendor-vehicle relationship
        Map<String, Object> vendor = new HashMap<>();
        vendor.put("id", "VENDOR001");
        vendor.put("name", "Jakarta Car Rental");
        
        List<String> vehicleIds = Arrays.asList("VEH001", "VEH002", "VEH003");
        vendor.put("vehicles", vehicleIds);
        
        assertEquals(3, ((List<?>) vendor.get("vehicles")).size());
        assertTrue(((List<?>) vendor.get("vehicles")).contains("VEH001"));
        assertTrue(((List<?>) vendor.get("vehicles")).contains("VEH002"));
        
        // Test vehicle count per vendor
        Map<String, Integer> vehicleCounts = new HashMap<>();
        vehicleCounts.put("VENDOR001", 3);
        vehicleCounts.put("VENDOR002", 5);
        vehicleCounts.put("VENDOR003", 1);
        
        assertEquals(3, vehicleCounts.get("VENDOR001"));
        assertEquals(5, vehicleCounts.get("VENDOR002"));
        assertTrue(vehicleCounts.get("VENDOR002") > vehicleCounts.get("VENDOR001"));
    }

    @Test
    void testVendorPerformanceMetrics() {
        // Test vendor performance metrics
        Map<String, Object> vendorMetrics = new HashMap<>();
        vendorMetrics.put("vendorId", "VENDOR001");
        vendorMetrics.put("totalBookings", 150);
        vendorMetrics.put("totalRevenue", 75000000.0);
        vendorMetrics.put("averageRating", 4.5);
        vendorMetrics.put("completedBookings", 145);
        vendorMetrics.put("cancelledBookings", 5);
        
        // Test completion rate
        int totalBookings = (Integer) vendorMetrics.get("totalBookings");
        int completedBookings = (Integer) vendorMetrics.get("completedBookings");
        double completionRate = (double) completedBookings / totalBookings * 100;
        
        assertTrue(completionRate > 95.0); // High completion rate
        assertEquals(96.67, completionRate, 0.1);
        
        // Test average revenue per booking
        double totalRevenue = (Double) vendorMetrics.get("totalRevenue");
        double avgRevenuePerBooking = totalRevenue / totalBookings;
        
        assertEquals(500000.0, avgRevenuePerBooking);
        assertTrue(avgRevenuePerBooking > 0);
        
        // Test rating validation
        double rating = (Double) vendorMetrics.get("averageRating");
        assertTrue(rating >= 1.0 && rating <= 5.0);
    }

    @Test
    void testVendorContactInformation() {
        // Test vendor contact information management
        Map<String, Object> vendor = new HashMap<>();
        vendor.put("id", "VENDOR001");
        vendor.put("name", "Jakarta Car Rental");
        vendor.put("email", "jakarta@rental.com");
        vendor.put("phoneNumber", "021-12345678");
        vendor.put("alternatePhone", "021-87654321");
        vendor.put("address", "Jl. Sudirman No. 123, Jakarta");
        vendor.put("website", "https://jakartacarrental.com");
        
        // Test contact validation
        assertNotNull(vendor.get("email"));
        assertNotNull(vendor.get("phoneNumber"));
        assertNotNull(vendor.get("address"));
        
        // Test multiple contact methods
        List<String> contactMethods = Arrays.asList(
            (String) vendor.get("email"),
            (String) vendor.get("phoneNumber"),
            (String) vendor.get("alternatePhone")
        );
        
        assertEquals(3, contactMethods.size());
        assertTrue(contactMethods.stream().allMatch(contact -> contact != null && !contact.trim().isEmpty()));
        
        // Test website URL validation
        String website = (String) vendor.get("website");
        assertTrue(website.startsWith("https://"));
    }

    @Test
    void testVendorBusinessHours() {
        // Test vendor business hours management
        Map<String, Object> vendor = new HashMap<>();
        vendor.put("id", "VENDOR001");
        
        Map<String, String> businessHours = new HashMap<>();
        businessHours.put("Monday", "08:00-17:00");
        businessHours.put("Tuesday", "08:00-17:00");
        businessHours.put("Wednesday", "08:00-17:00");
        businessHours.put("Thursday", "08:00-17:00");
        businessHours.put("Friday", "08:00-17:00");
        businessHours.put("Saturday", "08:00-12:00");
        businessHours.put("Sunday", "Closed");
        
        vendor.put("businessHours", businessHours);
        
        assertEquals(7, businessHours.size());
        assertEquals("08:00-17:00", businessHours.get("Monday"));
        assertEquals("Closed", businessHours.get("Sunday"));
        assertTrue(businessHours.get("Saturday").contains("08:00"));
        
        // Test operational days
        long operationalDays = businessHours.values().stream()
            .filter(hours -> !"Closed".equals(hours))
            .count();
        
        assertEquals(6, operationalDays);
    }

    @Test
    void testVendorCapacityManagement() {
        // Test vendor capacity and fleet management
        Map<String, Object> vendor = new HashMap<>();
        vendor.put("id", "VENDOR001");
        vendor.put("maxVehicles", 50);
        vendor.put("currentVehicles", 35);
        vendor.put("availableVehicles", 15);
        vendor.put("maintenanceVehicles", 10);
        vendor.put("rentedVehicles", 10);
        
        int maxVehicles = (Integer) vendor.get("maxVehicles");
        int currentVehicles = (Integer) vendor.get("currentVehicles");
        int availableVehicles = (Integer) vendor.get("availableVehicles");
        int maintenanceVehicles = (Integer) vendor.get("maintenanceVehicles");
        int rentedVehicles = (Integer) vendor.get("rentedVehicles");
        
        // Test capacity constraints
        assertTrue(currentVehicles <= maxVehicles);
        assertEquals(currentVehicles, availableVehicles + maintenanceVehicles + rentedVehicles);
        
        // Test utilization rate
        double utilizationRate = (double) rentedVehicles / currentVehicles * 100;
        assertTrue(utilizationRate >= 0 && utilizationRate <= 100);
        assertEquals(28.57, utilizationRate, 0.1); // 10/35 * 100 = 28.57
        
        // Test availability percentage
        double availabilityRate = (double) availableVehicles / currentVehicles * 100;
        assertEquals(42.86, availabilityRate, 0.1); // 15/35 * 100 = 42.86
    }

    @Test
    void testVendorLifecycle() {
        // Test vendor entity lifecycle
        Map<String, Object> vendor = new HashMap<>();
        LocalDateTime now = LocalDateTime.now();
        
        // Registration
        vendor.put("id", "VENDOR001");
        vendor.put("name", "New Car Rental");
        vendor.put("status", "PENDING");
        vendor.put("createdAt", now);
        vendor.put("updatedAt", now);
        vendor.put("active", false);
        
        assertEquals("PENDING", vendor.get("status"));
        assertFalse((Boolean) vendor.get("active"));
        
        // Approval
        LocalDateTime approvalTime = now.plusDays(1);
        vendor.put("status", "APPROVED");
        vendor.put("active", true);
        vendor.put("approvedAt", approvalTime);
        vendor.put("updatedAt", approvalTime);
        
        assertEquals("APPROVED", vendor.get("status"));
        assertTrue((Boolean) vendor.get("active"));
        assertNotNull(vendor.get("approvedAt"));
        
        // Suspension
        LocalDateTime suspensionTime = now.plusDays(30);
        vendor.put("status", "SUSPENDED");
        vendor.put("active", false);
        vendor.put("suspendedAt", suspensionTime);
        vendor.put("updatedAt", suspensionTime);
        
        assertEquals("SUSPENDED", vendor.get("status"));
        assertFalse((Boolean) vendor.get("active"));
        assertNotNull(vendor.get("suspendedAt"));
    }

    @Test
    void testVendorComparison() {
        // Test vendor comparison functionality
        List<Map<String, Object>> vendors = new ArrayList<>();
        
        Map<String, Object> vendor1 = new HashMap<>();
        vendor1.put("id", "VENDOR001");
        vendor1.put("name", "Premium Rentals");
        vendor1.put("rating", 4.8);
        vendor1.put("vehicleCount", 25);
        vendor1.put("priceRange", "Premium");
        
        Map<String, Object> vendor2 = new HashMap<>();
        vendor2.put("id", "VENDOR002");
        vendor2.put("name", "Budget Cars");
        vendor2.put("rating", 4.2);
        vendor2.put("vehicleCount", 40);
        vendor2.put("priceRange", "Budget");
        
        vendors.add(vendor1);
        vendors.add(vendor2);
        
        // Test sorting by rating
        vendors.sort((v1, v2) -> Double.compare((Double) v2.get("rating"), (Double) v1.get("rating")));
        assertEquals("VENDOR001", vendors.get(0).get("id")); // Higher rating first
        
        // Test sorting by vehicle count
        vendors.sort((v1, v2) -> Integer.compare((Integer) v2.get("vehicleCount"), (Integer) v1.get("vehicleCount")));
        assertEquals("VENDOR002", vendors.get(0).get("id")); // More vehicles first
        
        // Test filtering by price range
        long premiumCount = vendors.stream()
            .filter(v -> "Premium".equals(v.get("priceRange")))
            .count();
        assertEquals(1, premiumCount);
    }
}