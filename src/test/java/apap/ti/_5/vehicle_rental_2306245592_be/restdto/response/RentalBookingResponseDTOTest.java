package apap.ti._5.vehicle_rental_2306245592_be.restdto.response;

import apap.ti._5.vehicle_rental_2306245592_be.restdto.response.RentalAddOn.RentalAddOnResponseDTO;
import apap.ti._5.vehicle_rental_2306245592_be.restdto.response.booking.RentalBookingResponseDTO;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class RentalBookingResponseDTOTest {

    @Test
    void testNoArgsConstructor() {
        RentalBookingResponseDTO dto = new RentalBookingResponseDTO();
        assertNotNull(dto);
    }

    @Test
    void testAllArgsConstructor() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime pickUp = now.plusDays(1);
        LocalDateTime dropOff = now.plusDays(3);

        List<RentalAddOnResponseDTO> addOns = Arrays.asList(
                new RentalAddOnResponseDTO(UUID.randomUUID(), "GPS", 50000.0, now, now),
                new RentalAddOnResponseDTO(UUID.randomUUID(), "Child Seat", 100000.0, now, now)
        );

        RentalBookingResponseDTO dto = new RentalBookingResponseDTO(
                "BKG-001",
                "VH-001",
                "Toyota",
                "Fortuner",
                pickUp,
                dropOff,
                "Jakarta",
                "Bandung",
                5,
                "Automatic",
                2000000.0,
                true,
                "Confirmed",
                addOns,
                now,
                now
        );

        assertEquals("BKG-001", dto.getId());
        assertEquals("VH-001", dto.getVehicleId());
        assertEquals("Toyota", dto.getVehicleBrand());
        assertEquals("Fortuner", dto.getVehicleModel());
        assertEquals(pickUp, dto.getPickUpTime());
        assertEquals(dropOff, dto.getDropOffTime());
        assertEquals("Jakarta", dto.getPickUpLocation());
        assertEquals("Bandung", dto.getDropOffLocation());
        assertEquals(5, dto.getCapacityNeeded());
        assertEquals("Automatic", dto.getTransmissionNeeded());
        assertEquals(2000000.0, dto.getTotalPrice());
        assertTrue(dto.getIncludeDriver());
        assertEquals("Confirmed", dto.getStatus());
        assertEquals(2, dto.getListOfAddOns().size());
        assertEquals(now, dto.getCreatedAt());
        assertEquals(now, dto.getUpdatedAt());
    }

    @Test
    void testSettersAndGetters() {
        RentalBookingResponseDTO dto = new RentalBookingResponseDTO();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime pickUp = now.plusDays(2);
        LocalDateTime dropOff = now.plusDays(5);

        dto.setId("BKG-002");
        dto.setVehicleId("VH-002");
        dto.setVehicleBrand("Honda");
        dto.setVehicleModel("CR-V");
        dto.setPickUpTime(pickUp);
        dto.setDropOffTime(dropOff);
        dto.setPickUpLocation("Surabaya");
        dto.setDropOffLocation("Malang");
        dto.setCapacityNeeded(7);
        dto.setTransmissionNeeded("Manual");
        dto.setTotalPrice(1500000.0);
        dto.setIncludeDriver(false);
        dto.setStatus("Pending");
        dto.setListOfAddOns(new ArrayList<>());
        dto.setCreatedAt(now);
        dto.setUpdatedAt(now);

        assertEquals("BKG-002", dto.getId());
        assertEquals("VH-002", dto.getVehicleId());
        assertEquals("Honda", dto.getVehicleBrand());
        assertEquals("CR-V", dto.getVehicleModel());
        assertEquals(pickUp, dto.getPickUpTime());
        assertEquals(dropOff, dto.getDropOffTime());
        assertEquals("Surabaya", dto.getPickUpLocation());
        assertEquals("Malang", dto.getDropOffLocation());
        assertEquals(7, dto.getCapacityNeeded());
        assertEquals("Manual", dto.getTransmissionNeeded());
        assertEquals(1500000.0, dto.getTotalPrice());
        assertFalse(dto.getIncludeDriver());
        assertEquals("Pending", dto.getStatus());
        assertEquals(0, dto.getListOfAddOns().size());
    }

    @Test
    void testWithAddOns() {
        RentalBookingResponseDTO dto = new RentalBookingResponseDTO();
        LocalDateTime now = LocalDateTime.now();
        List<RentalAddOnResponseDTO> addOns = Arrays.asList(
                new RentalAddOnResponseDTO(UUID.randomUUID(), "GPS", 50000.0, now, now),
                new RentalAddOnResponseDTO(UUID.randomUUID(), "WiFi", 75000.0, now, now),
                new RentalAddOnResponseDTO(UUID.randomUUID(), "Child Seat", 100000.0, now, now)
        );

        dto.setListOfAddOns(addOns);
        assertEquals(3, dto.getListOfAddOns().size());
        assertEquals("GPS", dto.getListOfAddOns().get(0).getName());
        assertEquals("WiFi", dto.getListOfAddOns().get(1).getName());
        assertEquals("Child Seat", dto.getListOfAddOns().get(2).getName());
    }

    @Test
    void testWithoutAddOns() {
        RentalBookingResponseDTO dto = new RentalBookingResponseDTO();
        dto.setListOfAddOns(new ArrayList<>());
        assertNotNull(dto.getListOfAddOns());
        assertEquals(0, dto.getListOfAddOns().size());
    }

    @Test
    void testNullAddOns() {
        RentalBookingResponseDTO dto = new RentalBookingResponseDTO();
        dto.setListOfAddOns(null);
        assertNull(dto.getListOfAddOns());
    }

    @Test
    void testIncludeDriverTrue() {
        RentalBookingResponseDTO dto = new RentalBookingResponseDTO();
        dto.setIncludeDriver(true);
        assertTrue(dto.getIncludeDriver());
    }

    @Test
    void testIncludeDriverFalse() {
        RentalBookingResponseDTO dto = new RentalBookingResponseDTO();
        dto.setIncludeDriver(false);
        assertFalse(dto.getIncludeDriver());
    }

    @Test
    void testDifferentStatuses() {
        RentalBookingResponseDTO pending = new RentalBookingResponseDTO();
        pending.setStatus("Pending");

        RentalBookingResponseDTO confirmed = new RentalBookingResponseDTO();
        confirmed.setStatus("Confirmed");

        RentalBookingResponseDTO ongoing = new RentalBookingResponseDTO();
        ongoing.setStatus("Ongoing");

        RentalBookingResponseDTO completed = new RentalBookingResponseDTO();
        completed.setStatus("Completed");

        assertEquals("Pending", pending.getStatus());
        assertEquals("Confirmed", confirmed.getStatus());
        assertEquals("Ongoing", ongoing.getStatus());
        assertEquals("Completed", completed.getStatus());
    }

    @Test
    void testDifferentTransmissions() {
        RentalBookingResponseDTO auto = new RentalBookingResponseDTO();
        auto.setTransmissionNeeded("Automatic");

        RentalBookingResponseDTO manual = new RentalBookingResponseDTO();
        manual.setTransmissionNeeded("Manual");

        assertEquals("Automatic", auto.getTransmissionNeeded());
        assertEquals("Manual", manual.getTransmissionNeeded());
    }

    @Test
    void testCapacityRange() {
        RentalBookingResponseDTO dto1 = new RentalBookingResponseDTO();
        dto1.setCapacityNeeded(2);

        RentalBookingResponseDTO dto2 = new RentalBookingResponseDTO();
        dto2.setCapacityNeeded(5);

        RentalBookingResponseDTO dto3 = new RentalBookingResponseDTO();
        dto3.setCapacityNeeded(7);

        assertEquals(2, dto1.getCapacityNeeded());
        assertEquals(5, dto2.getCapacityNeeded());
        assertEquals(7, dto3.getCapacityNeeded());
    }

    @Test
    void testPriceRange() {
        RentalBookingResponseDTO cheap = new RentalBookingResponseDTO();
        cheap.setTotalPrice(500000.0);

        RentalBookingResponseDTO moderate = new RentalBookingResponseDTO();
        moderate.setTotalPrice(1500000.0);

        RentalBookingResponseDTO expensive = new RentalBookingResponseDTO();
        expensive.setTotalPrice(5000000.0);

        assertEquals(500000.0, cheap.getTotalPrice());
        assertEquals(1500000.0, moderate.getTotalPrice());
        assertEquals(5000000.0, expensive.getTotalPrice());
    }

    @Test
    void testTimeValidation() {
        RentalBookingResponseDTO dto = new RentalBookingResponseDTO();
        LocalDateTime pickUp = LocalDateTime.now().plusDays(1);
        LocalDateTime dropOff = LocalDateTime.now().plusDays(3);

        dto.setPickUpTime(pickUp);
        dto.setDropOffTime(dropOff);

        assertTrue(dto.getDropOffTime().isAfter(dto.getPickUpTime()));
    }

    @Test
    void testDifferentLocations() {
        RentalBookingResponseDTO dto1 = new RentalBookingResponseDTO();
        dto1.setPickUpLocation("Jakarta");
        dto1.setDropOffLocation("Bandung");

        RentalBookingResponseDTO dto2 = new RentalBookingResponseDTO();
        dto2.setPickUpLocation("Surabaya");
        dto2.setDropOffLocation("Bali");

        assertEquals("Jakarta", dto1.getPickUpLocation());
        assertEquals("Bandung", dto1.getDropOffLocation());
        assertEquals("Surabaya", dto2.getPickUpLocation());
        assertEquals("Bali", dto2.getDropOffLocation());
    }

    @Test
    void testSamePickUpAndDropOff() {
        RentalBookingResponseDTO dto = new RentalBookingResponseDTO();
        dto.setPickUpLocation("Jakarta");
        dto.setDropOffLocation("Jakarta");

        assertEquals(dto.getPickUpLocation(), dto.getDropOffLocation());
    }

    @Test
    void testVehicleInformation() {
        RentalBookingResponseDTO dto = new RentalBookingResponseDTO();
        dto.setVehicleId("VH-123");
        dto.setVehicleBrand("Toyota");
        dto.setVehicleModel("Avanza");

        assertEquals("VH-123", dto.getVehicleId());
        assertEquals("Toyota", dto.getVehicleBrand());
        assertEquals("Avanza", dto.getVehicleModel());
    }

    @Test
    void testTimestamps() {
        RentalBookingResponseDTO dto = new RentalBookingResponseDTO();
        LocalDateTime created = LocalDateTime.now().minusDays(5);
        LocalDateTime updated = LocalDateTime.now();

        dto.setCreatedAt(created);
        dto.setUpdatedAt(updated);

        assertEquals(created, dto.getCreatedAt());
        assertEquals(updated, dto.getUpdatedAt());
        assertTrue(dto.getUpdatedAt().isAfter(dto.getCreatedAt()));
    }

    @Test
    void testNullValues() {
        RentalBookingResponseDTO dto = new RentalBookingResponseDTO();
        assertNull(dto.getId());
        assertNull(dto.getVehicleId());
        assertNull(dto.getStatus());
        assertNull(dto.getTotalPrice());
    }

    @Test
    void testEqualsAndHashCode() {
        LocalDateTime now = LocalDateTime.now();
        RentalBookingResponseDTO dto1 = new RentalBookingResponseDTO();
        dto1.setId("BKG-001");
        dto1.setStatus("Confirmed");
        dto1.setCreatedAt(now);

        RentalBookingResponseDTO dto2 = new RentalBookingResponseDTO();
        dto2.setId("BKG-001");
        dto2.setStatus("Confirmed");
        dto2.setCreatedAt(now);

        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    void testToString() {
        RentalBookingResponseDTO dto = new RentalBookingResponseDTO();
        dto.setId("BKG-001");
        dto.setVehicleBrand("Toyota");
        dto.setStatus("Confirmed");

        String toString = dto.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("BKG-001"));
    }

    @Test
    void testMultipleAddOnsCalculation() {
        RentalBookingResponseDTO dto = new RentalBookingResponseDTO();
        LocalDateTime now = LocalDateTime.now();
        List<RentalAddOnResponseDTO> addOns = Arrays.asList(
                new RentalAddOnResponseDTO(UUID.randomUUID(), "GPS", 50000.0, now, now),
                new RentalAddOnResponseDTO(UUID.randomUUID(), "Child Seat", 100000.0, now, now)
        );
        dto.setListOfAddOns(addOns);

        double addOnsTotal = dto.getListOfAddOns().stream()
                .mapToDouble(RentalAddOnResponseDTO::getPrice)
                .sum();

        assertEquals(150000.0, addOnsTotal);
    }

    @Test
    void testBookingDuration() {
        RentalBookingResponseDTO dto = new RentalBookingResponseDTO();
        LocalDateTime pickUp = LocalDateTime.of(2025, 11, 10, 10, 0);
        LocalDateTime dropOff = LocalDateTime.of(2025, 11, 13, 10, 0);

        dto.setPickUpTime(pickUp);
        dto.setDropOffTime(dropOff);

        long daysBetween = java.time.Duration.between(pickUp, dropOff).toDays();
        assertEquals(3, daysBetween);
    }
}
