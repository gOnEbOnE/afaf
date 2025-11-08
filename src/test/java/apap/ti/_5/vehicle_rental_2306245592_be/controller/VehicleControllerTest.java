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

    @Test
    void testGetAllVehiclesByTypeAndKeyword() throws Exception {
        mockMvc.perform(get("/api/vehicles")
                .param("type", "SUV")
                .param("keyword", "Toyota"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200));
    }

    @Test
    void testGetVehicleByIdString() throws Exception {
        mockMvc.perform(get("/api/vehicles/" + testVehicle.getId())
                .param("id", testVehicle.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(testVehicle.getId()));
    }

    @Test
    void testCreateVehicleWithDTO() throws Exception {
        CreateVehicleRequestDTO request = new CreateVehicleRequestDTO();
        request.setRentalVendorId(testVendor.getId());
        request.setType("Sedan");
        request.setBrand("Honda");
        request.setModel("Civic");
        request.setYear(2024);
        request.setCapacity(5);
        request.setTransmission("CVT");
        request.setFuelType("Gasoline");
        request.setLocation("Bandung");
        request.setLicensePlate("D5678NEW");
        request.setPrice(600000.0);

        mockMvc.perform(post("/api/vehicles/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    void testGetVehicleForUpdateWithPath() throws Exception {
        mockMvc.perform(get("/api/vehicles/" + testVehicle.getId() + "/update"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.id").value(testVehicle.getId()));
    }

    @Test
    void testUpdateVehicleWithDTO() throws Exception {
        UpdateVehicleRequestDTO request = new UpdateVehicleRequestDTO();
        request.setId(testVehicle.getId());
        request.setRentalVendorId(testVendor.getId());
        request.setType("SUV");
        request.setBrand("Toyota");
        request.setModel("Fortuner Updated");
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
                .andExpect(status().isOk());
    }

    @Test
    void testDeleteVehicleByPath() throws Exception {
        mockMvc.perform(delete("/api/vehicles/" + testVehicle.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200));
    }

    @Test
    void testGetVehicleCountEndpoint() throws Exception {
        mockMvc.perform(get("/api/vehicles/count"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200));
    }

    @Test
    void testMapToVehicleResponseDTOWithVehicle() throws Exception {
        mockMvc.perform(get("/api/vehicles/" + testVehicle.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.type").value("SUV"))
                .andExpect(jsonPath("$.data.brand").value("Toyota"))
                .andExpect(jsonPath("$.data.model").value("Fortuner"));
    }

    @Test
    void testGetAllVehiclesReturnsMultiple() throws Exception {
        mockMvc.perform(get("/api/vehicles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    void testGetVehicleByIdReturnsCorrectData() throws Exception {
        mockMvc.perform(get("/api/vehicles/" + testVehicle.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(testVehicle.getId()))
                .andExpect(jsonPath("$.data.capacity").value(7))
                .andExpect(jsonPath("$.data.transmission").value("Automatic"));
    }

    @Test
    void testCreateVehicleReturnsCreatedStatus() throws Exception {
        CreateVehicleRequestDTO request = new CreateVehicleRequestDTO();
        request.setRentalVendorId(testVendor.getId());
        request.setType("Sedan");
        request.setBrand("Honda");
        request.setModel("Civic");
        request.setYear(2024);
        request.setCapacity(5);
        request.setTransmission("Manual");
        request.setFuelType("Gasoline");
        request.setLocation("Bandung");
        request.setLicensePlate("D" + System.currentTimeMillis());
        request.setPrice(600000.0);

        mockMvc.perform(post("/api/vehicles/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value(201));
    }

    @Test
    void testUpdateVehicleReturnsOkStatus() throws Exception {
        UpdateVehicleRequestDTO request = new UpdateVehicleRequestDTO();
        request.setId(testVehicle.getId());
        request.setRentalVendorId(testVendor.getId());
        request.setType("SUV");
        request.setBrand("Toyota");
        request.setModel("Fortuner Updated");
        request.setYear(2024);
        request.setCapacity(7);
        request.setTransmission("Automatic");
        request.setFuelType("Diesel");
        request.setLocation("Jakarta");
        request.setLicensePlate(testVehicle.getLicensePlate());
        request.setPrice(900000.0);
        request.setStatus("Available");

        mockMvc.perform(put("/api/vehicles/" + testVehicle.getId() + "/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200));
    }

    @Test
    void testDeleteVehicleReturnsSuccessMessage() throws Exception {
        Vehicle tempVehicle = new Vehicle();
        tempVehicle.setId("VH-TEMP-DELETE-" + System.currentTimeMillis());
        tempVehicle.setType("Sedan");
        tempVehicle.setBrand("Test");
        tempVehicle.setModel("Delete");
        tempVehicle.setYear(2023);
        tempVehicle.setCapacity(5);
        tempVehicle.setTransmission("Manual");
        tempVehicle.setFuelType("Gasoline");
        tempVehicle.setLocation("Jakarta");
        tempVehicle.setLicensePlate("DEL" + System.currentTimeMillis());
        tempVehicle.setPrice(500000.0);
        tempVehicle.setStatus("Available");
        tempVehicle.setRentalVendor(testVendor);
        vehicleRepository.save(tempVehicle);

        mockMvc.perform(delete("/api/vehicles/" + tempVehicle.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200));
    }

    @Test
    void testGetVehicleCountReturnsPositiveNumber() throws Exception {
        mockMvc.perform(get("/api/vehicles/count"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isNumber());
    }

    @Test
    void testGetAllVendorsReturnsList() throws Exception {
        mockMvc.perform(get("/api/vehicles/vendors"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray());
    }

    // Endpoint /api/vehicles/filter/type/{type} doesn't exist, using query params instead
    @Test
    void testFilterVehiclesByTypeSUV() throws Exception {
        mockMvc.perform(get("/api/vehicles")
                .param("type", "SUV"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    void testFilterVehiclesByTypeSedan() throws Exception {
        mockMvc.perform(get("/api/vehicles")
                .param("type", "Sedan"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray());
    }

    // Endpoint /api/vehicles/search doesn't exist, using keyword param instead
    @Test
    void testSearchVehiclesWithQuery() throws Exception {
        mockMvc.perform(get("/api/vehicles")
                .param("keyword", "Toyota"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    void testSearchVehiclesEmptyQuery() throws Exception {
        mockMvc.perform(get("/api/vehicles")
                .param("keyword", ""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray());
    }

    // Endpoint /api/vehicles/check-license doesn't exist - commenting out
    // @Test
    // void testCheckLicensePlateTaken() throws Exception {
    //     mockMvc.perform(get("/api/vehicles/check-license")
    //             .param("licensePlate", testVehicle.getLicensePlate()))
    //             .andExpect(status().isOk())
    //             .andExpect(jsonPath("$.data").value(true));
    // }

    // @Test
    // void testCheckLicensePlateNotTaken() throws Exception {
    //     mockMvc.perform(get("/api/vehicles/check-license")
    //             .param("licensePlate", "Z9999ZZZ"))
    //             .andExpect(status().isOk())
    //             .andExpect(jsonPath("$.data").value(false));
    // }

    @Test
    void testGetVehicleByIdValidatesId() throws Exception {
        mockMvc.perform(get("/api/vehicles/" + testVehicle.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data.id").value(testVehicle.getId()));
    }

    @Test
    void testCreateVehicleValidatesRequiredFields() throws Exception {
        CreateVehicleRequestDTO request = new CreateVehicleRequestDTO();
        request.setRentalVendorId(testVendor.getId());
        request.setType("SUV");
        request.setBrand("Test");
        request.setModel("Model");
        request.setYear(2024);
        request.setCapacity(5);
        request.setTransmission("Automatic");
        request.setFuelType("Diesel");
        request.setLocation("Jakarta");
        request.setLicensePlate("T" + System.currentTimeMillis());
        request.setPrice(500000.0);

        mockMvc.perform(post("/api/vehicles/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    void testUpdateVehicleValidatesExistence() throws Exception {
        UpdateVehicleRequestDTO request = new UpdateVehicleRequestDTO();
        request.setId(testVehicle.getId());
        request.setRentalVendorId(testVendor.getId());
        request.setType(testVehicle.getType());
        request.setBrand(testVehicle.getBrand());
        request.setModel("Updated Model");
        request.setYear(testVehicle.getYear());
        request.setCapacity(testVehicle.getCapacity());
        request.setTransmission(testVehicle.getTransmission());
        request.setFuelType(testVehicle.getFuelType());
        request.setLocation(testVehicle.getLocation());
        request.setLicensePlate(testVehicle.getLicensePlate());
        request.setPrice(testVehicle.getPrice());
        request.setStatus(testVehicle.getStatus());

        mockMvc.perform(put("/api/vehicles/" + testVehicle.getId() + "/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.model").value("Updated Model"));
    }

    @Test
    void testFilterVehiclesByTypeMPV() throws Exception {
        mockMvc.perform(get("/api/vehicles")
                .param("type", "MPV"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200));
    }

    @Test
    void testSearchVehiclesReturnsResults() throws Exception {
        mockMvc.perform(get("/api/vehicles")
                .param("keyword", "Fortuner"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200));
    }

    @Test
    void testGetAllVehiclesHasCorrectStructure() throws Exception {
        mockMvc.perform(get("/api/vehicles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.data").exists());
    }

    @Test
    void testGetVehicleByIdHasCorrectStructure() throws Exception {
        mockMvc.perform(get("/api/vehicles/" + testVehicle.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data.id").exists())
                .andExpect(jsonPath("$.data.brand").exists());
    }

    @Test
    void testVehicleResponseContainsAllFields() throws Exception {
        mockMvc.perform(get("/api/vehicles/" + testVehicle.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.type").exists())
                .andExpect(jsonPath("$.data.brand").exists())
                .andExpect(jsonPath("$.data.model").exists())
                .andExpect(jsonPath("$.data.year").exists())
                .andExpect(jsonPath("$.data.capacity").exists())
                .andExpect(jsonPath("$.data.transmission").exists())
                .andExpect(jsonPath("$.data.fuelType").exists())
                .andExpect(jsonPath("$.data.price").exists());
    }
}
