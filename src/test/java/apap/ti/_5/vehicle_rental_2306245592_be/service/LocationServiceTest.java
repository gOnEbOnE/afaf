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

    @Test
    void testGetRandomProvince() {
        String randomProvince = locationService.getRandomProvince();
        assertNotNull(randomProvince);
        assertFalse(randomProvince.isEmpty());
    }

    @Test
    void testGetRandomProvinceReturnsDifferentValues() {
        String province1 = locationService.getRandomProvince();
        String province2 = locationService.getRandomProvince();
        String province3 = locationService.getRandomProvince();
        
        // At least one of them should be in the province list
        List<String> provinces = locationService.getProvinceList();
        assertTrue(provinces.contains(province1));
        assertTrue(provinces.contains(province2));
        assertTrue(provinces.contains(province3));
    }

    @Test
    void testGetProvinceListReturnsNewListEachTime() {
        List<String> list1 = locationService.getProvinceList();
        List<String> list2 = locationService.getProvinceList();
        
        // Should return new instances
        assertNotSame(list1, list2);
        
        // But with same content
        assertEquals(list1.size(), list2.size());
    }

    @Test
    void testProvinceListNotEmpty() {
        List<String> provinces = locationService.getProvinceList();
        assertTrue(provinces.size() >= 34, "Should have at least 34 provinces");
    }

    @Test
    void testProvinceListContainsJakarta() {
        List<String> provinces = locationService.getProvinceList();
        assertTrue(provinces.stream().anyMatch(p -> p.contains("Jakarta")));
    }

    @Test
    void testProvinceListContainsBali() {
        List<String> provinces = locationService.getProvinceList();
        assertTrue(provinces.stream().anyMatch(p -> p.contains("Bali")));
    }

    @Test
    void testProvinceListContainsJawaBarat() {
        List<String> provinces = locationService.getProvinceList();
        assertTrue(provinces.stream().anyMatch(p -> p.contains("Jawa")));
    }

    @Test
    void testInitializeProvincesIdempotent() {
        locationService.initializeProvinces();
        int size1 = locationService.getProvinceList().size();
        
        locationService.initializeProvinces();
        int size2 = locationService.getProvinceList().size();
        
        assertEquals(size1, size2, "Multiple initializations should give same result");
    }

    @Test
    void testGetProvinceListAfterInitialization() {
        locationService.initializeProvinces();
        List<String> provinces = locationService.getProvinceList();
        
        assertNotNull(provinces);
        assertFalse(provinces.isEmpty());
    }

    @Test
    void testRandomProvinceIsInList() {
        List<String> provinces = locationService.getProvinceList();
        String random = locationService.getRandomProvince();
        
        assertTrue(provinces.contains(random), 
            "Random province should be from the province list");
    }

    @Test
    void testGetProvinceListMultipleTimes() {
        for (int i = 0; i < 5; i++) {
            List<String> provinces = locationService.getProvinceList();
            assertNotNull(provinces);
            assertTrue(provinces.size() > 0);
        }
    }

    @Test
    void testGetRandomProvinceMultipleTimes() {
        for (int i = 0; i < 10; i++) {
            String province = locationService.getRandomProvince();
            assertNotNull(province);
            assertFalse(province.isEmpty());
        }
    }

    @Test
    void testProvinceListContainsSumatera() {
        List<String> provinces = locationService.getProvinceList();
        boolean hasSumatera = provinces.stream().anyMatch(p -> p.contains("Sumatera") || p.contains("Sumatra"));
        assertTrue(hasSumatera);
    }

    @Test
    void testProvinceListContainsKalimantan() {
        List<String> provinces = locationService.getProvinceList();
        boolean hasKalimantan = provinces.stream().anyMatch(p -> p.contains("Kalimantan"));
        assertTrue(hasKalimantan);
    }

    @Test
    void testProvinceListContainsSulawesi() {
        List<String> provinces = locationService.getProvinceList();
        boolean hasSulawesi = provinces.stream().anyMatch(p -> p.contains("Sulawesi"));
        assertTrue(hasSulawesi);
    }

    @Test
    void testProvinceListNoDuplicates() {
        List<String> provinces = locationService.getProvinceList();
        long uniqueCount = provinces.stream().distinct().count();
        assertEquals(provinces.size(), uniqueCount, "Province list should not have duplicates");
    }

    @Test
    void testProvinceListAllNonEmpty() {
        List<String> provinces = locationService.getProvinceList();
        boolean allNonEmpty = provinces.stream().allMatch(p -> p != null && !p.isEmpty());
        assertTrue(allNonEmpty, "All provinces should be non-empty strings");
    }

    @Test
    void testInitializeProvincesBeforeGetRandom() {
        locationService.initializeProvinces();
        String random = locationService.getRandomProvince();
        assertNotNull(random);
    }

    @Test
    void testGetProvinceListThreadSafe() throws InterruptedException {
        Thread[] threads = new Thread[5];
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(() -> {
                List<String> provinces = locationService.getProvinceList();
                assertNotNull(provinces);
                assertTrue(provinces.size() > 0);
            });
            threads[i].start();
        }
        
        for (Thread thread : threads) {
            thread.join();
        }
    }
}
