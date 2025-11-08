package apap.ti._5.vehicle_rental_2306245592_be.controller;

import apap.ti._5.vehicle_rental_2306245592_be.model.RentalBooking;
import apap.ti._5.vehicle_rental_2306245592_be.model.Vehicle;
import apap.ti._5.vehicle_rental_2306245592_be.repository.RentalBookingRepository;
import apap.ti._5.vehicle_rental_2306245592_be.repository.VehicleRepository;
import apap.ti._5.vehicle_rental_2306245592_be.repository.RentalAddOnRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class BookingControllerPutEndpointsTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RentalBookingRepository bookingRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private RentalAddOnRepository addOnRepository;

    private Vehicle testVehicle;
    private RentalBooking upcomingBooking;
    private RentalBooking ongoingBooking;
    private RentalBooking doneBooking;

    @BeforeEach
    void setUp() {
        testVehicle = vehicleRepository.findAll().stream().findFirst().orElse(null);
        if (testVehicle != null) {
            upcomingBooking = createBookingWithStatus("Upcoming");
            ongoingBooking = createBookingWithStatus("Ongoing");
            doneBooking = createBookingWithStatus("Done");
        }
    }

    private RentalBooking createBookingWithStatus(String status) {
        RentalBooking booking = new RentalBooking();
        booking.setId("PUT-" + UUID.randomUUID().toString().substring(0, 8));
        booking.setVehicle(testVehicle);
        booking.setPickUpTime(LocalDateTime.now().plusDays(5));
        booking.setDropOffTime(LocalDateTime.now().plusDays(8));
        booking.setPickUpLocation(testVehicle.getLocation());
        booking.setDropOffLocation("Test Drop Location");
        booking.setCapacityNeeded(5);
        booking.setTransmissionNeeded("Manual");
        booking.setTotalPrice(1500000.0);
        booking.setIncludeDriver(false);
        booking.setStatus(status);
        booking.setDeletedAt(null);
        return bookingRepository.save(booking);
    }

    // Test PUT /bookings/update-details
    // @Test
    // void testUpdateBookingDetailsSuccess() throws Exception {
    //     if (upcomingBooking != null) {
    //         LocalDateTime newPickUp = LocalDateTime.now().plusDays(10);
    //         String requestBody = String.format("""
    //             {
    //                 "id": "%s",
    //                 "pickUpTime": "%s",
    //                 "dropOffTime": "%s",
    //                 "pickUpLocation": "New Pickup Location",
    //                 "dropOffLocation": "New Drop Location"
    //             }
    //             """, 
    //             upcomingBooking.getId(),
    //             newPickUp.toString(),
    //             newPickUp.plusDays(3).toString());

    //         mockMvc.perform(put("/api/bookings/update-details")
    //                 .contentType(MediaType.APPLICATION_JSON)
    //                 .content(requestBody))
    //                 .andExpect(status().isOk())
    //                 .andExpect(jsonPath("$.status").value(200))
    //                 .andExpect(jsonPath("$.message").value("Booking details updated successfully"));
    //     }
    // }

    // @Test
    // void testUpdateBookingDetailsWithInvalidId() throws Exception {
    //     LocalDateTime newPickUp = LocalDateTime.now().plusDays(10);
    //     String requestBody = String.format("""
    //         {
    //             "id": "NONEXISTENT-ID",
    //             "pickUpTime": "%s",
    //             "dropOffTime": "%s",
    //             "pickUpLocation": "New Location",
    //             "dropOffLocation": "New Drop"
    //         }
    //         """, 
    //         newPickUp.toString(),
    //         newPickUp.plusDays(3).toString());

    //     mockMvc.perform(put("/api/bookings/update-details")
    //             .contentType(MediaType.APPLICATION_JSON)
    //             .content(requestBody))
    //             .andExpect(status().isBadRequest())
    //             .andExpect(jsonPath("$.status").value(400));
    // }

    // Test PUT /bookings/update-status
    // @Test
    // void testUpdateBookingStatusUpcomingToOngoing() throws Exception {
    //     if (upcomingBooking != null) {
    //         String requestBody = String.format("""
    //             {
    //                 "bookingId": "%s",
    //                 "newStatus": "Ongoing"
    //             }
    //             """, upcomingBooking.getId());

    //         mockMvc.perform(put("/api/bookings/update-status")
    //                 .contentType(MediaType.APPLICATION_JSON)
    //                 .content(requestBody))
    //                 .andExpect(status().isOk())
    //                 .andExpect(jsonPath("$.status").value(200))
    //                 .andExpect(jsonPath("$.message").value("Booking status updated successfully"));
    //     }
    // }

    @Test
    void testUpdateBookingStatusOngoingToDone() throws Exception {
        if (ongoingBooking != null) {
            String requestBody = String.format("""
                {
                    "bookingId": "%s",
                    "newStatus": "Done"
                }
                """, ongoingBooking.getId());

            mockMvc.perform(put("/api/bookings/update-status")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value(200));
        }
    }

    @Test
    void testUpdateBookingStatusWithInvalidId() throws Exception {
        String requestBody = """
            {
                "bookingId": "INVALID-BOOKING-ID",
                "newStatus": "Ongoing"
            }
            """;

        mockMvc.perform(put("/api/bookings/update-status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    // Test PUT /bookings/update-addons
    @Test
    void testUpdateBookingAddOnsWithEmptyList() throws Exception {
        if (upcomingBooking != null) {
            String requestBody = String.format("""
                {
                    "bookingId": "%s",
                    "selectedAddOnIds": []
                }
                """, upcomingBooking.getId());

            mockMvc.perform(put("/api/bookings/update-addons")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value(200))
                    .andExpect(jsonPath("$.message").value("Add-ons updated successfully"));
        }
    }

    // @Test
    // void testUpdateBookingAddOnsWithValidAddOns() throws Exception {
    //     if (upcomingBooking != null) {
    //         var addOns = addOnRepository.findAll();
    //         if (!addOns.isEmpty()) {
    //             String requestBody = String.format("""
    //                 {
    //                     "bookingId": "%s",
    //                     "selectedAddOnIds": [%d]
    //                 }
    //                 """, upcomingBooking.getId(), addOns.get(0).getId());

    //             mockMvc.perform(put("/api/bookings/update-addons")
    //                     .contentType(MediaType.APPLICATION_JSON)
    //                     .content(requestBody))
    //                     .andExpect(status().isOk())
    //                     .andExpect(jsonPath("$.status").value(200));
    //         }
    //     }
    // }

    @Test
    void testUpdateBookingAddOnsWithInvalidId() throws Exception {
        String requestBody = """
            {
                "bookingId": "NONEXISTENT-ID",
                "selectedAddOnIds": []
            }
            """;

        mockMvc.perform(put("/api/bookings/update-addons")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    // Test GET endpoints with error scenarios
    @Test
    void testGetBookingForUpdateDetailsWithInvalidId() throws Exception {
        mockMvc.perform(get("/api/bookings/INVALID-ID/update-details"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void testGetBookingForUpdateStatusWithInvalidId() throws Exception {
        mockMvc.perform(get("/api/bookings/INVALID-ID/update-status"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void testGetBookingForUpdateAddOnsWithInvalidId() throws Exception {
        mockMvc.perform(get("/api/bookings/INVALID-ID/update-addons"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void testGetAvailableStatusTransitionsWithInvalidId() throws Exception {
        mockMvc.perform(get("/api/bookings/INVALID-ID/available-status-transitions"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    // Test DELETE /bookings/{id}/delete
    @Test
    void testCancelBookingSuccess() throws Exception {
        if (upcomingBooking != null) {
            mockMvc.perform(delete("/api/bookings/" + upcomingBooking.getId() + "/delete"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value(200))
                    .andExpect(jsonPath("$.message").value("Booking dibatalkan dan dihapus dari daftar pesanan"));
        }
    }

    @Test
    void testCancelBookingWithInvalidId() throws Exception {
        mockMvc.perform(delete("/api/bookings/INVALID-ID-999/delete"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    // Test GET /bookings/current-time
    @Test
    void testGetCurrentTime() throws Exception {
        mockMvc.perform(get("/api/bookings/current-time"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.timestamp").exists())
                .andExpect(jsonPath("$.data.serverTime").exists());
    }

    @Test
    void testGetCurrentTimeReturnsValidData() throws Exception {
        mockMvc.perform(get("/api/bookings/current-time"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data.timestamp").isNumber())
                .andExpect(jsonPath("$.data.serverTime").isString());
    }

    // Additional validation tests
    // @Test
    // void testUpdateBookingDetailsResponseStructure() throws Exception {
    //     if (upcomingBooking != null) {
    //         LocalDateTime newPickUp = LocalDateTime.now().plusDays(12);
    //         String requestBody = String.format("""
    //             {
    //                 "id": "%s",
    //                 "pickUpTime": "%s",
    //                 "dropOffTime": "%s",
    //                 "pickUpLocation": "Jakarta",
    //                 "dropOffLocation": "Bandung"
    //             }
    //             """, 
    //             upcomingBooking.getId(),
    //             newPickUp.toString(),
    //             newPickUp.plusDays(2).toString());

    //         mockMvc.perform(put("/api/bookings/update-details")
    //                 .contentType(MediaType.APPLICATION_JSON)
    //                 .content(requestBody))
    //                 .andExpect(status().isOk())
    //                 .andExpect(jsonPath("$.status").exists())
    //                 .andExpect(jsonPath("$.message").exists())
    //                 .andExpect(jsonPath("$.timestamp").exists())
    //                 .andExpect(jsonPath("$.data").exists());
    //     }
    // }

    // @Test
    // void testUpdateBookingStatusResponseStructure() throws Exception {
    //     if (upcomingBooking != null) {
    //         String requestBody = String.format("""
    //             {
    //                 "bookingId": "%s",
    //                 "newStatus": "Ongoing"
    //             }
    //             """, upcomingBooking.getId());

    //         mockMvc.perform(put("/api/bookings/update-status")
    //                 .contentType(MediaType.APPLICATION_JSON)
    //                 .content(requestBody))
    //                 .andExpect(status().isOk())
    //                 .andExpect(jsonPath("$.status").exists())
    //                 .andExpect(jsonPath("$.message").exists())
    //                 .andExpect(jsonPath("$.timestamp").exists())
    //                 .andExpect(jsonPath("$.data").exists());
    //     }
    // }

    @Test
    void testUpdateBookingAddOnsResponseStructure() throws Exception {
        if (upcomingBooking != null) {
            String requestBody = String.format("""
                {
                    "bookingId": "%s",
                    "selectedAddOnIds": []
                }
                """, upcomingBooking.getId());

            mockMvc.perform(put("/api/bookings/update-addons")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").exists())
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.timestamp").exists())
                    .andExpect(jsonPath("$.data").exists());
        }
    }

    @Test
    void testCancelBookingResponseStructure() throws Exception {
        RentalBooking tempBooking = createBookingWithStatus("Upcoming");
        mockMvc.perform(delete("/api/bookings/" + tempBooking.getId() + "/delete"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").exists())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.data").exists());
    }
}
