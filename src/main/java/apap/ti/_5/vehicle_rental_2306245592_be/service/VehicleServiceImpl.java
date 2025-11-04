package apap.ti._5.vehicle_rental_2306245592_be.service;

import apap.ti._5.vehicle_rental_2306245592_be.model.Vehicle;
import apap.ti._5.vehicle_rental_2306245592_be.repository.VehicleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class VehicleServiceImpl implements VehicleService {

    private final VehicleRepository vehicleRepository;

    public VehicleServiceImpl(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    @Override
    public List<Vehicle> getAllVehicles() {
        return vehicleRepository.findAll();
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
        if (vehicleRepository.existsById(id)) {
            vehicleRepository.deleteById(id);
        } else {
            throw new RuntimeException("Vehicle not found with id: " + id);
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
}