package apap.ti._5.vehicle_rental_2306245592_be.restdto.response;

import apap.ti._5.vehicle_rental_2306245592_be.restdto.response.vehicle.VehicleResponseDTO;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class VehicleResponseDTOTest {

    @Test
    void testNoArgsConstructor() {
        VehicleResponseDTO dto = new VehicleResponseDTO();
        assertNotNull(dto);
    }

    @Test
    void testAllArgsConstructor() {
        LocalDateTime now = LocalDateTime.now();
        VehicleResponseDTO dto = new VehicleResponseDTO(
                "VH-001",
                1,
                "Test Vendor",
                "SUV",
                "Toyota",
                "Fortuner",
                2024,
                "Jakarta",
                "B1234ABC",
                7,
                "Automatic",
                "Diesel",
                850000.0,
                "Available",
                now,
                now
        );

        assertEquals("VH-001", dto.getId());
        assertEquals(1, dto.getRentalVendorId());
        assertEquals("Test Vendor", dto.getRentalVendorName());
        assertEquals("SUV", dto.getType());
        assertEquals("Toyota", dto.getBrand());
        assertEquals("Fortuner", dto.getModel());
        assertEquals(2024, dto.getYear());
        assertEquals("Jakarta", dto.getLocation());
        assertEquals("B1234ABC", dto.getLicensePlate());
        assertEquals(7, dto.getCapacity());
        assertEquals("Automatic", dto.getTransmission());
        assertEquals("Diesel", dto.getFuelType());
        assertEquals(850000.0, dto.getPrice());
        assertEquals("Available", dto.getStatus());
        assertEquals(now, dto.getCreatedAt());
        assertEquals(now, dto.getUpdatedAt());
    }

    @Test
    void testBuilder() {
        LocalDateTime now = LocalDateTime.now();
        VehicleResponseDTO dto = VehicleResponseDTO.builder()
                .id("VH-002")
                .rentalVendorId(2)
                .rentalVendorName("Vendor B")
                .type("Sedan")
                .brand("Honda")
                .model("Civic")
                .year(2023)
                .location("Bandung")
                .licensePlate("D5678XYZ")
                .capacity(5)
                .transmission("Manual")
                .fuelType("Gasoline")
                .price(600000.0)
                .status("Available")
                .createdAt(now)
                .updatedAt(now)
                .build();

        assertNotNull(dto);
        assertEquals("VH-002", dto.getId());
        assertEquals("Vendor B", dto.getRentalVendorName());
        assertEquals("Sedan", dto.getType());
        assertEquals(5, dto.getCapacity());
    }

    @Test
    void testSettersAndGetters() {
        VehicleResponseDTO dto = new VehicleResponseDTO();
        LocalDateTime now = LocalDateTime.now();

        dto.setId("VH-003");
        dto.setRentalVendorId(3);
        dto.setRentalVendorName("Vendor C");
        dto.setType("MPV");
        dto.setBrand("Suzuki");
        dto.setModel("Ertiga");
        dto.setYear(2022);
        dto.setLocation("Surabaya");
        dto.setLicensePlate("L9999MPV");
        dto.setCapacity(6);
        dto.setTransmission("Automatic");
        dto.setFuelType("Gasoline");
        dto.setPrice(500000.0);
        dto.setStatus("Booked");
        dto.setCreatedAt(now);
        dto.setUpdatedAt(now);

        assertEquals("VH-003", dto.getId());
        assertEquals(3, dto.getRentalVendorId());
        assertEquals("Vendor C", dto.getRentalVendorName());
        assertEquals("MPV", dto.getType());
        assertEquals("Suzuki", dto.getBrand());
        assertEquals("Ertiga", dto.getModel());
        assertEquals(2022, dto.getYear());
        assertEquals("Surabaya", dto.getLocation());
        assertEquals("L9999MPV", dto.getLicensePlate());
        assertEquals(6, dto.getCapacity());
        assertEquals("Automatic", dto.getTransmission());
        assertEquals("Gasoline", dto.getFuelType());
        assertEquals(500000.0, dto.getPrice());
        assertEquals("Booked", dto.getStatus());
        assertEquals(now, dto.getCreatedAt());
        assertEquals(now, dto.getUpdatedAt());
    }

    @Test
    void testEqualsAndHashCode() {
        LocalDateTime now = LocalDateTime.now();
        VehicleResponseDTO dto1 = VehicleResponseDTO.builder()
                .id("VH-001")
                .type("SUV")
                .brand("Toyota")
                .model("Fortuner")
                .price(850000.0)
                .createdAt(now)
                .build();

        VehicleResponseDTO dto2 = VehicleResponseDTO.builder()
                .id("VH-001")
                .type("SUV")
                .brand("Toyota")
                .model("Fortuner")
                .price(850000.0)
                .createdAt(now)
                .build();

        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    void testToString() {
        VehicleResponseDTO dto = VehicleResponseDTO.builder()
                .id("VH-001")
                .brand("Toyota")
                .model("Fortuner")
                .build();

        String toString = dto.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("VH-001"));
        assertTrue(toString.contains("Toyota"));
        assertTrue(toString.contains("Fortuner"));
    }

    @Test
    void testNullValues() {
        VehicleResponseDTO dto = new VehicleResponseDTO();
        assertNull(dto.getId());
        assertNull(dto.getBrand());
        assertNull(dto.getModel());
        assertNull(dto.getPrice());
    }

    @Test
    void testSetNullValues() {
        VehicleResponseDTO dto = new VehicleResponseDTO();
        dto.setId(null);
        dto.setBrand(null);
        dto.setModel(null);
        dto.setPrice(null);

        assertNull(dto.getId());
        assertNull(dto.getBrand());
        assertNull(dto.getModel());
        assertNull(dto.getPrice());
    }

    @Test
    void testDifferentVehicleTypes() {
        VehicleResponseDTO suv = VehicleResponseDTO.builder().type("SUV").build();
        VehicleResponseDTO sedan = VehicleResponseDTO.builder().type("Sedan").build();
        VehicleResponseDTO mpv = VehicleResponseDTO.builder().type("MPV").build();

        assertEquals("SUV", suv.getType());
        assertEquals("Sedan", sedan.getType());
        assertEquals("MPV", mpv.getType());
    }

    @Test
    void testDifferentTransmissionTypes() {
        VehicleResponseDTO auto = VehicleResponseDTO.builder().transmission("Automatic").build();
        VehicleResponseDTO manual = VehicleResponseDTO.builder().transmission("Manual").build();
        VehicleResponseDTO cvt = VehicleResponseDTO.builder().transmission("CVT").build();

        assertEquals("Automatic", auto.getTransmission());
        assertEquals("Manual", manual.getTransmission());
        assertEquals("CVT", cvt.getTransmission());
    }

    @Test
    void testDifferentFuelTypes() {
        VehicleResponseDTO diesel = VehicleResponseDTO.builder().fuelType("Diesel").build();
        VehicleResponseDTO gasoline = VehicleResponseDTO.builder().fuelType("Gasoline").build();
        VehicleResponseDTO electric = VehicleResponseDTO.builder().fuelType("Electric").build();

        assertEquals("Diesel", diesel.getFuelType());
        assertEquals("Gasoline", gasoline.getFuelType());
        assertEquals("Electric", electric.getFuelType());
    }

    @Test
    void testDifferentStatuses() {
        VehicleResponseDTO available = VehicleResponseDTO.builder().status("Available").build();
        VehicleResponseDTO booked = VehicleResponseDTO.builder().status("Booked").build();
        VehicleResponseDTO maintenance = VehicleResponseDTO.builder().status("Under Maintenance").build();

        assertEquals("Available", available.getStatus());
        assertEquals("Booked", booked.getStatus());
        assertEquals("Under Maintenance", maintenance.getStatus());
    }

    @Test
    void testCapacityRange() {
        VehicleResponseDTO small = VehicleResponseDTO.builder().capacity(2).build();
        VehicleResponseDTO medium = VehicleResponseDTO.builder().capacity(5).build();
        VehicleResponseDTO large = VehicleResponseDTO.builder().capacity(7).build();
        VehicleResponseDTO extraLarge = VehicleResponseDTO.builder().capacity(20).build();

        assertEquals(2, small.getCapacity());
        assertEquals(5, medium.getCapacity());
        assertEquals(7, large.getCapacity());
        assertEquals(20, extraLarge.getCapacity());
    }

    @Test
    void testPriceRange() {
        VehicleResponseDTO cheap = VehicleResponseDTO.builder().price(300000.0).build();
        VehicleResponseDTO moderate = VehicleResponseDTO.builder().price(700000.0).build();
        VehicleResponseDTO expensive = VehicleResponseDTO.builder().price(1500000.0).build();

        assertEquals(300000.0, cheap.getPrice());
        assertEquals(700000.0, moderate.getPrice());
        assertEquals(1500000.0, expensive.getPrice());
    }

    @Test
    void testYearRange() {
        VehicleResponseDTO old = VehicleResponseDTO.builder().year(2015).build();
        VehicleResponseDTO recent = VehicleResponseDTO.builder().year(2023).build();
        VehicleResponseDTO newest = VehicleResponseDTO.builder().year(2025).build();

        assertEquals(2015, old.getYear());
        assertEquals(2023, recent.getYear());
        assertEquals(2025, newest.getYear());
    }

    @Test
    void testDifferentLocations() {
        VehicleResponseDTO jakarta = VehicleResponseDTO.builder().location("Jakarta").build();
        VehicleResponseDTO bandung = VehicleResponseDTO.builder().location("Bandung").build();
        VehicleResponseDTO surabaya = VehicleResponseDTO.builder().location("Surabaya").build();
        VehicleResponseDTO bali = VehicleResponseDTO.builder().location("Bali").build();

        assertEquals("Jakarta", jakarta.getLocation());
        assertEquals("Bandung", bandung.getLocation());
        assertEquals("Surabaya", surabaya.getLocation());
        assertEquals("Bali", bali.getLocation());
    }

    @Test
    void testLicensePlateFormats() {
        VehicleResponseDTO jakarta = VehicleResponseDTO.builder().licensePlate("B1234ABC").build();
        VehicleResponseDTO bandung = VehicleResponseDTO.builder().licensePlate("D5678XYZ").build();
        VehicleResponseDTO surabaya = VehicleResponseDTO.builder().licensePlate("L9999MPV").build();

        assertEquals("B1234ABC", jakarta.getLicensePlate());
        assertEquals("D5678XYZ", bandung.getLicensePlate());
        assertEquals("L9999MPV", surabaya.getLicensePlate());
    }

    @Test
    void testBuilderWithPartialData() {
        VehicleResponseDTO dto = VehicleResponseDTO.builder()
                .id("VH-001")
                .brand("Toyota")
                .model("Avanza")
                .build();

        assertEquals("VH-001", dto.getId());
        assertEquals("Toyota", dto.getBrand());
        assertEquals("Avanza", dto.getModel());
        assertNull(dto.getPrice());
        assertNull(dto.getLocation());
    }

    @Test
    void testTimestampFields() {
        LocalDateTime created = LocalDateTime.now().minusDays(5);
        LocalDateTime updated = LocalDateTime.now();

        VehicleResponseDTO dto = VehicleResponseDTO.builder()
                .createdAt(created)
                .updatedAt(updated)
                .build();

        assertEquals(created, dto.getCreatedAt());
        assertEquals(updated, dto.getUpdatedAt());
        assertTrue(dto.getUpdatedAt().isAfter(dto.getCreatedAt()));
    }

    @Test
    void testVendorFields() {
        VehicleResponseDTO dto = VehicleResponseDTO.builder()
                .rentalVendorId(123)
                .rentalVendorName("Premium Rentals")
                .build();

        assertEquals(123, dto.getRentalVendorId());
        assertEquals("Premium Rentals", dto.getRentalVendorName());
    }
}
