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
    void testGetBookingChartData() throws Exception {
        mockMvc.perform(get("/api/bookings/chart")
                .param("period", "Monthly")
                .param("year", "2025"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200));
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
    void testDeleteBooking() throws Exception {
        mockMvc.perform(delete("/api/bookings/" + testBooking.getId()))
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

    @Test
    void testGetBookingByIdDetails() throws Exception {
        mockMvc.perform(get("/api/bookings/" + testBooking.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.id").value(testBooking.getId()));
    }

    @Test
    void testGetCurrentTime() throws Exception {
        mockMvc.perform(get("/api/bookings/current-time"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200));
    }

    @Test
    void testFinalizeBookingWithMissingData() throws Exception {
        FinalizBookingRequestDTO finalizeDTO = new FinalizBookingRequestDTO();
        // Missing bookingDTO and addOnsDTO to trigger validation error

        mockMvc.perform(post("/api/bookings/finalize")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(finalizeDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetAllBookingsReturnsArray() throws Exception {
        mockMvc.perform(get("/api/bookings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    void testSearchVehiclesWithDriver() throws Exception {
        CreateBookingRequestDTO request = new CreateBookingRequestDTO();
        request.setPickUpLocation("Jakarta");
        request.setDropOffLocation("Bandung");
        request.setPickUpTime(LocalDateTime.now().plusDays(10));
        request.setDropOffTime(LocalDateTime.now().plusDays(12));
        request.setCapacityNeeded(5);
        request.setTransmissionNeeded("Automatic");
        request.setIncludeDriver(true);

        mockMvc.perform(post("/api/bookings/search")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200));
    }

    @Test
    void testSearchVehiclesManualTransmission() throws Exception {
        CreateBookingRequestDTO request = new CreateBookingRequestDTO();
        request.setPickUpLocation("Jakarta");
        request.setDropOffLocation("Bandung");
        request.setPickUpTime(LocalDateTime.now().plusDays(10));
        request.setDropOffTime(LocalDateTime.now().plusDays(12));
        request.setCapacityNeeded(5);
        request.setTransmissionNeeded("Manual");
        request.setIncludeDriver(false);

        mockMvc.perform(post("/api/bookings/search")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void testSearchVehiclesHighCapacity() throws Exception {
        CreateBookingRequestDTO request = new CreateBookingRequestDTO();
        request.setPickUpLocation("Jakarta");
        request.setDropOffLocation("Bandung");
        request.setPickUpTime(LocalDateTime.now().plusDays(10));
        request.setDropOffTime(LocalDateTime.now().plusDays(12));
        request.setCapacityNeeded(7);
        request.setTransmissionNeeded("Automatic");
        request.setIncludeDriver(false);

        mockMvc.perform(post("/api/bookings/search")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void testGetBookingCountReturnsNumber() throws Exception {
        mockMvc.perform(get("/api/bookings/count"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isNumber());
    }

    @Test
    void testGetAllAddOnsReturnsArray() throws Exception {
        mockMvc.perform(get("/api/bookings/addons"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    void testGetProvincesReturnsArray() throws Exception {
        mockMvc.perform(get("/api/bookings/provinces"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    void testGetChartDataMonthly() throws Exception {
        mockMvc.perform(get("/api/bookings/chart")
                .param("period", "Monthly")
                .param("year", "2024"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200));
    }

    // @Test
    // void testGetChartDataYearly() throws Exception {
    //     mockMvc.perform(get("/api/bookings/chart")
    //             .param("period", "Yearly")
    //             .param("year", "2024"))
    //             .andExpect(status().isOk());
    // }

    @Test
    void testGetBookingByIdVerifyFields() throws Exception {
        mockMvc.perform(get("/api/bookings/" + testBooking.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.pickUpLocation").value("Jakarta"))
                .andExpect(jsonPath("$.data.dropOffLocation").value("Bandung"))
                .andExpect(jsonPath("$.data.status").value("Pending"));
    }

    // @Test
    // void testGetBookingByIdVerifyVehicleData() throws Exception {
    //     mockMvc.perform(get("/api/bookings/" + testBooking.getId()))
    //             .andExpect(status().isOk())
    //             .andExpect(jsonPath("$.data.vehicle").exists())
    //             .andExpect(jsonPath("$.data.vehicle.id").value(testVehicle.getId()));
    // }

    @Test
    void testDeleteBookingSuccessMessage() throws Exception {
        RentalBooking tempBooking = new RentalBooking();
        tempBooking.setId("BKG-TEMP-DELETE");
        tempBooking.setVehicle(testVehicle);
        tempBooking.setPickUpLocation("Jakarta");
        tempBooking.setDropOffLocation("Bandung");
        tempBooking.setPickUpTime(LocalDateTime.now().plusDays(1));
        tempBooking.setDropOffTime(LocalDateTime.now().plusDays(3));
        tempBooking.setTotalPrice(2400000.0);
        tempBooking.setStatus("Pending");
        tempBooking.setIncludeDriver(false);
        tempBooking.setCapacityNeeded(7);
        tempBooking.setTransmissionNeeded("Automatic");
        bookingRepository.save(tempBooking);

        mockMvc.perform(delete("/api/bookings/" + tempBooking.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200));
    }

    @Test
    void testFinalizeBookingWithAddOns() throws Exception {
        CreateBookingRequestDTO bookingDTO = new CreateBookingRequestDTO();
        bookingDTO.setPickUpLocation("Jakarta");
        bookingDTO.setDropOffLocation("Bandung");
        bookingDTO.setPickUpTime(LocalDateTime.now().plusDays(10));
        bookingDTO.setDropOffTime(LocalDateTime.now().plusDays(12));
        bookingDTO.setIncludeDriver(true);
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
                .andExpect(status().isCreated());
    }

    @Test
    void testGetCurrentTimeReturnsValidFormat() throws Exception {
        mockMvc.perform(get("/api/bookings/current-time"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").exists());
    }

    @Test
    void testSearchVehiclesSameLocation() throws Exception {
        CreateBookingRequestDTO request = new CreateBookingRequestDTO();
        request.setPickUpLocation("Jakarta");
        request.setDropOffLocation("Jakarta");
        request.setPickUpTime(LocalDateTime.now().plusDays(10));
        request.setDropOffTime(LocalDateTime.now().plusDays(12));
        request.setCapacityNeeded(5);
        request.setTransmissionNeeded("Automatic");
        request.setIncludeDriver(false);

        mockMvc.perform(post("/api/bookings/search")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void testGetBookingByIdResponseStructure() throws Exception {
        mockMvc.perform(get("/api/bookings/" + testBooking.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").exists())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.data").exists());
    }

    @Test
    void testGetAllBookingsResponseStructure() throws Exception {
        mockMvc.perform(get("/api/bookings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.data").isArray());
    }

    // @Test
    // void testSearchVehiclesResponseStructure() throws Exception {
    //     CreateBookingRequestDTO request = new CreateBookingRequestDTO();
    //     request.setPickUpLocation("Jakarta");
    //     request.setDropOffLocation("Bandung");
    //     request.setPickUpTime(LocalDateTime.now().plusDays(10));
    //     request.setDropOffTime(LocalDateTime.now().plusDays(12));
    //     request.setCapacityNeeded(5);
    //     request.setTransmissionNeeded("Automatic");
    //     request.setIncludeDriver(false);

    //     mockMvc.perform(post("/api/bookings/search")
    //             .contentType(MediaType.APPLICATION_JSON)
    //             .content(objectMapper.writeValueAsString(request)))
    //             .andExpect(status().isOk())
    //             .andExpect(jsonPath("$.status").value(200))
    //             .andExpect(jsonPath("$.message").exists())
    //             .andExpect(jsonPath("$.data").isArray());
    // }

    @Test
    void testGetBookingCountResponseStructure() throws Exception {
        mockMvc.perform(get("/api/bookings/count"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data").exists());
    }

    @Test
    void testGetChartDataResponseStructure() throws Exception {
        mockMvc.perform(get("/api/bookings/chart")
                .param("period", "Monthly")
                .param("year", "2025"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data").exists());
    }

    @Test
    void testFinalizeBookingResponseStructure() throws Exception {
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
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value(201));
    }
}
