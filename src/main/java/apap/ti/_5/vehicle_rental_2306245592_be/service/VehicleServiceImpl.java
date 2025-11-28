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

    // ============ EXISTING METHODS (KEEP - untuk backward compatibility) ============
    
    @Override
    public List<Vehicle> getAllVehicles() {
        // ⚠️ EXISTING: Untuk backward compatibility dengan endpoint/service lama
        return vehicleRepository.findAll();
    }

    @Override
    public Optional<Vehicle> getVehicleById(String id) {
        // ⚠️ EXISTING: Untuk backward compatibility
        return vehicleRepository.findById(id);
    }

    @Override
    public Vehicle createVehicle(Vehicle vehicle) {
        return vehicleRepository.save(vehicle);
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
        // ✅ UPDATED: Sekarang melakukan soft delete
        Optional<Vehicle> vehicle = vehicleRepository.findById(id);
        if (vehicle.isPresent()) {
            Vehicle v = vehicle.get();
            v.setDeletedAt(LocalDateTime.now());
            vehicleRepository.save(v);
        } else {
            throw new RuntimeException("Vehicle not found with id: " + id);
        }
    }

    @Override
    public List<Vehicle> filterVehiclesByType(String type) {
        // ✅ UPDATED: Sekarang exclude soft deleted
        return vehicleRepository.findByTypeNotDeleted(type);
    }

    @Override
    public List<Vehicle> searchVehicles(String keyword) {
        // ✅ UPDATED: Sekarang exclude soft deleted
        return vehicleRepository.findByBrandOrModelContainingNotDeleted(keyword);
    }

    @Override
    public int getVehicleCount() {
        // ⚠️ EXISTING: Count all (including deleted)
        return (int) vehicleRepository.count();
    }

    @Override
    public List<RentalVendor> getAllVendors() {
        return rentalVendorRepository.findAll();
    }

    @Override
    public VehicleResponseDTO createVehicleFromDTO(CreateVehicleRequestDTO dto) {
        System.out.println("🔍 Creating vehicle from DTO: " + dto);

        // PBI-BE-V3: Validasi unique license plate (exclude soft deleted)
        Optional<Vehicle> existingVehicle = vehicleRepository.findByLicensePlateNotDeleted(dto.getLicensePlate());
        if (existingVehicle.isPresent()) {
            throw new RuntimeException("License plate already exists: " + dto.getLicensePlate());
        }

        // Get rental vendor
        RentalVendor vendor = rentalVendorRepository.findById(dto.getRentalVendorId())
                .orElseThrow(() -> new RuntimeException("Rental Vendor not found with id: " + dto.getRentalVendorId()));

        // PBI-BE-V3: Validasi location ada di list vendor locations
        if (vendor.getListOfLocations() == null || !vendor.getListOfLocations().contains(dto.getLocation())) {
            throw new RuntimeException("Location '" + dto.getLocation() + "' is not in vendor's operation list");
        }

        String vehicleId = generateVehicleId();

        Vehicle vehicle = new Vehicle();
        vehicle.setId(vehicleId);
        vehicle.setRentalVendor(vendor);
        vehicle.setType(dto.getType());
        vehicle.setBrand(dto.getBrand());
        vehicle.setModel(dto.getModel());
        vehicle.setYear(dto.getYear());
        vehicle.setLocation(dto.getLocation());
        vehicle.setLicensePlate(dto.getLicensePlate());
        vehicle.setCapacity(dto.getCapacity());
        vehicle.setTransmission(dto.getTransmission());
        vehicle.setFuelType(dto.getFuelType());
        vehicle.setPrice(dto.getPrice());
        
        // PBI-BE-V3: Default status "Available"
        vehicle.setStatus("Available");
        
        vehicle.setCreatedAt(LocalDateTime.now());
        vehicle.setUpdatedAt(LocalDateTime.now());

        Vehicle savedVehicle = vehicleRepository.save(vehicle);

        return mapToVehicleResponseDTO(savedVehicle);
    }

    @Override
    public VehicleResponseDTO updateVehicleFromDTO(UpdateVehicleRequestDTO dto) {
        System.out.println("🔍 Updating vehicle from DTO: " + dto);

        Vehicle vehicle = vehicleRepository.findById(dto.getId())
                .orElseThrow(() -> new RuntimeException("Vehicle not found with id: " + dto.getId()));

        RentalVendor vendor = rentalVendorRepository.findById(dto.getRentalVendorId())
                .orElseThrow(() -> new RuntimeException("Rental Vendor not found with id: " + dto.getRentalVendorId()));

        // PBI-BE-V3: Validasi location ada di list vendor
        if (vendor.getListOfLocations() == null || !vendor.getListOfLocations().contains(dto.getLocation())) {
            throw new RuntimeException("Location '" + dto.getLocation() + "' is not in vendor's operation list");
        }

        vehicle.setRentalVendor(vendor);
        vehicle.setType(dto.getType());
        vehicle.setBrand(dto.getBrand());
        vehicle.setModel(dto.getModel());
        vehicle.setYear(dto.getYear());
        vehicle.setLocation(dto.getLocation());
        vehicle.setLicensePlate(dto.getLicensePlate());
        vehicle.setCapacity(dto.getCapacity());
        vehicle.setTransmission(dto.getTransmission());
        vehicle.setFuelType(dto.getFuelType());
        vehicle.setPrice(dto.getPrice());
        vehicle.setStatus(dto.getStatus());
        vehicle.setUpdatedAt(LocalDateTime.now());

        Vehicle updatedVehicle = vehicleRepository.save(vehicle);

        return mapToVehicleResponseDTO(updatedVehicle);
    }
    
    // ============ NEW METHODS FOR RBAC & SOFT DELETE ============
    
    @Override
    public List<Vehicle> getAllVehiclesNotDeleted() {
        return vehicleRepository.findAllNotDeleted();
    }
    
    @Override
    public Optional<Vehicle> getVehicleByIdNotDeleted(String id) {
        return vehicleRepository.findByIdNotDeleted(id);
    }
    
    @Override
    public void softDeleteVehicle(String id) {
        Optional<Vehicle> vehicle = vehicleRepository.findByIdNotDeleted(id);
        if (vehicle.isPresent()) {
            Vehicle v = vehicle.get();
            v.setDeletedAt(LocalDateTime.now());
            vehicleRepository.save(v);
            System.out.println("✅ Vehicle soft-deleted: " + id);
        } else {
            throw new RuntimeException("Vehicle not found or already deleted with id: " + id);
        }
    }
    @Override
    public String generateVehicleId() {
        int count = (int) vehicleRepository.count(); // Pastikan casting ke int jika perlu
        int nextId = count + 1;
        return String.format("VEH%04d", nextId);
    }
    @Override
    public boolean canUpdateVehicle(String id) {
        Optional<Vehicle> vehicle = vehicleRepository.findByIdNotDeleted(id);
        if (vehicle.isEmpty()) {
            return false;
        }
        
        String status = vehicle.get().getStatus();
        // Hanya bisa update jika Available atau Maintenance
        return "Available".equals(status) || "Maintenance".equals(status);
    }

    private VehicleResponseDTO mapToVehicleResponseDTO(Vehicle vehicle) {
        if (vehicle == null) {
            return null;
        }

        return VehicleResponseDTO.builder()
                .id(vehicle.getId())
                .rentalVendorId(vehicle.getRentalVendor() != null ? vehicle.getRentalVendor().getId() : null)
                .rentalVendorName(vehicle.getRentalVendor() != null ? vehicle.getRentalVendor().getName() : "")
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