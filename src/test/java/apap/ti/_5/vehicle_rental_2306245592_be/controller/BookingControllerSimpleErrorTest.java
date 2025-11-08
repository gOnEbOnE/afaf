package apap.ti._5.vehicle_rental_2306245592_be.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Simple error scenario tests targeting BookingController catch blocks
 * Goal: Increase coverage to 80%+ by hitting error paths
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class BookingControllerSimpleErrorTest {

    @Autowired
    private MockMvc mockMvc;

    // Test POST /api/bookings/finalize with null bookingDTO (RuntimeException path)
    // @Test
    // void testFinalizeWithNullBookingDTO() throws Exception {
    //     String requestBody = """
    //         {
    //             "bookingDTO": null,
    //             "addOnsDTO": []
    //         }
    //         """;

    //     mockMvc.perform(post("/api/bookings/finalize")
    //             .contentType(MediaType.APPLICATION_JSON)
    //             .content(requestBody))
    //             .andExpect(status().isBadRequest())
    //             .andExpect(jsonPath("$.status").value(400));
    // }

    // Test POST /api/bookings/finalize with null addOnsDTO (RuntimeException path)
    @Test
    void testFinalizeWithNullAddOnsDTO() throws Exception {
        String requestBody = """
            {
                "bookingDTO": {
                    "vehicleId": "test-id",
                    "pickupLocationId": "loc-id",
                    "dropoffLocationId": "loc-id",
                    "startDate": "2025-01-15",
                    "endDate": "2025-01-20",
                    "customerName": "Test",
                    "customerEmail": "test@test.com",
                    "customerPhone": "081234567890",
                    "needDriver": false
                },
                "addOnsDTO": null
            }
            """;

        mockMvc.perform(post("/api/bookings/finalize")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Data booking atau add-ons tidak boleh kosong"));
    }

    // Test POST /api/bookings/finalize with invalid vehicleId (RuntimeException path)
    // @Test
    // void testFinalizeWithInvalidVehicleId() throws Exception {
    //     String requestBody = """
    //         {
    //             "bookingDTO": {
    //                 "vehicleId": "non-existent-vehicle-id-999",
    //                 "pickupLocationId": "non-existent-location-999",
    //                 "dropoffLocationId": "non-existent-location-999",
    //                 "startDate": "2025-01-15",
    //                 "endDate": "2025-01-20",
    //                 "customerName": "Test Customer",
    //                 "customerEmail": "test@example.com",
    //                 "customerPhone": "081234567890",
    //                 "needDriver": false
    //             },
    //             "addOnsDTO": []
    //         }
    //         """;

    //     mockMvc.perform(post("/api/bookings/finalize")
    //             .contentType(MediaType.APPLICATION_JSON)
    //             .content(requestBody))
    //             .andExpect(status().isBadRequest())
    //             .andExpect(jsonPath("$.status").value(400));
    // }

    // Test DELETE /api/bookings/{id} with invalid ID (RuntimeException path)
    @Test
    void testDeleteBookingWithInvalidId() throws Exception {
        mockMvc.perform(delete("/api/bookings/non-existent-booking-id-999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    // Test GET /api/bookings/{id}/update-details with invalid ID (RuntimeException path)
    // @Test
    // void testGetForUpdateDetailsWithInvalidId() throws Exception {
    //     mockMvc.perform(get("/api/bookings/invalid-id-999/update-details"))
    //             .andExpect(status().isNotFound())
    //             .andExpect(jsonPath("$.status").value(404));
    // }

    // Test GET /api/bookings/{id}/update-status with invalid ID (RuntimeException path)
    // @Test
    // void testGetForUpdateStatusWithInvalidId() throws Exception {
    //     mockMvc.perform(get("/api/bookings/invalid-id-999/update-status"))
    //             .andExpect(status().isNotFound())
    //             .andExpect(jsonPath("$.status").value(404));
    // }

    // Test GET /api/bookings/{id}/update-addons with invalid ID (RuntimeException path)
    // @Test
    // void testGetForUpdateAddOnsWithInvalidId() throws Exception {
    //     mockMvc.perform(get("/api/bookings/invalid-id-999/update-addons"))
    //             .andExpect(status().isNotFound())
    //             .andExpect(jsonPath("$.status").value(404));
    // }

    // Test POST /api/bookings/search with invalid dates (RuntimeException path)
    @Test
    void testSearchWithEndBeforeStart() throws Exception {
        String requestBody = """
            {
                "pickupLocationId": "any-location",
                "dropoffLocationId": "any-location",
                "startDate": "2025-01-20",
                "endDate": "2025-01-15",
                "needDriver": false
            }
            """;

        mockMvc.perform(post("/api/bookings/search")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    // Test POST /api/bookings/search with missing fields (validation error)
    @Test
    void testSearchWithMissingFields() throws Exception {
        String requestBody = """
            {
                "startDate": "2025-01-15",
                "endDate": "2025-01-20"
            }
            """;

        mockMvc.perform(post("/api/bookings/search")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    // Test GET /api/bookings/chart with invalid period (RuntimeException path)
    @Test
    void testChartWithInvalidPeriod() throws Exception {
        mockMvc.perform(get("/api/bookings/chart")
                .param("period", "INVALID_PERIOD_VALUE")
                .param("year", "2024"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    // Test GET /api/bookings with multiple filter parameters
    @Test
    void testGetAllBookingsWithAllFilters() throws Exception {
        mockMvc.perform(get("/api/bookings")
                .param("status", "Upcoming")
                .param("customerName", "Test")
                .param("vehicleName", "Car")
                .param("startDate", "2025-01-01")
                .param("endDate", "2025-12-31"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data").isArray());
    }

    // Test GET /api/bookings/addons - simple endpoint coverage
    @Test
    void testGetAllAddOns() throws Exception {
        mockMvc.perform(get("/api/bookings/addons"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data").isArray());
    }

    // Test GET /api/bookings/current-time - simple endpoint coverage
    @Test
    void testGetCurrentTime() throws Exception {
        mockMvc.perform(get("/api/bookings/current-time"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data").exists());
    }

    // Test POST /api/bookings/search with capacity filter
    // @Test
    // void testSearchWithCapacityFilter() throws Exception {
    //     String requestBody = """
    //         {
    //             "pickupLocationId": "test-location-id",
    //             "dropoffLocationId": "test-location-id",
    //             "startDate": "2025-04-01",
    //             "endDate": "2025-04-05",
    //             "needDriver": false,
    //             "capacity": 7
    //         }
    //         """;

    //     mockMvc.perform(post("/api/bookings/search")
    //             .contentType(MediaType.APPLICATION_JSON)
    //             .content(requestBody))
    //             .andExpect(status().isOk())
    //             .andExpect(jsonPath("$.status").value(200));
    // }

    // Test POST /api/bookings/search with transmission filter
    // @Test
    // void testSearchWithTransmissionFilter() throws Exception {
    //     String requestBody = """
    //         {
    //             "pickupLocationId": "test-location-id",
    //             "dropoffLocationId": "test-location-id",
    //             "startDate": "2025-05-01",
    //             "endDate": "2025-05-05",
    //             "needDriver": true,
    //             "transmission": "Automatic"
    //         }
    //         """;

    //     mockMvc.perform(post("/api/bookings/search")
    //             .contentType(MediaType.APPLICATION_JSON)
    //             .content(requestBody))
    //             .andExpect(status().isOk())
    //             .andExpect(jsonPath("$.status").value(200));
    // }

    // Test POST /api/bookings/search with price range
    // @Test
    // void testSearchWithPriceRange() throws Exception {
    //     String requestBody = """
    //         {
    //             "pickupLocationId": "test-location-id",
    //             "dropoffLocationId": "test-location-id",
    //             "startDate": "2025-06-01",
    //             "endDate": "2025-06-05",
    //             "needDriver": false,
    //             "minPrice": 300000,
    //             "maxPrice": 500000
    //         }
    //         """;

    //     mockMvc.perform(post("/api/bookings/search")
    //             .contentType(MediaType.APPLICATION_JSON)
    //             .content(requestBody))
    //             .andExpect(status().isOk())
    //             .andExpect(jsonPath("$.status").value(200));
    // }

    // Test POST /api/bookings/search with fuelType filter
    // @Test
    // void testSearchWithFuelTypeFilter() throws Exception {
    //     String requestBody = """
    //         {
    //             "pickupLocationId": "test-location-id",
    //             "dropoffLocationId": "test-location-id",
    //             "startDate": "2025-07-01",
    //             "endDate": "2025-07-05",
    //             "needDriver": false,
    //             "fuelType": "Bensin"
    //         }
    //         """;

    //     mockMvc.perform(post("/api/bookings/search")
    //             .contentType(MediaType.APPLICATION_JSON)
    //             .content(requestBody))
    //             .andExpect(status().isOk())
    //             .andExpect(jsonPath("$.status").value(200));
    // }

    // Test GET /api/bookings/chart with DAILY period
    // @Test
    // void testChartWithDailyPeriod() throws Exception {
    //     mockMvc.perform(get("/api/bookings/chart")
    //             .param("period", "DAILY")
    //             .param("year", "2024"))
    //             .andExpect(status().isOk())
    //             .andExpect(jsonPath("$.status").value(200))
    //             .andExpect(jsonPath("$.data").exists());
    // }

    // Test GET /api/bookings/chart with MONTHLY period
    @Test
    void testChartWithMonthlyPeriod() throws Exception {
        mockMvc.perform(get("/api/bookings/chart")
                .param("period", "MONTHLY")
                .param("year", "2024"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data").exists());
    }

    // Test GET /api/bookings with status filter only
    @Test
    void testGetBookingsByStatus() throws Exception {
        mockMvc.perform(get("/api/bookings")
                .param("status", "Done"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200));
    }

    // Test GET /api/bookings with customer name filter only
    @Test
    void testGetBookingsByCustomerName() throws Exception {
        mockMvc.perform(get("/api/bookings")
                .param("customerName", "John"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200));
    }

    // Test GET /api/bookings with vehicle name filter only
    @Test
    void testGetBookingsByVehicleName() throws Exception {
        mockMvc.perform(get("/api/bookings")
                .param("vehicleName", "Avanza"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200));
    }
}
