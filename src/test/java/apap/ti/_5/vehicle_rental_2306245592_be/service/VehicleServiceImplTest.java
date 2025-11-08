package apap.ti._5.vehicle_rental_2306245592_be.service;

import apap.ti._5.vehicle_rental_2306245592_be.model.RentalVendor;
import apap.ti._5.vehicle_rental_2306245592_be.model.Vehicle;
import apap.ti._5.vehicle_rental_2306245592_be.repository.RentalVendorRepository;
import apap.ti._5.vehicle_rental_2306245592_be.repository.VehicleRepository;
import apap.ti._5.vehicle_rental_2306245592_be.restdto.request.vehicle.CreateVehicleRequestDTO;
import apap.ti._5.vehicle_rental_2306245592_be.restdto.request.vehicle.UpdateVehicleRequestDTO;
import apap.ti._5.vehicle_rental_2306245592_be.restdto.response.vehicle.VehicleResponseDTO;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class VehicleServiceImplTest {

    @Autowired
    private VehicleService vehicleService;

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
    void testGetAllVehicles() {
        List<Vehicle> vehicles = vehicleService.getAllVehicles();
        assertNotNull(vehicles);
        assertTrue(vehicles.size() >= 1);
    }

    @Test
    void testGetAllVendors() {
        List<RentalVendor> vendors = vehicleService.getAllVendors();
        assertNotNull(vendors);
        assertTrue(vendors.size() >= 1);
    }

    @Test
    void testGetVehicleById() {
        Optional<Vehicle> vehicle = vehicleService.getVehicleById(testVehicle.getId());
        assertTrue(vehicle.isPresent());
        assertEquals(testVehicle.getId(), vehicle.get().getId());
    }

    @Test
    void testCreateVehicle() {
        Vehicle vehicle = new Vehicle();
        vehicle.setId("VH-TEST-002");
        vehicle.setType("Sedan");
        vehicle.setBrand("Honda");
        vehicle.setModel("Civic");
        vehicle.setYear(2022);
        vehicle.setCapacity(5);
        vehicle.setTransmission("Manual");
        vehicle.setFuelType("Gasoline");
        vehicle.setLocation("Bandung");
        vehicle.setLicensePlate("B5678TEST");
        vehicle.setPrice(600000.0);
        vehicle.setStatus("Available");
        vehicle.setRentalVendor(testVendor);

        Vehicle savedVehicle = vehicleService.createVehicle(vehicle);
        assertNotNull(savedVehicle);
        assertEquals("VH-TEST-002", savedVehicle.getId());
    }

    @Test
    void testCreateVehicleFromDTO() {
        CreateVehicleRequestDTO dto = new CreateVehicleRequestDTO();
        dto.setRentalVendorId(testVendor.getId());
        dto.setType("MPV");
        dto.setBrand("Toyota");
        dto.setModel("Avanza");
        dto.setYear(2021);
        dto.setCapacity(7);
        dto.setTransmission("Automatic");
        dto.setFuelType("Gasoline");
        dto.setLocation("Surabaya");
        dto.setLicensePlate("L1111TEST");
        dto.setPrice(500000.0);

        VehicleResponseDTO response = vehicleService.createVehicleFromDTO(dto);
        assertNotNull(response);
    }

    @Test
    void testUpdateVehicleFromDTO() {
        Vehicle vehicle = new Vehicle();
        vehicle.setId("VH-TEST-003");
        vehicle.setType("SUV");
        vehicle.setBrand("Mitsubishi");
        vehicle.setModel("Pajero");
        vehicle.setYear(2020);
        vehicle.setCapacity(7);
        vehicle.setTransmission("Automatic");
        vehicle.setFuelType("Diesel");
        vehicle.setLocation("Jakarta");
        vehicle.setLicensePlate("B9999TEST");
        vehicle.setPrice(750000.0);
        vehicle.setStatus("Available");
        vehicle.setRentalVendor(testVendor);
        Vehicle savedVehicle = vehicleRepository.save(vehicle);

        UpdateVehicleRequestDTO dto = new UpdateVehicleRequestDTO();
        dto.setId(savedVehicle.getId());
        dto.setRentalVendorId(testVendor.getId());
        dto.setType("SUV");
        dto.setBrand("Mitsubishi");
        dto.setModel("Pajero Sport");
        dto.setYear(2024);
        dto.setCapacity(7);
        dto.setTransmission("Automatic");
        dto.setFuelType("Diesel");
        dto.setLocation("Jakarta");
        dto.setLicensePlate("B9999TEST");
        dto.setPrice(900000.0);
        dto.setStatus("Available");

        VehicleResponseDTO response = vehicleService.updateVehicleFromDTO(dto);
        assertNotNull(response);
    }

    @Test
    void testDeleteVehicle() {
        Vehicle vehicle = new Vehicle();
        vehicle.setId("VH-TEST-DELETE");
        vehicle.setType("Sedan");
        vehicle.setBrand("Mazda");
        vehicle.setModel("3");
        vehicle.setYear(2023);
        vehicle.setCapacity(5);
        vehicle.setTransmission("Automatic");
        vehicle.setFuelType("Gasoline");
        vehicle.setLocation("Jakarta");
        vehicle.setLicensePlate("B8888TEST");
        vehicle.setPrice(700000.0);
        vehicle.setStatus("Available");
        vehicle.setRentalVendor(testVendor);
        vehicleRepository.save(vehicle);

        vehicleService.deleteVehicle(vehicle.getId());
        Optional<Vehicle> deletedVehicle = vehicleService.getVehicleById(vehicle.getId());
        assertFalse(deletedVehicle.isPresent());
    }

    @Test
    void testFilterVehiclesByType() {
        List<Vehicle> suvs = vehicleService.filterVehiclesByType("SUV");
        assertNotNull(suvs);
    }

    @Test
    void testSearchVehicles() {
        List<Vehicle> results = vehicleService.searchVehicles("Toyota");
        assertNotNull(results);
    }

    @Test
    void testGetVehicleCount() {
        int count = vehicleService.getVehicleCount();
        assertTrue(count >= 1);
    }

    @Test
    void testIsLicensePlateTaken() {
        boolean taken = vehicleService.isLicensePlateTaken("B1234TEST");
        assertTrue(taken);
    }

    @Test
    void testGenerateVehicleId() {
        String vehicleId = vehicleService.generateVehicleId();
        assertNotNull(vehicleId);
        assertTrue(vehicleId.startsWith("VH-"));
    }
}
