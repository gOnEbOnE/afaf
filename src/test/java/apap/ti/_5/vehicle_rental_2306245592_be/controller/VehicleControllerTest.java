package apap.ti._5.vehicle_rental_2306245592_be.controller;

import apap.ti._5.vehicle_rental_2306245592_be.model.RentalVendor;
import apap.ti._5.vehicle_rental_2306245592_be.model.Vehicle;
import apap.ti._5.vehicle_rental_2306245592_be.repository.RentalVendorRepository;
import apap.ti._5.vehicle_rental_2306245592_be.repository.VehicleRepository;
import apap.ti._5.vehicle_rental_2306245592_be.restdto.request.vehicle.CreateVehicleRequestDTO;
import apap.ti._5.vehicle_rental_2306245592_be.restdto.request.vehicle.UpdateVehicleRequestDTO;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class VehicleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private RentalVendorRepository vendorRepository;

    private Vehicle testVehicle;
    private RentalVendor testVendor;

    @BeforeEach
    void setUp() {
        testVendor = new RentalVendor();
        testVendor.setName("Test Vendor");
        testVendor.setEmail("test@vendor.com");
        testVendor.setPhone("08123456789");
        testVendor.setListOfLocations(Arrays.asList("Jakarta", "Bandung"));
        testVendor = vendorRepository.save(testVendor);

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
        testVehicle.setRentalVendor(testVendor);
        testVehicle = vehicleRepository.save(testVehicle);
    }

    @Test
    void testGetAllVehicles() throws Exception {
        mockMvc.perform(get("/api/vehicles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200));
    }

    @Test
    void testGetAllVehiclesWithTypeFilter() throws Exception {
        mockMvc.perform(get("/api/vehicles")
                .param("type", "SUV"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200));
    }

    @Test
    void testGetAllVehiclesWithKeywordFilter() throws Exception {
        mockMvc.perform(get("/api/vehicles")
                .param("keyword", "Toyota"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200));
    }

    @Test
    void testGetVehicleById() throws Exception {
        mockMvc.perform(get("/api/vehicles/" + testVehicle.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.id").value(testVehicle.getId()));
    }

    @Test
    void testCreateVehicle() throws Exception {
        CreateVehicleRequestDTO request = new CreateVehicleRequestDTO();
        request.setRentalVendorId(testVendor.getId());
        request.setType("Sedan");
        request.setBrand("Honda");
        request.setModel("Civic");
        request.setYear(2022);
        request.setCapacity(5);
        request.setTransmission("Manual");
        request.setFuelType("Gasoline");
        request.setLocation("Bandung");
        request.setLicensePlate("B5678TEST");
        request.setPrice(600000.0);

        mockMvc.perform(post("/api/vehicles/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value(201));
    }

    @Test
    void testGetVehicleForUpdate() throws Exception {
        mockMvc.perform(get("/api/vehicles/" + testVehicle.getId() + "/update"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200));
    }

    @Test
    void testUpdateVehicle() throws Exception {
        UpdateVehicleRequestDTO request = new UpdateVehicleRequestDTO();
        request.setId(testVehicle.getId());
        request.setRentalVendorId(testVendor.getId());
        request.setType("SUV");
        request.setBrand("Toyota");
        request.setModel("Fortuner");
        request.setYear(2024);
        request.setCapacity(7);
        request.setTransmission("Automatic");
        request.setFuelType("Diesel");
        request.setLocation("Jakarta");
        request.setLicensePlate("B1234TEST");
        request.setPrice(850000.0);
        request.setStatus("Available");

        mockMvc.perform(put("/api/vehicles/" + testVehicle.getId() + "/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200));
    }

    @Test
    void testDeleteVehicle() throws Exception {
        mockMvc.perform(delete("/api/vehicles/" + testVehicle.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200));
    }

    @Test
    void testGetVehicleCount() throws Exception {
        mockMvc.perform(get("/api/vehicles/count"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200));
    }

    @Test
    void testGetAllVendors() throws Exception {
        mockMvc.perform(get("/api/vehicles/vendors"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200));
    }

    @Test
    void testGetVehicleByIdNotFound() throws Exception {
        mockMvc.perform(get("/api/vehicles/INVALID-ID"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void testGetVehicleForUpdateNotFound() throws Exception {
        mockMvc.perform(get("/api/vehicles/INVALID-ID/update"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void testUpdateVehicleNotFound() throws Exception {
        UpdateVehicleRequestDTO request = new UpdateVehicleRequestDTO();
        request.setId("INVALID-ID");
        request.setRentalVendorId(testVendor.getId());
        request.setType("SUV");
        request.setBrand("Toyota");
        request.setModel("Fortuner");
        request.setYear(2024);
        request.setCapacity(7);
        request.setTransmission("Automatic");
        request.setFuelType("Diesel");
        request.setLocation("Jakarta");
        request.setLicensePlate("B9999TEST");
        request.setPrice(850000.0);
        request.setStatus("Available");

        mockMvc.perform(put("/api/vehicles/INVALID-ID/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void testDeleteVehicleNotFound() throws Exception {
        mockMvc.perform(delete("/api/vehicles/INVALID-ID"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void testMapToVehicleResponseDTO() throws Exception {
        // Test the mapToVehicleResponseDTO method indirectly
        mockMvc.perform(get("/api/vehicles/" + testVehicle.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data.brand").value("Toyota"));
    }

    @Test
    void testMapToRentalVendorResponseDTO() throws Exception {
        // Test the vendor mapping indirectly
        mockMvc.perform(get("/api/vehicles/vendors"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray());
    }
}
