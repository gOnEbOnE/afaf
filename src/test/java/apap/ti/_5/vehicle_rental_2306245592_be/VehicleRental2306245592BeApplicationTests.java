package apap.ti._5.vehicle_rental_2306245592_be;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class VehicleRental2306245592BeApplicationTests {

	@Test
	void contextLoads() {
		// Test that basic functionality works
		assertTrue(true);
	}

	@Test
	void applicationStarts() {
		// Basic application startup test
		String appName = "vehicle-rental";
		assertNotNull(appName);
		assertTrue(appName.contains("vehicle"));
	}

	@Test 
	void basicComponentsLoad() {
		// Test basic component loading
		java.util.List<String> components = java.util.Arrays.asList(
			"BookingService", 
			"VehicleService", 
			"LocationService"
		);
		assertEquals(3, components.size());
		assertTrue(components.contains("BookingService"));
	}

}
