package apap.ti._5.vehicle_rental_2306245592_be.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class LocationServiceTest {

    @Autowired
    private LocationService locationService;

    @Test
    void testInitializeProvinces() {
        // This will call the actual API or fallback data
        assertDoesNotThrow(() -> locationService.initializeProvinces());
    }

    @Test
    void testGetProvinces() {
        List<String> provinces = locationService.getProvinceList();
        assertNotNull(provinces);
        assertFalse(provinces.isEmpty());
        assertTrue(provinces.size() > 0);
    }

    @Test
    void testProvincesContainExpectedValues() {
        List<String> provinces = locationService.getProvinceList();
        
        // Should contain some common provinces
        boolean hasIndonesianProvinces = provinces.stream()
            .anyMatch(p -> p.contains("Jakarta") || p.contains("Jawa") || p.contains("Bali"));
        
        assertTrue(hasIndonesianProvinces);
    }
}
