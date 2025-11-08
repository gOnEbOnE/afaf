package apap.ti._5.vehicle_rental_2306245592_be.model;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class BasicModelTest {

    @Test
    void testRentalBookingBasicOperations() {
        RentalBooking booking = new RentalBooking();
        
        // Test basic setters and getters
        booking.setId("BOOK001");
        booking.setPickUpLocation("Jakarta");
        booking.setDropOffLocation("Bandung");
        booking.setCapacityNeeded(5);
        booking.setTransmissionNeeded("Automatic");
        booking.setTotalPrice(1000000.0);
        booking.setIncludeDriver(false);
        booking.setStatus("Upcoming");

        assertEquals("BOOK001", booking.getId());
        assertEquals("Jakarta", booking.getPickUpLocation());
        assertEquals("Bandung", booking.getDropOffLocation());
        assertEquals(5, booking.getCapacityNeeded());
        assertEquals("Automatic", booking.getTransmissionNeeded());
        assertEquals(1000000.0, booking.getTotalPrice());
        assertFalse(booking.getIncludeDriver());
        assertEquals("Upcoming", booking.getStatus());
    }

    @Test
    void testVehicleBasicOperations() {
        Vehicle vehicle = new Vehicle();
        
        vehicle.setId("VEH001");
        vehicle.setType("SUV");
        vehicle.setBrand("Toyota");
        vehicle.setModel("Fortuner");
        vehicle.setYear(2023);
        vehicle.setLocation("Jakarta");
        vehicle.setLicensePlate("B1234ABC");
        vehicle.setCapacity(7);
        vehicle.setTransmission("Automatic");
        vehicle.setFuelType("Diesel");
        vehicle.setPrice(800000.0);
        vehicle.setStatus("Available");

        assertEquals("VEH001", vehicle.getId());
        assertEquals("SUV", vehicle.getType());
        assertEquals("Toyota", vehicle.getBrand());
        assertEquals("Fortuner", vehicle.getModel());
        assertEquals(2023, vehicle.getYear());
        assertEquals("Jakarta", vehicle.getLocation());
        assertEquals("B1234ABC", vehicle.getLicensePlate());
        assertEquals(7, vehicle.getCapacity());
        assertEquals("Automatic", vehicle.getTransmission());
        assertEquals("Diesel", vehicle.getFuelType());
        assertEquals(800000.0, vehicle.getPrice());
        assertEquals("Available", vehicle.getStatus());
    }

    @Test
    void testRentalVendorBasicOperations() {
        RentalVendor vendor = new RentalVendor();
        
        vendor.setId(1);
        vendor.setName("Test Rental Company");
        vendor.setEmail("test@rental.com");
        vendor.setPhone("+6281234567890");

        assertEquals(1, vendor.getId());
        assertEquals("Test Rental Company", vendor.getName());
        assertEquals("test@rental.com", vendor.getEmail());
        assertEquals("+6281234567890", vendor.getPhone());
    }

    @Test
    void testRentalAddOnBasicOperations() {
        RentalAddOn addOn = new RentalAddOn();
        
        addOn.setName("GPS Navigation");
        addOn.setPrice(25000.0);

        assertEquals("GPS Navigation", addOn.getName());
        assertEquals(25000.0, addOn.getPrice());
    }

    @Test
    void testLocalDateTimeOperations() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime future = now.plusDays(1);
        
        assertTrue(future.isAfter(now));
        assertFalse(future.isBefore(now));
        
        RentalBooking booking = new RentalBooking();
        booking.setPickUpTime(now);
        booking.setDropOffTime(future);
        
        assertTrue(booking.getDropOffTime().isAfter(booking.getPickUpTime()));
    }
}