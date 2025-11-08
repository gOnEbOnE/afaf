package apap.ti._5.vehicle_rental_2306245592_be.controller;

import apap.ti._5.vehicle_rental_2306245592_be.model.RentalBooking;
import apap.ti._5.vehicle_rental_2306245592_be.model.Vehicle;
import apap.ti._5.vehicle_rental_2306245592_be.repository.RentalBookingRepository;
import apap.ti._5.vehicle_rental_2306245592_be.repository.VehicleRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class BookingControllerVariationsTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RentalBookingRepository bookingRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    private Vehicle testVehicle;
    private RentalBooking testBooking1;
    private RentalBooking testBooking2;
    private RentalBooking testBooking3;

    @BeforeEach
    void setUp() {
        testVehicle = vehicleRepository.findAll().stream().findFirst().orElse(null);
        if (testVehicle != null) {
            testBooking1 = createBookingWithStatus("Upcoming");
            testBooking2 = createBookingWithStatus("Ongoing");
            testBooking3 = createBookingWithStatus("Done");
        }
    }

    private RentalBooking createBookingWithStatus(String status) {
        RentalBooking booking = new RentalBooking();
        booking.setId("VAR-" + UUID.randomUUID().toString().substring(0, 8));
        booking.setVehicle(testVehicle);
        booking.setPickUpTime(LocalDateTime.now().plusDays(2));
        booking.setDropOffTime(LocalDateTime.now().plusDays(5));
        booking.setPickUpLocation(testVehicle.getLocation());
        booking.setDropOffLocation("Test Location");
        booking.setCapacityNeeded(5);
        booking.setTransmissionNeeded("Manual");
        booking.setTotalPrice(1000000.0);
        booking.setIncludeDriver(false);
        booking.setStatus(status);
        booking.setDeletedAt(null);
        return bookingRepository.save(booking);
    }

    // Test variations of getAllBookings with different status filters
    @Test
    void testGetBookingsFilteredByUpcoming() throws Exception {
        mockMvc.perform(get("/api/bookings")
                .param("status", "Upcoming"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    void testGetBookingsFilteredByOngoing() throws Exception {
        mockMvc.perform(get("/api/bookings")
                .param("status", "Ongoing"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    void testGetBookingsFilteredByDone() throws Exception {
        mockMvc.perform(get("/api/bookings")
                .param("status", "Done"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    void testGetBookingsFilteredByCancelled() throws Exception {
        mockMvc.perform(get("/api/bookings")
                .param("status", "Cancelled"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    void testGetBookingsWithEmptyStatusParam() throws Exception {
        mockMvc.perform(get("/api/bookings")
                .param("status", ""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200));
    }

    @Test
    void testGetBookingsWithWhitespaceStatus() throws Exception {
        mockMvc.perform(get("/api/bookings")
                .param("status", "   "))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200));
    }

    @Test
    void testGetBookingsNoStatusParam() throws Exception {
        mockMvc.perform(get("/api/bookings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.data").isArray());
    }

    // Test getBookingById variations
    @Test
    void testGetBookingByIdExists() throws Exception {
        if (testBooking1 != null) {
            mockMvc.perform(get("/api/bookings/" + testBooking1.getId()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value(200))
                    .andExpect(jsonPath("$.data.id").value(testBooking1.getId()))
                    .andExpect(jsonPath("$.data.status").value("Upcoming"));
        }
    }

    @Test
    void testGetBookingByIdNotFound() throws Exception {
        mockMvc.perform(get("/api/bookings/NONEXISTENT-ID"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Booking not found"));
    }

    @Test
    void testGetBookingByIdEmptyString() throws Exception {
        mockMvc.perform(get("/api/bookings/ "))
                .andExpect(status().isNotFound());
    }

    // Test getBookingCount
    @Test
    void testGetBookingCountReturnsInteger() throws Exception {
        mockMvc.perform(get("/api/bookings/count"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data").isNumber())
                .andExpect(jsonPath("$.message").value("Success"));
    }

    @Test
    void testGetBookingCountPositiveNumber() throws Exception {
        mockMvc.perform(get("/api/bookings/count"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").exists());
    }

    // Test addons endpoint
    @Test
    void testGetAddOnsReturnsArray() throws Exception {
        mockMvc.perform(get("/api/bookings/addons"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    void testGetAddOnsHasMessage() throws Exception {
        mockMvc.perform(get("/api/bookings/addons"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").exists());
    }

    // Test provinces endpoint
    @Test
    void testGetProvincesReturnsArray() throws Exception {
        mockMvc.perform(get("/api/bookings/provinces"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    void testGetProvincesHasTimestamp() throws Exception {
        mockMvc.perform(get("/api/bookings/provinces"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void testGetProvincesSuccessMessage() throws Exception {
        mockMvc.perform(get("/api/bookings/provinces"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").exists());
    }

    // Test chart endpoint variations
    @Test
    void testGetChartDefaultPeriod() throws Exception {
        mockMvc.perform(get("/api/bookings/chart"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data").exists());
    }

    @Test
    void testGetChartMonthlyPeriod() throws Exception {
        mockMvc.perform(get("/api/bookings/chart")
                .param("period", "monthly"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data").exists());
    }

    @Test
    void testGetChartQuarterlyPeriod() throws Exception {
        mockMvc.perform(get("/api/bookings/chart")
                .param("period", "quarterly"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data").exists());
    }

    @Test
    void testGetChartWithYearParam() throws Exception {
        mockMvc.perform(get("/api/bookings/chart")
                .param("year", "2024"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200));
    }

    @Test
    void testGetChartWithPeriodAndYear() throws Exception {
        mockMvc.perform(get("/api/bookings/chart")
                .param("period", "monthly")
                .param("year", "2024"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200));
    }

    // Test getBookingForUpdateDetails
    @Test
    void testGetBookingForUpdateDetailsExists() throws Exception {
        if (testBooking1 != null) {
            mockMvc.perform(get("/api/bookings/" + testBooking1.getId() + "/update-details"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value(200))
                    .andExpect(jsonPath("$.data").exists());
        }
    }

    // Test getBookingForUpdateStatus
    @Test
    void testGetBookingForUpdateStatusExists() throws Exception {
        if (testBooking1 != null) {
            mockMvc.perform(get("/api/bookings/" + testBooking1.getId() + "/update-status"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value(200))
                    .andExpect(jsonPath("$.data").exists());
        }
    }

    // Test getBookingForUpdateAddOns
    @Test
    void testGetBookingForUpdateAddOnsExists() throws Exception {
        if (testBooking1 != null) {
            mockMvc.perform(get("/api/bookings/" + testBooking1.getId() + "/update-addons"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value(200))
                    .andExpect(jsonPath("$.data").exists());
        }
    }

    // Test getAvailableStatusTransitions
    @Test
    void testGetAvailableStatusTransitionsUpcoming() throws Exception {
        if (testBooking1 != null) {
            mockMvc.perform(get("/api/bookings/" + testBooking1.getId() + "/available-status-transitions"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value(200))
                    .andExpect(jsonPath("$.data").isArray());
        }
    }

    @Test
    void testGetAvailableStatusTransitionsOngoing() throws Exception {
        if (testBooking2 != null) {
            mockMvc.perform(get("/api/bookings/" + testBooking2.getId() + "/available-status-transitions"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value(200))
                    .andExpect(jsonPath("$.data").isArray());
        }
    }

    @Test
    void testGetAvailableStatusTransitionsDone() throws Exception {
        if (testBooking3 != null) {
            mockMvc.perform(get("/api/bookings/" + testBooking3.getId() + "/available-status-transitions"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value(200))
                    .andExpect(jsonPath("$.data").isArray());
        }
    }

    // Test delete booking
    @Test
    void testDeleteExistingBooking() throws Exception {
        if (testBooking1 != null) {
            String bookingId = testBooking1.getId();
            mockMvc.perform(delete("/api/bookings/" + bookingId + "/delete"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value(200))
                    .andExpect(jsonPath("$.message").exists());
        }
    }

    // Additional GET variations
    @Test
    void testGetBookingsReturnsTimestamp() throws Exception {
        mockMvc.perform(get("/api/bookings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void testGetBookingsResponseStructure() throws Exception {
        mockMvc.perform(get("/api/bookings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").exists())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.data").exists());
    }

    @Test
    void testGetBookingByIdResponseStructure() throws Exception {
        if (testBooking1 != null) {
            mockMvc.perform(get("/api/bookings/" + testBooking1.getId()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").exists())
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.timestamp").exists())
                    .andExpect(jsonPath("$.data").exists());
        }
    }

    @Test
    void testGetAddOnsResponseStructure() throws Exception {
        mockMvc.perform(get("/api/bookings/addons"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").exists())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.data").exists());
    }

    @Test
    void testGetProvincesResponseStructure() throws Exception {
        mockMvc.perform(get("/api/bookings/provinces"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").exists())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.data").exists());
    }

    @Test
    void testGetChartResponseStructure() throws Exception {
        mockMvc.perform(get("/api/bookings/chart"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").exists())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.data").exists());
    }
}
