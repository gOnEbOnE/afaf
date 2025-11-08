package apap.ti._5.vehicle_rental_2306245592_be.controller;

import apap.ti._5.vehicle_rental_2306245592_be.model.Vehicle;
import apap.ti._5.vehicle_rental_2306245592_be.model.RentalVendor;
import apap.ti._5.vehicle_rental_2306245592_be.repository.RentalVendorRepository;
import apap.ti._5.vehicle_rental_2306245592_be.repository.VehicleRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class VehicleControllerEndpointTest {

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
        // Use existing vehicle from database
        testVehicle = vehicleRepository.findAll().stream().findFirst().orElse(null);
        if (testVehicle != null) {
            testVendor = testVehicle.getRentalVendor();
        }
        // If no vehicle exists, use any existing vendor
        if (testVendor == null) {
            testVendor = vendorRepository.findAll().stream().findFirst().orElse(null);
        }
    }

    @Test
    void testGetAllVehicles() throws Exception {
        mockMvc.perform(get("/api/vehicles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    void testGetAllVehiclesWithTypeFilter() throws Exception {
        mockMvc.perform(get("/api/vehicles")
                .param("type", "MPV"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    void testGetAllVehiclesWithKeywordSearch() throws Exception {
        mockMvc.perform(get("/api/vehicles")
                .param("keyword", "Toyota"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    void testGetAllVehiclesWithEmptyFilters() throws Exception {
        mockMvc.perform(get("/api/vehicles")
                .param("type", "")
                .param("keyword", ""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200));
    }

    @Test
    void testGetVehicleById() throws Exception {
        if (testVehicle != null) {
            mockMvc.perform(get("/api/vehicles/" + testVehicle.getId()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value(200))
                    .andExpect(jsonPath("$.data.id").value(testVehicle.getId()));
        }
    }

    @Test
    void testGetVehicleByIdNotFound() throws Exception {
        mockMvc.perform(get("/api/vehicles/NONEXISTENT"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Vehicle not found"));
    }

    // @Test
    // void testCreateVehicleSuccess() throws Exception {
    //     if (testVendor != null) {
    //         String requestBody = String.format("""
    //             {
    //                 "merk": "Honda",
    //                 "model": "CR-V",
    //                 "tahunProduksi": 2024,
    //                 "jenisTransmisi": "Automatic",
    //                 "kapasitas": 5,
    //                 "tipeKendaraan": "SUV",
    //                 "hargaSewa": 500000,
    //                 "biayaSopir": 200000,
    //                 "vendorId": %d
    //             }
    //             """, testVendor.getId());

    //         mockMvc.perform(post("/api/vehicles/create")
    //                 .contentType(MediaType.APPLICATION_JSON)
    //                 .content(requestBody))
    //                 .andExpect(status().isCreated())
    //                 .andExpect(jsonPath("$.status").value(201));
    //     }
    // }

    @Test
    void testCreateVehicleWithInvalidData() throws Exception {
        String requestBody = """
            {
                "merk": "",
                "model": "",
                "tahunProduksi": 1800,
                "jenisTransmisi": "",
                "kapasitas": -1,
                "tipeKendaraan": "",
                "hargaSewa": -100,
                "biayaSopir": -50,
                "vendorId": 999999
            }
            """;

        mockMvc.perform(post("/api/vehicles/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void testGetVehicleForUpdate() throws Exception {
        if (testVehicle != null) {
            mockMvc.perform(get("/api/vehicles/" + testVehicle.getId() + "/update"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value(200))
                    .andExpect(jsonPath("$.data.id").value(testVehicle.getId()))
                    .andExpect(jsonPath("$.message").value("Vehicle retrieved successfully for update"));
        }
    }

    @Test
    void testGetVehicleForUpdateNotFound() throws Exception {
        mockMvc.perform(get("/api/vehicles/NONEXISTENT/update"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Vehicle not found"));
    }

    // @Test
    // void testUpdateVehicleSuccess() throws Exception {
    //     if (testVehicle != null && testVendor != null) {
    //         String requestBody = String.format("""
    //             {
    //                 "id": "%s",
    //                 "merk": "Toyota",
    //                 "model": "Fortuner",
    //                 "tahunProduksi": 2024,
    //                 "jenisTransmisi": "Automatic",
    //                 "kapasitas": 7,
    //                 "tipeKendaraan": "SUV",
    //                 "hargaSewa": 800000,
    //                 "biayaSopir": 250000,
    //                 "vendorId": %d
    //             }
    //             """, testVehicle.getId(), testVendor.getId());

    //         mockMvc.perform(put("/api/vehicles/" + testVehicle.getId() + "/update")
    //                 .contentType(MediaType.APPLICATION_JSON)
    //                 .content(requestBody))
    //                 .andExpect(status().isOk())
    //                 .andExpect(jsonPath("$.status").value(200));
    //     }
    // }

    @Test
    void testUpdateVehicleWithMismatchedId() throws Exception {
        if (testVehicle != null && testVendor != null) {
            String requestBody = String.format("""
                {
                    "id": "WRONG_ID",
                    "merk": "Toyota",
                    "model": "Fortuner",
                    "tahunProduksi": 2024,
                    "jenisTransmisi": "Automatic",
                    "kapasitas": 7,
                    "tipeKendaraan": "SUV",
                    "hargaSewa": 800000,
                    "biayaSopir": 250000,
                    "vendorId": %d
                }
                """, testVendor.getId());

            mockMvc.perform(put("/api/vehicles/" + testVehicle.getId() + "/update")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.message").value("Vehicle ID in path does not match request body"));
        }
    }

    @Test
    void testUpdateVehicleWithInvalidData() throws Exception {
        if (testVehicle != null) {
            String requestBody = String.format("""
                {
                    "id": "%s",
                    "merk": "",
                    "model": "",
                    "tahunProduksi": 1500,
                    "jenisTransmisi": "",
                    "kapasitas": -5,
                    "tipeKendaraan": "",
                    "hargaSewa": -1000,
                    "biayaSopir": -500,
                    "vendorId": 999999
                }
                """, testVehicle.getId());

            mockMvc.perform(put("/api/vehicles/" + testVehicle.getId() + "/update")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(400));
        }
    }

    @Test
    void testGetVehicleCount() throws Exception {
        mockMvc.perform(get("/api/vehicles/count"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data").isNumber())
                .andExpect(jsonPath("$.message").value("Vehicles retrieved successfully"));
    }

    @Test
    void testGetVendorCount() throws Exception {
        mockMvc.perform(get("/api/vehicles/vendor/count"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data").isNumber())
                .andExpect(jsonPath("$.message").value("Vendors retrieved successfully"));
    }

    @Test
    void testDeleteVehicleSuccess() throws Exception {
        if (testVehicle != null) {
            mockMvc.perform(delete("/api/vehicles/" + testVehicle.getId()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value(200))
                    .andExpect(jsonPath("$.message").value("Vehicle deleted successfully"));
        }
    }

    @Test
    void testDeleteVehicleNotFound() throws Exception {
        mockMvc.perform(delete("/api/vehicles/NONEXISTENT"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void testGetAllVendors() throws Exception {
        mockMvc.perform(get("/api/vehicles/vendors"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.message").value("Vendors retrieved successfully"));
    }

    // @Test
    // void testGetAllVendorsReturnsNonEmptyArray() throws Exception {
    //     mockMvc.perform(get("/api/vehicles/vendors"))
    //             .andExpect(status().isOk())
    //             .andExpect(jsonPath("$.data").isArray())
    //             .andExpect(jsonPath("$.data[0].id").exists())
    //             .andExpect(jsonPath("$.data[0].nama").exists());
    // }

    // @Test
    // void testCreateVehicleWithValidVendor() throws Exception {
    //     if (testVendor != null) {
    //         String requestBody = String.format("""
    //             {
    //                 "merk": "Mitsubishi",
    //                 "model": "Pajero",
    //                 "tahunProduksi": 2023,
    //                 "jenisTransmisi": "Automatic",
    //                 "kapasitas": 7,
    //                 "tipeKendaraan": "SUV",
    //                 "hargaSewa": 900000,
    //                 "biayaSopir": 300000,
    //                 "vendorId": %d
    //             }
    //             """, testVendor.getId());

    //         mockMvc.perform(post("/api/vehicles/create")
    //                 .contentType(MediaType.APPLICATION_JSON)
    //                 .content(requestBody))
    //                 .andExpect(status().isCreated())
    //                 .andExpect(jsonPath("$.status").value(201));
    //     }
    // }

    @Test
    void testGetVehiclesByTypeMPV() throws Exception {
        mockMvc.perform(get("/api/vehicles")
                .param("type", "MPV"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200));
    }

    @Test
    void testGetVehiclesByTypeSUV() throws Exception {
        mockMvc.perform(get("/api/vehicles")
                .param("type", "SUV"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200));
    }

    @Test
    void testSearchVehiclesByMerk() throws Exception {
        mockMvc.perform(get("/api/vehicles")
                .param("keyword", "Toyota"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200));
    }

    @Test
    void testSearchVehiclesByModel() throws Exception {
        mockMvc.perform(get("/api/vehicles")
                .param("keyword", "Avanza"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200));
    }

    @Test
    void testSearchVehiclesWithNonMatchingKeyword() throws Exception {
        mockMvc.perform(get("/api/vehicles")
                .param("keyword", "NonExistentVehicle"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data").isArray());
    }
}
