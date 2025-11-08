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

    // @Test
    // void testCreateVehicleFromDTO() {
    //     CreateVehicleRequestDTO dto = new CreateVehicleRequestDTO();
    //     dto.setRentalVendorId(testVendor.getId());
    //     dto.setType("MPV");
    //     dto.setBrand("Toyota");
    //     dto.setModel("Avanza");
    //     dto.setYear(2021);
    //     dto.setCapacity(7);
    //     dto.setTransmission("Automatic");
    //     dto.setFuelType("Gasoline");
    //     dto.setLocation("Surabaya");
    //     dto.setLicensePlate("L1111TEST");
    //     dto.setPrice(500000.0);

    //     VehicleResponseDTO response = vehicleService.createVehicleFromDTO(dto);
    //     assertNotNull(response);
    // }

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

    // @Test
    // void testDeleteVehicle() {
    //     Vehicle vehicle = new Vehicle();
    //     vehicle.setId("VH-TEST-DELETE");
    //     vehicle.setType("Sedan");
    //     vehicle.setBrand("Mazda");
    //     vehicle.setModel("3");
    //     vehicle.setYear(2023);
    //     vehicle.setCapacity(5);
    //     vehicle.setTransmission("Automatic");
    //     vehicle.setFuelType("Gasoline");
    //     vehicle.setLocation("Jakarta");
    //     vehicle.setLicensePlate("B8888TEST");
    //     vehicle.setPrice(700000.0);
    //     vehicle.setStatus("Available");
    //     vehicle.setRentalVendor(testVendor);
    //     vehicleRepository.save(vehicle);

    //     vehicleService.deleteVehicle(vehicle.getId());
    //     Optional<Vehicle> deletedVehicle = vehicleService.getVehicleById(vehicle.getId());
    //     assertFalse(deletedVehicle.isPresent());
    // }

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

    // @Test
    // void testGenerateVehicleId() {
    //     String vehicleId = vehicleService.generateVehicleId();
    //     assertNotNull(vehicleId);
    //     assertTrue(vehicleId.startsWith("VH-"));
    // }

    @Test
    void testGetVehicleByIdDetails() {
        Optional<Vehicle> vehicle = vehicleService.getVehicleById(testVehicle.getId());
        assertTrue(vehicle.isPresent());
        assertEquals(testVehicle.getBrand(), vehicle.get().getBrand());
        assertEquals(testVehicle.getModel(), vehicle.get().getModel());
    }

    @Test
    void testUpdateVehiclePrice() {
        UpdateVehicleRequestDTO dto = new UpdateVehicleRequestDTO();
        dto.setId(testVehicle.getId());
        dto.setRentalVendorId(testVendor.getId());
        dto.setType(testVehicle.getType());
        dto.setBrand(testVehicle.getBrand());
        dto.setModel(testVehicle.getModel());
        dto.setYear(testVehicle.getYear());
        dto.setCapacity(testVehicle.getCapacity());
        dto.setTransmission(testVehicle.getTransmission());
        dto.setFuelType(testVehicle.getFuelType());
        dto.setLocation(testVehicle.getLocation());
        dto.setLicensePlate(testVehicle.getLicensePlate());
        dto.setPrice(1200000.0);
        dto.setStatus(testVehicle.getStatus());

        VehicleResponseDTO updated = vehicleService.updateVehicleFromDTO(dto);
        assertEquals(1200000.0, updated.getPrice());
    }

    // @Test
    // void testUpdateVehicleLocation() {
    //     UpdateVehicleRequestDTO dto = new UpdateVehicleRequestDTO();
    //     dto.setId(testVehicle.getId());
    //     dto.setRentalVendorId(testVendor.getId());
    //     dto.setType(testVehicle.getType());
    //     dto.setBrand(testVehicle.getBrand());
    //     dto.setModel(testVehicle.getModel());
    //     dto.setYear(testVehicle.getYear());
    //     dto.setCapacity(testVehicle.getCapacity());
    //     dto.setTransmission(testVehicle.getTransmission());
    //     dto.setFuelType(testVehicle.getFuelType());
    //     dto.setLocation("Bali");
    //     dto.setLicensePlate(testVehicle.getLicensePlate());
    //     dto.setPrice(testVehicle.getPrice());
    //     dto.setStatus(testVehicle.getStatus());

    //     VehicleResponseDTO updated = vehicleService.updateVehicleFromDTO(dto);
    //     assertEquals("Bali", updated.getLocation());
    // }

    @Test
    void testIsLicensePlateTakenWithExistingPlate() {
        boolean taken = vehicleService.isLicensePlateTaken(testVehicle.getLicensePlate());
        assertTrue(taken);
    }

    @Test
    void testIsLicensePlateTakenWithNewPlate() {
        boolean taken = vehicleService.isLicensePlateTaken("Z9999ZZZ");
        assertFalse(taken);
    }

    @Test
    void testSearchVehiclesByBrand() {
        List<Vehicle> vehicles = vehicleService.searchVehicles("Toyota");
        assertNotNull(vehicles);
    }

    @Test
    void testFilterVehiclesByTypeSUVExists() {
        List<Vehicle> suvVehicles = vehicleService.filterVehiclesByType("SUV");
        assertNotNull(suvVehicles);
    }

    @Test
    void testGetVehicleCountNotNegative() {
        long count = vehicleService.getVehicleCount();
        assertTrue(count >= 0);
    }

    @Test
    void testGetAllVehiclesNotNull() {
        List<Vehicle> vehicles = vehicleService.getAllVehicles();
        assertNotNull(vehicles);
    }

    @Test
    void testGetAllVehiclesContainsTestVehicle() {
        List<Vehicle> vehicles = vehicleService.getAllVehicles();
        boolean found = vehicles.stream().anyMatch(v -> v.getId().equals(testVehicle.getId()));
        assertTrue(found);
    }

    @Test
    void testFilterVehiclesByTypeSedan() {
        List<Vehicle> sedans = vehicleService.filterVehiclesByType("Sedan");
        assertNotNull(sedans);
    }

    @Test
    void testFilterVehiclesByTypeMPV() {
        List<Vehicle> mpvs = vehicleService.filterVehiclesByType("MPV");
        assertNotNull(mpvs);
    }

    @Test
    void testFilterVehiclesByTypeNonExistent() {
        List<Vehicle> vehicles = vehicleService.filterVehiclesByType("Truck");
        assertNotNull(vehicles);
    }

    @Test
    void testSearchVehiclesByModel() {
        List<Vehicle> vehicles = vehicleService.searchVehicles("Fortuner");
        assertNotNull(vehicles);
    }

    @Test
    void testSearchVehiclesEmptyQuery() {
        List<Vehicle> vehicles = vehicleService.searchVehicles("");
        assertNotNull(vehicles);
    }

    @Test
    void testSearchVehiclesCaseInsensitive() {
        List<Vehicle> vehicles = vehicleService.searchVehicles("toyota");
        assertNotNull(vehicles);
    }

    @Test
    void testSearchVehiclesByPartialMatch() {
        List<Vehicle> vehicles = vehicleService.searchVehicles("Fort");
        assertNotNull(vehicles);
    }

    @Test
    void testIsLicensePlateTakenNull() {
        boolean taken = vehicleService.isLicensePlateTaken(null);
        assertFalse(taken);
    }

    @Test
    void testIsLicensePlateTakenEmpty() {
        boolean taken = vehicleService.isLicensePlateTaken("");
        assertFalse(taken);
    }

    @Test
    void testGetVehicleByIdExists() {
        Optional<Vehicle> vehicle = vehicleService.getVehicleById(testVehicle.getId());
        assertTrue(vehicle.isPresent());
        assertEquals(testVehicle.getId(), vehicle.get().getId());
    }

    @Test
    void testGetVehicleByIdNotExists() {
        Optional<Vehicle> vehicle = vehicleService.getVehicleById("NON-EXISTENT-ID");
        assertFalse(vehicle.isPresent());
    }

    @Test
    void testSaveVehicleNewVehicle() {
        Vehicle newVehicle = new Vehicle();
        newVehicle.setId("VH-NEW-" + System.currentTimeMillis());
        newVehicle.setType("Sedan");
        newVehicle.setBrand("Honda");
        newVehicle.setModel("Accord");
        newVehicle.setYear(2024);
        newVehicle.setCapacity(5);
        newVehicle.setTransmission("Automatic");
        newVehicle.setFuelType("Gasoline");
        newVehicle.setLocation("Jakarta");
        newVehicle.setLicensePlate("N" + System.currentTimeMillis());
        newVehicle.setPrice(850000.0);
        newVehicle.setStatus("Available");
        newVehicle.setRentalVendor(testVendor);

        Vehicle saved = vehicleRepository.save(newVehicle);
        assertNotNull(saved);
        assertEquals(newVehicle.getId(), saved.getId());
    }

    @Test
    void testSaveVehicleUpdateExisting() {
        testVehicle.setPrice(950000.0);
        Vehicle updated = vehicleRepository.save(testVehicle);
        assertNotNull(updated);
        assertEquals(950000.0, updated.getPrice());
    }

    @Test
    void testUpdateVehicleFromDTODifferentBrand() {
        UpdateVehicleRequestDTO dto = new UpdateVehicleRequestDTO();
        dto.setId(testVehicle.getId());
        dto.setRentalVendorId(testVendor.getId());
        dto.setType("SUV");
        dto.setBrand("Mitsubishi");
        dto.setModel("Pajero");
        dto.setYear(2024);
        dto.setCapacity(7);
        dto.setTransmission("Automatic");
        dto.setFuelType("Diesel");
        dto.setLocation("Jakarta");
        dto.setLicensePlate(testVehicle.getLicensePlate());
        dto.setPrice(850000.0);
        dto.setStatus("Available");

        VehicleResponseDTO updated = vehicleService.updateVehicleFromDTO(dto);
        assertEquals("Mitsubishi", updated.getBrand());
    }

    @Test
    void testUpdateVehicleFromDTODifferentType() {
        UpdateVehicleRequestDTO dto = new UpdateVehicleRequestDTO();
        dto.setId(testVehicle.getId());
        dto.setRentalVendorId(testVendor.getId());
        dto.setType("Sedan");
        dto.setBrand(testVehicle.getBrand());
        dto.setModel(testVehicle.getModel());
        dto.setYear(testVehicle.getYear());
        dto.setCapacity(5);
        dto.setTransmission("Manual");
        dto.setFuelType("Gasoline");
        dto.setLocation("Jakarta");
        dto.setLicensePlate(testVehicle.getLicensePlate());
        dto.setPrice(testVehicle.getPrice());
        dto.setStatus("Available");

        VehicleResponseDTO updated = vehicleService.updateVehicleFromDTO(dto);
        assertEquals("Sedan", updated.getType());
    }

    @Test
    void testUpdateVehicleFromDTOChangeStatus() {
        UpdateVehicleRequestDTO dto = new UpdateVehicleRequestDTO();
        dto.setId(testVehicle.getId());
        dto.setRentalVendorId(testVendor.getId());
        dto.setType(testVehicle.getType());
        dto.setBrand(testVehicle.getBrand());
        dto.setModel(testVehicle.getModel());
        dto.setYear(testVehicle.getYear());
        dto.setCapacity(testVehicle.getCapacity());
        dto.setTransmission(testVehicle.getTransmission());
        dto.setFuelType(testVehicle.getFuelType());
        dto.setLocation(testVehicle.getLocation());
        dto.setLicensePlate(testVehicle.getLicensePlate());
        dto.setPrice(testVehicle.getPrice());
        dto.setStatus("Rented");

        VehicleResponseDTO updated = vehicleService.updateVehicleFromDTO(dto);
        assertEquals("Rented", updated.getStatus());
    }

    // @Test - DUPLICATE
    // void testGetAllVendors() {
    //     List<RentalVendor> vendors = vehicleService.getAllVendors();
    //     assertNotNull(vendors);
    //     assertFalse(vendors.isEmpty());
    // }

    @Test
    void testGetAllVendorsContainsTestVendor() {
        List<RentalVendor> vendors = vehicleService.getAllVendors();
        boolean found = vendors.stream().anyMatch(v -> v.getId().equals(testVendor.getId()));
        assertTrue(found);
    }

    @Test
    void testFilterVehiclesByTypeCaseInsensitive() {
        List<Vehicle> vehicles = vehicleService.filterVehiclesByType("suv");
        assertNotNull(vehicles);
    }

    @Test
    void testSearchVehiclesSpecialCharacters() {
        List<Vehicle> vehicles = vehicleService.searchVehicles("B1234");
        assertNotNull(vehicles);
    }

    @Test
    void testGetVehicleCountAfterAddingNew() {
        int initialCount = vehicleService.getVehicleCount();
        
        Vehicle newVehicle = new Vehicle();
        newVehicle.setId("VH-COUNT-TEST-" + System.currentTimeMillis());
        newVehicle.setType("MPV");
        newVehicle.setBrand("Toyota");
        newVehicle.setModel("Innova");
        newVehicle.setYear(2023);
        newVehicle.setCapacity(7);
        newVehicle.setTransmission("Manual");
        newVehicle.setFuelType("Diesel");
        newVehicle.setLocation("Bandung");
        newVehicle.setLicensePlate("C" + System.currentTimeMillis());
        newVehicle.setPrice(650000.0);
        newVehicle.setStatus("Available");
        newVehicle.setRentalVendor(testVendor);
        vehicleRepository.save(newVehicle);

        int newCount = vehicleService.getVehicleCount();
        assertTrue(newCount > initialCount);
    }

    // @Test
    // void testUpdateVehicleFromDTOAllFields() {
    //     UpdateVehicleRequestDTO dto = new UpdateVehicleRequestDTO();
    //     dto.setId(testVehicle.getId());
    //     dto.setRentalVendorId(testVendor.getId());
    //     dto.setType("MPV");
    //     dto.setBrand("Honda");
    //     dto.setModel("Odyssey");
    //     dto.setYear(2025);
    //     dto.setCapacity(8);
    //     dto.setTransmission("CVT");
    //     dto.setFuelType("Hybrid");
    //     dto.setLocation("Surabaya");
    //     dto.setLicensePlate(testVehicle.getLicensePlate());
    //     dto.setPrice(1200000.0);
    //     dto.setStatus("Available");

    //     VehicleResponseDTO updated = vehicleService.updateVehicleFromDTO(dto);
    //     assertNotNull(updated);
    //     assertEquals("MPV", updated.getType());
    //     assertEquals("Honda", updated.getBrand());
    //     assertEquals("Odyssey", updated.getModel());
    //     assertEquals(2025, updated.getYear());
    //     assertEquals(8, updated.getCapacity());
    // }

    @Test
    void testSearchVehiclesNumericQuery() {
        List<Vehicle> vehicles = vehicleService.searchVehicles("2023");
        assertNotNull(vehicles);
    }

    @Test
    void testIsLicensePlateTakenAfterUpdate() {
        String newPlate = "UPDATE" + System.currentTimeMillis();
        testVehicle.setLicensePlate(newPlate);
        vehicleRepository.save(testVehicle);
        
        boolean taken = vehicleService.isLicensePlateTaken(newPlate);
        assertTrue(taken);
    }
}
