package apap.ti._5.vehicle_rental_2306245592_be.service;

import apap.ti._5.vehicle_rental_2306245592_be.model.RentalVendor;
import apap.ti._5.vehicle_rental_2306245592_be.model.Vehicle;
import apap.ti._5.vehicle_rental_2306245592_be.repository.RentalVendorRepository;
import apap.ti._5.vehicle_rental_2306245592_be.repository.VehicleRepository;
import apap.ti._5.vehicle_rental_2306245592_be.restdto.request.vehicle.CreateVehicleRequestDTO;
import apap.ti._5.vehicle_rental_2306245592_be.restdto.request.vehicle.UpdateVehicleRequestDTO;
import apap.ti._5.vehicle_rental_2306245592_be.restdto.response.vehicle.VehicleResponseDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.Year;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class VehicleServiceImpl implements VehicleService {

    private final VehicleRepository vehicleRepository;
    private final RentalVendorRepository rentalVendorRepository;

    public VehicleServiceImpl(VehicleRepository vehicleRepository, RentalVendorRepository rentalVendorRepository) {
        this.vehicleRepository = vehicleRepository;
        this.rentalVendorRepository = rentalVendorRepository;
    }

    @Override
    public List<Vehicle> getAllVehicles() {
        return vehicleRepository.findAll();
    }
    
    @Override
    public List<RentalVendor> getAllVendors() {
        return rentalVendorRepository.findAll();
    }

    @Override
    public Optional<Vehicle> getVehicleById(String id) {
        return vehicleRepository.findById(id);
    }

    @Override
    public Vehicle createVehicle(Vehicle vehicle) {
        return vehicleRepository.save(vehicle);
    }

    @Override
    public VehicleResponseDTO createVehicleFromDTO(CreateVehicleRequestDTO createVehicleRequestDTO) {
        int currentYear = Year.now().getValue();
        if (createVehicleRequestDTO.getYear() > currentYear) {
            throw new RuntimeException("Vehicle year cannot be greater than current year");
        }

        if (isLicensePlateTaken(createVehicleRequestDTO.getLicensePlate())) {
            throw new RuntimeException("License plate already exists: " + createVehicleRequestDTO.getLicensePlate());
        }

        Optional<RentalVendor> vendor = rentalVendorRepository.findById(createVehicleRequestDTO.getRentalVendorId());
        if (vendor.isEmpty()) {
            throw new RuntimeException("Rental Vendor not found with id: " + createVehicleRequestDTO.getRentalVendorId());
        }

        RentalVendor rentalVendor = vendor.get();
        if (!rentalVendor.getListOfLocations().contains(createVehicleRequestDTO.getLocation())) {
            throw new RuntimeException("Vendor does not operate in location: " + createVehicleRequestDTO.getLocation());
        }

        String vehicleId = generateVehicleId();

        Vehicle vehicle = new Vehicle();
        vehicle.setId(vehicleId);
        vehicle.setRentalVendor(rentalVendor);
        vehicle.setType(createVehicleRequestDTO.getType());
        vehicle.setBrand(createVehicleRequestDTO.getBrand());
        vehicle.setModel(createVehicleRequestDTO.getModel());
        vehicle.setYear(createVehicleRequestDTO.getYear());
        vehicle.setLocation(createVehicleRequestDTO.getLocation());
        vehicle.setLicensePlate(createVehicleRequestDTO.getLicensePlate());
        vehicle.setCapacity(createVehicleRequestDTO.getCapacity());
        vehicle.setTransmission(createVehicleRequestDTO.getTransmission());
        vehicle.setFuelType(createVehicleRequestDTO.getFuelType());
        vehicle.setPrice(createVehicleRequestDTO.getPrice());
        vehicle.setStatus("Available");

        Vehicle savedVehicle = vehicleRepository.save(vehicle);
        return mapToVehicleResponseDTO(savedVehicle);
    }

    @Override
    public VehicleResponseDTO updateVehicleFromDTO(UpdateVehicleRequestDTO updateVehicleRequestDTO) {
        Optional<Vehicle> existingVehicle = vehicleRepository.findById(updateVehicleRequestDTO.getId());
        if (existingVehicle.isEmpty()) {
            throw new RuntimeException("Vehicle not found with id: " + updateVehicleRequestDTO.getId());
        }

        int currentYear = Year.now().getValue();
        if (updateVehicleRequestDTO.getYear() > currentYear) {
            throw new RuntimeException("Vehicle year cannot be greater than current year");
        }

        if (isLicensePlateTakenExcludeId(updateVehicleRequestDTO.getLicensePlate(), updateVehicleRequestDTO.getId())) {
            throw new RuntimeException("License plate already exists: " + updateVehicleRequestDTO.getLicensePlate());
        }

        Optional<RentalVendor> vendor = rentalVendorRepository.findById(updateVehicleRequestDTO.getRentalVendorId());
        if (vendor.isEmpty()) {
            throw new RuntimeException("Rental Vendor not found with id: " + updateVehicleRequestDTO.getRentalVendorId());
        }

        RentalVendor rentalVendor = vendor.get();
        if (!rentalVendor.getListOfLocations().contains(updateVehicleRequestDTO.getLocation())) {
            throw new RuntimeException("Vendor does not operate in location: " + updateVehicleRequestDTO.getLocation());
        }

        Vehicle vehicle = existingVehicle.get();
        vehicle.setRentalVendor(rentalVendor);
        vehicle.setType(updateVehicleRequestDTO.getType());
        vehicle.setBrand(updateVehicleRequestDTO.getBrand());
        vehicle.setModel(updateVehicleRequestDTO.getModel());
        vehicle.setYear(updateVehicleRequestDTO.getYear());
        vehicle.setLocation(updateVehicleRequestDTO.getLocation());
        vehicle.setLicensePlate(updateVehicleRequestDTO.getLicensePlate());
        vehicle.setCapacity(updateVehicleRequestDTO.getCapacity());
        vehicle.setTransmission(updateVehicleRequestDTO.getTransmission());
        vehicle.setFuelType(updateVehicleRequestDTO.getFuelType());
        vehicle.setPrice(updateVehicleRequestDTO.getPrice());
        vehicle.setStatus(updateVehicleRequestDTO.getStatus());

        Vehicle updatedVehicle = vehicleRepository.save(vehicle);
        return mapToVehicleResponseDTO(updatedVehicle);
    }

    @Override
    public Vehicle updateVehicle(String id, Vehicle vehicle) {
        Optional<Vehicle> existingVehicle = vehicleRepository.findById(id);
        if (existingVehicle.isPresent()) {
            vehicle.setId(id);
            return vehicleRepository.save(vehicle);
        }
        throw new RuntimeException("Vehicle not found with id: " + id);
    }

    @Override
    public void deleteVehicle(String id) {
        // ✅ Soft delete - Hibernate @SoftDelete akan mengelola deletedAt
        if (vehicleRepository.existsById(id)) {
            Optional<Vehicle> vehicle = vehicleRepository.findById(id);
            if (vehicle.isPresent()) {
                Vehicle v = vehicle.get();
                v.setStatus("Unavailable");
                v.setDeletedAt(LocalDateTime.now());
                vehicleRepository.save(v);
            }
        } else {
            throw new RuntimeException("Vehicle not found with id: " + id);
        }
    }

    @Override
    public void permanentlyDeleteVehicle(String id) {
        // ✅ Hard delete - gunakan untuk admin saja
        if (vehicleRepository.existsById(id)) {
            vehicleRepository.deleteById(id);
        } else {
            throw new RuntimeException("Vehicle not found with id: " + id);
        }
    }

    @Override
    public void restoreVehicle(String id) {
        // ✅ Restore soft deleted vehicle
        Optional<Vehicle> deletedVehicle = vehicleRepository.findDeletedVehicleById(id);
        if (deletedVehicle.isPresent()) {
            Vehicle v = deletedVehicle.get();
            v.setStatus("Available");
            v.setDeletedAt(null);
            vehicleRepository.save(v);
        } else {
            throw new RuntimeException("Deleted vehicle not found with id: " + id);
        }
    }

    @Override
    public List<Vehicle> searchVehicles(String keyword) {
        return vehicleRepository.findByBrandContainingOrModelContaining(keyword, keyword);
    }

    @Override
    public List<Vehicle> filterVehiclesByType(String type) {
        return vehicleRepository.findByType(type);
    }

    @Override
    public int getVehicleCount() {
        return (int) vehicleRepository.count();
    }

    @Override
    public boolean isLicensePlateTaken(String licensePlate) {
        return vehicleRepository.findByLicensePlate(licensePlate).isPresent();
    }

    @Override
    public boolean isLicensePlateTakenExcludeId(String licensePlate, String vehicleId) {
        Optional<Vehicle> vehicle = vehicleRepository.findByLicensePlate(licensePlate);
        if (vehicle.isEmpty()) {
            return false;
        }
        return !vehicle.get().getId().equals(vehicleId);
    }

    @Override
    public String generateVehicleId() {
        int count = getVehicleCount();
        int nextId = count + 1;
        return String.format("VEH%04d", nextId);
    }

    private VehicleResponseDTO mapToVehicleResponseDTO(Vehicle vehicle) {
        if (vehicle == null) {
            return null;
        }

        String vendorName = "";
        if (vehicle.getRentalVendor() != null) {
            vendorName = vehicle.getRentalVendor().getName();
        }

        return VehicleResponseDTO.builder()
                .id(vehicle.getId())
                .rentalVendorId(vehicle.getRentalVendor() != null ? vehicle.getRentalVendor().getId() : null)
                .rentalVendorName(vendorName)
                .type(vehicle.getType())
                .brand(vehicle.getBrand())
                .model(vehicle.getModel())
                .year(vehicle.getYear())
                .location(vehicle.getLocation())
                .licensePlate(vehicle.getLicensePlate())
                .capacity(vehicle.getCapacity())
                .transmission(vehicle.getTransmission())
                .fuelType(vehicle.getFuelType())
                .price(vehicle.getPrice())
                .status(vehicle.getStatus())
                .createdAt(vehicle.getCreatedAt())
                .updatedAt(vehicle.getUpdatedAt())
                .build();
    }
}