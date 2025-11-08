package apap.ti._5.vehicle_rental_2306245592_be.restdto.response;

import apap.ti._5.vehicle_rental_2306245592_be.restdto.response.RentalAddOn.RentalAddOnResponseDTO;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class RentalAddOnResponseDTOTest {

    @Test
    void testNoArgsConstructor() {
        RentalAddOnResponseDTO dto = new RentalAddOnResponseDTO();
        assertNotNull(dto);
    }

    @Test
    void testAllArgsConstructor() {
        UUID id = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        RentalAddOnResponseDTO dto = new RentalAddOnResponseDTO(
                id,
                "GPS Navigation",
                50000.0,
                now,
                now
        );

        assertEquals(id, dto.getId());
        assertEquals("GPS Navigation", dto.getName());
        assertEquals(50000.0, dto.getPrice());
        assertEquals(now, dto.getCreatedAt());
        assertEquals(now, dto.getUpdatedAt());
    }

    @Test
    void testSettersAndGetters() {
        RentalAddOnResponseDTO dto = new RentalAddOnResponseDTO();
        UUID id = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        dto.setId(id);
        dto.setName("Child Seat");
        dto.setPrice(100000.0);
        dto.setCreatedAt(now);
        dto.setUpdatedAt(now);

        assertEquals(id, dto.getId());
        assertEquals("Child Seat", dto.getName());
        assertEquals(100000.0, dto.getPrice());
        assertEquals(now, dto.getCreatedAt());
        assertEquals(now, dto.getUpdatedAt());
    }

    @Test
    void testDifferentAddOnTypes() {
        RentalAddOnResponseDTO gps = new RentalAddOnResponseDTO();
        gps.setName("GPS Navigation");
        gps.setPrice(50000.0);

        RentalAddOnResponseDTO wifi = new RentalAddOnResponseDTO();
        wifi.setName("Portable WiFi");
        wifi.setPrice(75000.0);

        RentalAddOnResponseDTO childSeat = new RentalAddOnResponseDTO();
        childSeat.setName("Child Seat");
        childSeat.setPrice(100000.0);

        assertEquals("GPS Navigation", gps.getName());
        assertEquals(50000.0, gps.getPrice());
        assertEquals("Portable WiFi", wifi.getName());
        assertEquals(75000.0, wifi.getPrice());
        assertEquals("Child Seat", childSeat.getName());
        assertEquals(100000.0, childSeat.getPrice());
    }

    @Test
    void testPriceRange() {
        RentalAddOnResponseDTO cheap = new RentalAddOnResponseDTO();
        cheap.setPrice(25000.0);

        RentalAddOnResponseDTO moderate = new RentalAddOnResponseDTO();
        moderate.setPrice(75000.0);

        RentalAddOnResponseDTO expensive = new RentalAddOnResponseDTO();
        expensive.setPrice(200000.0);

        assertEquals(25000.0, cheap.getPrice());
        assertEquals(75000.0, moderate.getPrice());
        assertEquals(200000.0, expensive.getPrice());
    }

    @Test
    void testEqualsAndHashCode() {
        UUID id = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        RentalAddOnResponseDTO dto1 = new RentalAddOnResponseDTO(id, "GPS", 50000.0, now, now);
        RentalAddOnResponseDTO dto2 = new RentalAddOnResponseDTO(id, "GPS", 50000.0, now, now);

        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    void testToString() {
        RentalAddOnResponseDTO dto = new RentalAddOnResponseDTO();
        dto.setName("GPS Navigation");
        dto.setPrice(50000.0);

        String toString = dto.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("GPS Navigation"));
        assertTrue(toString.contains("50000"));
    }

    @Test
    void testNullValues() {
        RentalAddOnResponseDTO dto = new RentalAddOnResponseDTO();
        assertNull(dto.getId());
        assertNull(dto.getName());
        assertNull(dto.getPrice());
        assertNull(dto.getCreatedAt());
        assertNull(dto.getUpdatedAt());
    }

    @Test
    void testTimestamps() {
        RentalAddOnResponseDTO dto = new RentalAddOnResponseDTO();
        LocalDateTime created = LocalDateTime.now().minusDays(10);
        LocalDateTime updated = LocalDateTime.now();

        dto.setCreatedAt(created);
        dto.setUpdatedAt(updated);

        assertEquals(created, dto.getCreatedAt());
        assertEquals(updated, dto.getUpdatedAt());
        assertTrue(dto.getUpdatedAt().isAfter(dto.getCreatedAt()));
    }

    @Test
    void testUniqueIds() {
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();

        RentalAddOnResponseDTO dto1 = new RentalAddOnResponseDTO();
        dto1.setId(id1);

        RentalAddOnResponseDTO dto2 = new RentalAddOnResponseDTO();
        dto2.setId(id2);

        assertNotEquals(dto1.getId(), dto2.getId());
    }

    @Test
    void testZeroPrice() {
        RentalAddOnResponseDTO dto = new RentalAddOnResponseDTO();
        dto.setPrice(0.0);
        assertEquals(0.0, dto.getPrice());
    }

    @Test
    void testVeryHighPrice() {
        RentalAddOnResponseDTO dto = new RentalAddOnResponseDTO();
        dto.setPrice(1000000.0);
        assertEquals(1000000.0, dto.getPrice());
    }

    @Test
    void testEmptyName() {
        RentalAddOnResponseDTO dto = new RentalAddOnResponseDTO();
        dto.setName("");
        assertEquals("", dto.getName());
    }

    @Test
    void testLongName() {
        RentalAddOnResponseDTO dto = new RentalAddOnResponseDTO();
        String longName = "Premium GPS Navigation System with Real-Time Traffic Updates";
        dto.setName(longName);
        assertEquals(longName, dto.getName());
    }

    @Test
    void testMultipleAddOnsComparison() {
        RentalAddOnResponseDTO addon1 = new RentalAddOnResponseDTO();
        addon1.setName("GPS");
        addon1.setPrice(50000.0);

        RentalAddOnResponseDTO addon2 = new RentalAddOnResponseDTO();
        addon2.setName("WiFi");
        addon2.setPrice(75000.0);

        assertTrue(addon2.getPrice() > addon1.getPrice());
    }
}
