package apap.ti._5.vehicle_rental_2306245592_be.controller;

import apap.ti._5.vehicle_rental_2306245592_be.model.RentalBooking;
import apap.ti._5.vehicle_rental_2306245592_be.model.RentalVendor;
import apap.ti._5.vehicle_rental_2306245592_be.model.Vehicle;
import apap.ti._5.vehicle_rental_2306245592_be.repository.RentalBookingRepository;
import apap.ti._5.vehicle_rental_2306245592_be.repository.RentalVendorRepository;
import apap.ti._5.vehicle_rental_2306245592_be.repository.VehicleRepository;
import apap.ti._5.vehicle_rental_2306245592_be.restdto.request.booking.CreateBookingRequestDTO;
import apap.ti._5.vehicle_rental_2306245592_be.restdto.request.booking.UpdateBookingStatusDTO;
import apap.ti._5.vehicle_rental_2306245592_be.restdto.request.booking.UpdateBookingRequestDTO;
import apap.ti._5.vehicle_rental_2306245592_be.restdto.request.booking.AddAddOnsRequestDTO;
import apap.ti._5.vehicle_rental_2306245592_be.restdto.request.booking.UpdateAddOnsRequestDTO;
import apap.ti._5.vehicle_rental_2306245592_be.restdto.request.booking.FinalizBookingRequestDTO;

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
import java.util.Arrays;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RentalBookingRepository bookingRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private RentalVendorRepository vendorRepository;

    private RentalBooking testBooking;
    private Vehicle testVehicle;

    @BeforeEach
    void setUp() {
        RentalVendor vendor = new RentalVendor();
        vendor.setName("Test Vendor");
        vendor.setEmail("test@vendor.com");
        vendor.setPhone("08123456789");
        vendor.setListOfLocations(Arrays.asList("Jakarta", "Bandung"));
        vendor = vendorRepository.save(vendor);

        testVehicle = new Vehicle();
        testVehicle.setId("VH-TEST-001");
        testVehicle.setType("SUV");
        testVehicle.setBrand("Toyota");
        testVehicle.setModel("Fortuner");
        testVehicle.setYear(2023);
        testVehicle.setCapacity(7);
        testVehicle.setTransmission("Automatic");
        testVehicle.setFuelType("Diesel");
        testVehicle.setLocation("Jakarta");
        testVehicle.setLicensePlate("B1234TEST");
        testVehicle.setPrice(800000.0);
        testVehicle.setStatus("Available");
        testVehicle.setRentalVendor(vendor);
        testVehicle = vehicleRepository.save(testVehicle);

        testBooking = new RentalBooking();
        testBooking.setId("BKG-TEST-001");
        testBooking.setVehicle(testVehicle);
        testBooking.setPickUpLocation("Jakarta");
        testBooking.setDropOffLocation("Bandung");
        testBooking.setPickUpTime(LocalDateTime.now().plusDays(1));
        testBooking.setDropOffTime(LocalDateTime.now().plusDays(3));
        testBooking.setTotalPrice(2400000.0);
        testBooking.setStatus("Pending");
        testBooking.setIncludeDriver(false);
        testBooking.setCapacityNeeded(7);
        testBooking.setTransmissionNeeded("Automatic");
        testBooking = bookingRepository.save(testBooking);
    }

    @Test
    void testGetAllBookings() throws Exception {
        mockMvc.perform(get("/api/bookings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200));
    }

    @Test
    void testGetBookingById() throws Exception {
        mockMvc.perform(get("/api/bookings/" + testBooking.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.id").value(testBooking.getId()));
    }

    @Test
    void testSearchVehicles() throws Exception {
        CreateBookingRequestDTO request = new CreateBookingRequestDTO();
        request.setPickUpLocation("Jakarta");
        request.setDropOffLocation("Bandung");
        request.setPickUpTime(LocalDateTime.now().plusDays(10));
        request.setDropOffTime(LocalDateTime.now().plusDays(12));
        request.setCapacityNeeded(5);
        request.setTransmissionNeeded("Automatic");
        request.setIncludeDriver(false);

        mockMvc.perform(post("/api/bookings/search")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200));
    }

    @Test
    void testGetBookingCount() throws Exception {
        mockMvc.perform(get("/api/bookings/count"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200));
    }

    @Test
    void testGetAllAddOns() throws Exception {
        mockMvc.perform(get("/api/bookings/addons"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200));
    }

    @Test
    void testGetProvinces() throws Exception {
        mockMvc.perform(get("/api/bookings/provinces"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200));
    }

    @Test
    void testUpdateBookingStatus() throws Exception {
        UpdateBookingStatusDTO statusDTO = new UpdateBookingStatusDTO();
        statusDTO.setBookingId(testBooking.getId());
        statusDTO.setNewStatus("Confirmed");

        mockMvc.perform(put("/api/bookings/update-status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(statusDTO)))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    void testGetBookingChartData() throws Exception {
        mockMvc.perform(get("/api/bookings/chart")
                .param("period", "Monthly")
                .param("year", "2025"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200));
    }

    @Test
    void testCancelBooking() throws Exception {
        mockMvc.perform(delete("/api/bookings/" + testBooking.getId() + "/delete"))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    void testGetAllBookingsWithStatusFilter() throws Exception {
        mockMvc.perform(get("/api/bookings")
                .param("status", "Pending"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetBookingByIdNotFound() throws Exception {
        mockMvc.perform(get("/api/bookings/INVALID-ID"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void testCreateBooking() throws Exception {
        CreateBookingRequestDTO request = new CreateBookingRequestDTO();
        request.setPickUpLocation("Jakarta");
        request.setDropOffLocation("Surabaya");
        request.setPickUpTime(LocalDateTime.now().plusDays(5));
        request.setDropOffTime(LocalDateTime.now().plusDays(7));
        request.setCapacityNeeded(5);
        request.setTransmissionNeeded("Manual");
        request.setIncludeDriver(true);

        mockMvc.perform(post("/api/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    void testUpdateBooking() throws Exception {
        mockMvc.perform(put("/api/bookings/" + testBooking.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testBooking)))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    void testDeleteBooking() throws Exception {
        mockMvc.perform(delete("/api/bookings/" + testBooking.getId()))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    void testGetBookingForUpdate() throws Exception {
        mockMvc.perform(get("/api/bookings/" + testBooking.getId() + "/update"))
                .andExpect(status().isOk());
    }

    @Test
    void testUpdateBookingDetails() throws Exception {
        UpdateBookingRequestDTO updateDTO = new UpdateBookingRequestDTO();
        updateDTO.setId(testBooking.getId());
        updateDTO.setPickUpLocation("Bandung");
        updateDTO.setDropOffLocation("Yogyakarta");
        updateDTO.setPickUpTime(LocalDateTime.now().plusDays(2));
        updateDTO.setDropOffTime(LocalDateTime.now().plusDays(4));
        updateDTO.setCapacityNeeded(5);
        updateDTO.setTransmissionNeeded("Automatic");
        updateDTO.setIncludeDriver(false);

        mockMvc.perform(put("/api/bookings/update-details")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    void testGetBookingForUpdateStatus() throws Exception {
        mockMvc.perform(get("/api/bookings/" + testBooking.getId() + "/update-status"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetAvailableStatusTransitions() throws Exception {
        mockMvc.perform(get("/api/bookings/" + testBooking.getId() + "/status-transitions"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetBookingForUpdateAddOns() throws Exception {
        mockMvc.perform(get("/api/bookings/" + testBooking.getId() + "/update-addons"))
                .andExpect(status().isOk());
    }

    @Test
    void testUpdateBookingAddOns() throws Exception {
        UpdateAddOnsRequestDTO updateDTO = new UpdateAddOnsRequestDTO();
        updateDTO.setBookingId(testBooking.getId());
        updateDTO.setSelectedAddOnIds(Arrays.asList("1", "2"));

        mockMvc.perform(put("/api/bookings/update-addons")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    void testFinalizeBooking() throws Exception {
        CreateBookingRequestDTO bookingDTO = new CreateBookingRequestDTO();
        bookingDTO.setPickUpLocation("Jakarta");
        bookingDTO.setDropOffLocation("Bandung");
        bookingDTO.setPickUpTime(LocalDateTime.now().plusDays(10));
        bookingDTO.setDropOffTime(LocalDateTime.now().plusDays(12));
        bookingDTO.setIncludeDriver(false);
        bookingDTO.setCapacityNeeded(4);
        bookingDTO.setTransmissionNeeded("Automatic");
        
        AddAddOnsRequestDTO addOnsDTO = new AddAddOnsRequestDTO();
        addOnsDTO.setVehicleId(testVehicle.getId());
        addOnsDTO.setSelectedAddOnIds(new java.util.ArrayList<>());
        
        FinalizBookingRequestDTO finalizeDTO = new FinalizBookingRequestDTO();
        finalizeDTO.setBookingDTO(bookingDTO);
        finalizeDTO.setAddOnsDTO(addOnsDTO);

        mockMvc.perform(post("/api/bookings/finalize")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(finalizeDTO)))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    void testGetChartDataQuarterly() throws Exception {
        mockMvc.perform(get("/api/bookings/chart")
                .param("period", "Quarterly")
                .param("year", "2025"))
                .andExpect(status().isOk());
    }

    @Test
    void testConvertToDTOCoverage() throws Exception {
        // This will trigger the convertToDTO method
        mockMvc.perform(get("/api/bookings/" + testBooking.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").exists());
    }
}
