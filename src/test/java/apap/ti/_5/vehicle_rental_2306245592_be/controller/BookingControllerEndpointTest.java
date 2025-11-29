// package apap.ti._5.vehicle_rental_2306245592_be.controller;

// import apap.ti._5.vehicle_rental_2306245592_be.model.RentalBooking;
// import apap.ti._5.vehicle_rental_2306245592_be.model.Vehicle;
// import apap.ti._5.vehicle_rental_2306245592_be.model.RentalAddOn;
// import apap.ti._5.vehicle_rental_2306245592_be.repository.RentalBookingRepository;
// import apap.ti._5.vehicle_rental_2306245592_be.repository.VehicleRepository;
// import apap.ti._5.vehicle_rental_2306245592_be.repository.RentalAddOnRepository;
// import com.fasterxml.jackson.databind.ObjectMapper;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.http.MediaType;
// import org.springframework.test.web.servlet.MockMvc;
// import org.springframework.transaction.annotation.Transactional;

// import java.time.LocalDateTime;
// import java.util.List;
// import java.util.UUID;

// import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
// import static org.hamcrest.Matchers.*;

// @SpringBootTest
// @AutoConfigureMockMvc
// @Transactional
// class BookingControllerEndpointTest {

//     @Autowired
//     private MockMvc mockMvc;

//     @Autowired
//     private ObjectMapper objectMapper;

//     @Autowired
//     private RentalBookingRepository bookingRepository;

//     @Autowired
//     private VehicleRepository vehicleRepository;

//     @Autowired
//     private RentalAddOnRepository addOnRepository;

//     private Vehicle testVehicle;
//     private RentalBooking testBooking;

//     @BeforeEach
//     void setUp() {
//         testVehicle = vehicleRepository.findAll().stream().findFirst().orElse(null);
//         if (testVehicle != null) {
//             testBooking = createTestBooking();
//         }
//     }

//     private RentalBooking createTestBooking() {
//         RentalBooking booking = new RentalBooking();
//         booking.setId("TEST-" + UUID.randomUUID().toString().substring(0, 8));
//         booking.setVehicle(testVehicle);
//         booking.setPickUpTime(LocalDateTime.now().plusDays(2));
//         booking.setDropOffTime(LocalDateTime.now().plusDays(5));
//         booking.setPickUpLocation(testVehicle.getLocation());
//         booking.setDropOffLocation("Test Drop Location");
//         booking.setCapacityNeeded(5);
//         booking.setTransmissionNeeded("Manual");
//         booking.setTotalPrice(1050000.0);
//         booking.setIncludeDriver(false);
//         booking.setStatus("Upcoming");
//         booking.setDeletedAt(null);
//         return bookingRepository.save(booking);
//     }

//     @Test
//     void testGetAllBookings() throws Exception {
//         mockMvc.perform(get("/api/bookings"))
//                 .andExpect(status().isOk())
//                 .andExpect(jsonPath("$.status").value(200))
//                 .andExpect(jsonPath("$.message").exists())
//                 .andExpect(jsonPath("$.data").isArray());
//     }

//     @Test
//     void testGetAllBookingsWithStatusFilter() throws Exception {
//         mockMvc.perform(get("/api/bookings")
//                 .param("status", "Upcoming"))
//                 .andExpect(status().isOk())
//                 .andExpect(jsonPath("$.status").value(200))
//                 .andExpect(jsonPath("$.data").isArray());
//     }

//     @Test
//     void testGetBookingCount() throws Exception {
//         mockMvc.perform(get("/api/bookings/count"))
//                 .andExpect(status().isOk())
//                 .andExpect(jsonPath("$.status").value(200))
//                 .andExpect(jsonPath("$.data").isNumber());
//     }

//     @Test
//     void testGetBookingById() throws Exception {
//         mockMvc.perform(get("/api/bookings/{id}", testBooking.getId()))
//                 .andExpect(status().isOk())
//                 .andExpect(jsonPath("$.status").value(200))
//                 .andExpect(jsonPath("$.data.id").value(testBooking.getId()));
//     }

//     @Test
//     void testGetBookingByIdNotFound() throws Exception {
//         mockMvc.perform(get("/api/bookings/{id}", "NON-EXISTENT"))
//                 .andExpect(status().isNotFound());
//     }

//     @Test
//     void testGetAddOns() throws Exception {
//         mockMvc.perform(get("/api/bookings/addons"))
//                 .andExpect(status().isOk())
//                 .andExpect(jsonPath("$.status").value(200))
//                 .andExpect(jsonPath("$.data").isArray());
//     }

//     @Test
//     void testGetProvinces() throws Exception {
//         mockMvc.perform(get("/api/bookings/provinces"))
//                 .andExpect(status().isOk())
//                 .andExpect(jsonPath("$.status").value(200))
//                 .andExpect(jsonPath("$.data").isArray())
//                 .andExpect(jsonPath("$.data", hasSize(greaterThan(0))));
//     }

//     @Test
//     void testGetBookingForUpdateDetails() throws Exception {
//         mockMvc.perform(get("/api/bookings/{id}/update-details", testBooking.getId()))
//                 .andExpect(status().isOk())
//                 .andExpect(jsonPath("$.status").value(200));
//     }

//     // @Test
//     // void testGetBookingForUpdateDetailsNotFound() throws Exception {
//     //     mockMvc.perform(get("/api/bookings/{id}/update-details", "NON-EXISTENT"))
//     //             .andExpect(status().isNotFound());
//     // }

//     @Test
//     void testGetBookingForUpdateStatus() throws Exception {
//         mockMvc.perform(get("/api/bookings/{id}/update-status", testBooking.getId()))
//                 .andExpect(status().isOk())
//                 .andExpect(jsonPath("$.status").value(200));
//     }

//     @Test
//     void testGetAvailableStatusTransitions() throws Exception {
//         mockMvc.perform(get("/api/bookings/{id}/available-status-transitions", testBooking.getId()))
//                 .andExpect(status().isOk())
//                 .andExpect(jsonPath("$.status").value(200))
//                 .andExpect(jsonPath("$.data").isArray());
//     }

//     @Test
//     void testGetBookingForUpdateAddOns() throws Exception {
//         mockMvc.perform(get("/api/bookings/{id}/update-addons", testBooking.getId()))
//                 .andExpect(status().isOk())
//                 .andExpect(jsonPath("$.status").value(200));
//     }

//     @Test
//     void testDeleteBooking() throws Exception {
//         RentalBooking bookingToDelete = createTestBooking();
        
//         mockMvc.perform(delete("/api/bookings/{id}/delete", bookingToDelete.getId()))
//                 .andExpect(status().isOk())
//                 .andExpect(jsonPath("$.status").value(200));
//     }

//     // @Test
//     // void testDeleteBookingNotFound() throws Exception {
//     //     mockMvc.perform(delete("/api/bookings/{id}/delete", "NON-EXISTENT"))
//     //             .andExpect(status().isNotFound());
//     // }

//     @Test
//     void testGetChartData() throws Exception {
//         mockMvc.perform(get("/api/bookings/chart")
//                 .param("period", "monthly")
//                 .param("year", String.valueOf(LocalDateTime.now().getYear())))
//                 .andExpect(status().isOk())
//                 .andExpect(jsonPath("$.status").value(200))
//                 .andExpect(jsonPath("$.data").exists());
//     }

//     @Test
//     void testGetChartDataQuarterly() throws Exception {
//         mockMvc.perform(get("/api/bookings/chart")
//                 .param("period", "quarterly")
//                 .param("year", String.valueOf(LocalDateTime.now().getYear())))
//                 .andExpect(status().isOk())
//                 .andExpect(jsonPath("$.status").value(200));
//     }

//     @Test
//     void testGetChartDataDefaultPeriod() throws Exception {
//         mockMvc.perform(get("/api/bookings/chart"))
//                 .andExpect(status().isOk())
//                 .andExpect(jsonPath("$.status").value(200));
//     }

//     // @Test
//     // void testGetCurrentTime() throws Exception {
//     //     mockMvc.perform(get("/api/bookings/current-time"))
//     //             .andExpect(status().isOk())
//     //             .andExpect(jsonPath("$.status").value(200))
//     //             .andExpect(jsonPath("$.data.currentTime").exists());
//     // }

//     @Test
//     void testSearchVehicles() throws Exception {
//         String requestBody = """
//             {
//                 "pickUpLocation": "Jakarta Pusat",
//                 "dropOffLocation": "Jakarta Selatan",
//                 "pickUpTime": "2025-11-10T10:00",
//                 "dropOffTime": "2025-11-13T10:00",
//                 "capacityNeeded": 5,
//                 "transmissionNeeded": "Manual",
//                 "includeDriver": false
//             }
//             """;

//         mockMvc.perform(post("/api/bookings/search")
//                 .contentType(MediaType.APPLICATION_JSON)
//                 .content(requestBody))
//                 .andExpect(status().isOk())
//                 .andExpect(jsonPath("$.status").value(200))
//                 .andExpect(jsonPath("$.data").exists());
//     }

//     @Test
//     void testSearchVehiclesWithDriver() throws Exception {
//         String requestBody = """
//             {
//                 "pickUpLocation": "Jakarta Pusat",
//                 "dropOffLocation": "Jakarta Selatan",
//                 "pickUpTime": "2025-11-10T10:00",
//                 "dropOffTime": "2025-11-13T10:00",
//                 "capacityNeeded": 5,
//                 "transmissionNeeded": "Manual",
//                 "includeDriver": true
//             }
//             """;

//         mockMvc.perform(post("/api/bookings/search")
//                 .contentType(MediaType.APPLICATION_JSON)
//                 .content(requestBody))
//                 .andExpect(status().isOk())
//                 .andExpect(jsonPath("$.status").value(200));
//     }

//     @Test
//     void testSearchVehiclesAutomaticTransmission() throws Exception {
//         String requestBody = """
//             {
//                 "pickUpLocation": "Jakarta Pusat",
//                 "dropOffLocation": "Jakarta Selatan",
//                 "pickUpTime": "2025-11-10T10:00",
//                 "dropOffTime": "2025-11-13T10:00",
//                 "capacityNeeded": 7,
//                 "transmissionNeeded": "Automatic",
//                 "includeDriver": false
//             }
//             """;

//         mockMvc.perform(post("/api/bookings/search")
//                 .contentType(MediaType.APPLICATION_JSON)
//                 .content(requestBody))
//                 .andExpect(status().isOk())
//                 .andExpect(jsonPath("$.status").value(200));
//     }

//     @Test
//     void testSearchVehiclesHighCapacity() throws Exception {
//         String requestBody = """
//             {
//                 "pickUpLocation": "Jakarta Pusat",
//                 "dropOffLocation": "Jakarta Selatan",
//                 "pickUpTime": "2025-11-10T10:00",
//                 "dropOffTime": "2025-11-13T10:00",
//                 "capacityNeeded": 8,
//                 "transmissionNeeded": "Manual",
//                 "includeDriver": true
//             }
//             """;

//         mockMvc.perform(post("/api/bookings/search")
//                 .contentType(MediaType.APPLICATION_JSON)
//                 .content(requestBody))
//                 .andExpect(status().isOk())
//                 .andExpect(jsonPath("$.status").value(200));
//     }

//     // @Test
//     // void testSearchVehiclesSameDayRental() throws Exception {
//     //     LocalDateTime tomorrow = LocalDateTime.now().plusDays(1);
//     //     String requestBody = String.format("""
//     //         {
//     //             "pickUpLocation": "Jakarta Pusat",
//     //             "dropOffLocation": "Jakarta Pusat",
//     //             "pickUpTime": "%s",
//     //             "dropOffTime": "%s",
//     //             "capacityNeeded": 5,
//     //             "transmissionNeeded": "Manual",
//     //             "includeDriver": false
//     //         }
//     //         """, 
//     //         tomorrow.withHour(8).toString(),
//     //         tomorrow.withHour(20).toString());

//     //     mockMvc.perform(post("/api/bookings/search")
//     //             .contentType(MediaType.APPLICATION_JSON)
//     //             .content(requestBody))
//     //             .andExpect(status().isOk())
//     //             .andExpect(jsonPath("$.status").value(200));
//     // }

//     @Test
//     void testGetAllBookingsEmptyStatus() throws Exception {
//         mockMvc.perform(get("/api/bookings")
//                 .param("status", ""))
//                 .andExpect(status().isOk())
//                 .andExpect(jsonPath("$.status").value(200));
//     }

//     @Test
//     void testGetAllBookingsMultipleCalls() throws Exception {
//         mockMvc.perform(get("/api/bookings"))
//                 .andExpect(status().isOk());
        
//         mockMvc.perform(get("/api/bookings"))
//                 .andExpect(status().isOk());
//     }

//     @Test
//     void testGetChartDataPreviousYear() throws Exception {
//         int previousYear = LocalDateTime.now().getYear() - 1;
        
//         mockMvc.perform(get("/api/bookings/chart")
//                 .param("period", "monthly")
//                 .param("year", String.valueOf(previousYear)))
//                 .andExpect(status().isOk())
//                 .andExpect(jsonPath("$.status").value(200));
//     }

//     @Test
//     void testGetBookingCountReturnsValidNumber() throws Exception {
//         mockMvc.perform(get("/api/bookings/count"))
//                 .andExpect(status().isOk())
//                 .andExpect(jsonPath("$.data").isNumber())
//                 .andExpect(jsonPath("$.data", greaterThanOrEqualTo(0)));
//     }

//     @Test
//     void testGetAddOnsReturnsNonEmptyArray() throws Exception {
//         mockMvc.perform(get("/api/bookings/addons"))
//                 .andExpect(status().isOk())
//                 .andExpect(jsonPath("$.data").isArray());
//     }

//     // @Test
//     // void testCurrentTimeReturnsValidTimestamp() throws Exception {
//     //     mockMvc.perform(get("/api/bookings/current-time"))
//     //             .andExpect(status().isOk())
//     //             .andExpect(jsonPath("$.data.currentTime").isString())
//     //             .andExpect(jsonPath("$.data.currentTime", not(emptyString())));
//     // }
// }
