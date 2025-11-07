package apap.ti._5.vehicle_rental_2306245592_be.service;

import apap.ti._5.vehicle_rental_2306245592_be.model.RentalAddOn;
import apap.ti._5.vehicle_rental_2306245592_be.model.RentalBooking;
import apap.ti._5.vehicle_rental_2306245592_be.model.Vehicle;
import apap.ti._5.vehicle_rental_2306245592_be.repository.RentalAddOnRepository;
import apap.ti._5.vehicle_rental_2306245592_be.repository.RentalBookingRepository;
import apap.ti._5.vehicle_rental_2306245592_be.repository.VehicleRepository;
import apap.ti._5.vehicle_rental_2306245592_be.restdto.request.booking.AddAddOnsRequestDTO;
import apap.ti._5.vehicle_rental_2306245592_be.restdto.request.booking.CreateBookingRequestDTO;
import apap.ti._5.vehicle_rental_2306245592_be.restdto.response.booking.AvailableVehicleResponseDTO;
import apap.ti._5.vehicle_rental_2306245592_be.restdto.response.booking.SearchVehiclesResponseDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class BookingServiceImpl implements BookingService {

    private final RentalBookingRepository rentalBookingRepository;
    private final VehicleRepository vehicleRepository;
    private final RentalAddOnRepository rentalAddOnRepository;
    private final LocationService locationService;
    
    private static final Double DRIVER_COST_PER_DAY = 100000.0;

    public BookingServiceImpl(
            RentalBookingRepository rentalBookingRepository,
            VehicleRepository vehicleRepository,
            RentalAddOnRepository rentalAddOnRepository,
            LocationService locationService) {
        this.rentalBookingRepository = rentalBookingRepository;
        this.vehicleRepository = vehicleRepository;
        this.rentalAddOnRepository = rentalAddOnRepository;
        this.locationService = locationService;
    }

    @Override
    public List<RentalBooking> getAllBookings() {
        return rentalBookingRepository.findAll();
    }

    @Override
    public Optional<RentalBooking> getBookingById(String id) {
        return rentalBookingRepository.findById(id);
    }

    @Override
    public RentalBooking createBooking(RentalBooking booking) {
        return rentalBookingRepository.save(booking);
    }

    @Override
    public RentalBooking updateBooking(String id, RentalBooking booking) {
        Optional<RentalBooking> existingBooking = rentalBookingRepository.findById(id);
        if (existingBooking.isPresent()) {
            booking.setId(id);
            return rentalBookingRepository.save(booking);
        }
        throw new RuntimeException("Booking not found with id: " + id);
    }

    @Override
    public void deleteBooking(String id) {
        if (rentalBookingRepository.existsById(id)) {
            rentalBookingRepository.deleteById(id);
        } else {
            throw new RuntimeException("Booking not found with id: " + id);
        }
    }

    @Override
    public List<RentalBooking> getBookingsByStatus(String status) {
        return rentalBookingRepository.findByStatus(status);
    }

    @Override
    public List<RentalBooking> getBookingsByVehicleId(String vehicleId) {
        return rentalBookingRepository.findByVehicleId(vehicleId);
    }

    @Override
    public int getBookingCount() {
        return (int) rentalBookingRepository.count();
    }

    @Override
    public SearchVehiclesResponseDTO searchAvailableVehicles(CreateBookingRequestDTO criteria) {
        System.out.println("🔍 Searching vehicles with criteria: " + criteria);
        
        // Validate input
        if (criteria.getPickUpTime() == null || criteria.getDropOffTime() == null) {
            throw new RuntimeException("Pick-up time and drop-off time cannot be null");
        }
        
        if (criteria.getPickUpTime().isAfter(criteria.getDropOffTime())) {
            throw new RuntimeException("Pick-up time must be before drop-off time");
        }
        
        LocalDateTime now = LocalDateTime.now(java.time.ZoneOffset.UTC);
        System.out.println("⏰ Current UTC time: " + now);
        System.out.println("⏰ Pick-up time: " + criteria.getPickUpTime());
        
        LocalDateTime minAllowedTime = now.minusMinutes(5);
        
        if (criteria.getPickUpTime().isBefore(minAllowedTime)) {
            throw new RuntimeException("Pick-up time cannot be lebih dari 5 menit di masa lalu");
        }

        // Calculate rental days
        long hours = ChronoUnit.HOURS.between(criteria.getPickUpTime(), criteria.getDropOffTime());
        int rentalDaysCalc = (int) Math.ceil((double) hours / 24);
        final int rentalDays = rentalDaysCalc == 0 ? 1 : rentalDaysCalc;
        System.out.println("✅ Rental days calculated: " + rentalDays);

        // DEBUG: Get ALL available vehicles first
        List<Vehicle> allAvailable = vehicleRepository.findAllAvailable();
        System.out.println("📊 Total vehicles with status Available: " + allAvailable.size());
        allAvailable.forEach(v -> System.out.println("   - " + v.getBrand() + " " + v.getModel() + 
                " (Transmission: " + v.getTransmission() + ", Capacity: " + v.getCapacity() + ")"));

        // Now filter by transmission and capacity
        List<Vehicle> availableVehicles = vehicleRepository.findAvailableVehicles(
            criteria.getTransmissionNeeded(),
            criteria.getCapacityNeeded(),
            criteria.getPickUpTime(),
            criteria.getDropOffTime()
        );

        System.out.println("✅ Found " + availableVehicles.size() + " vehicles matching transmission (" + 
                criteria.getTransmissionNeeded() + ") and capacity >= " + criteria.getCapacityNeeded());

        // Filter by vendor locations - PRODUCTION MODE: Enable location check
        List<Vehicle> filteredVehicles = availableVehicles.stream()
            .filter(v -> {
                System.out.println("\n🚗 Vehicle: " + v.getBrand() + " " + v.getModel() + " (ID: " + v.getId() + ")");
                System.out.println("   Vendor: " + v.getRentalVendor().getName());
                System.out.println("   Vendor Locations: " + v.getRentalVendor().getListOfLocations());
                System.out.println("   Pick-up Location: " + criteria.getPickUpLocation());
                System.out.println("   Drop-off Location: " + criteria.getDropOffLocation());
                
                // Validate vendor has both pick-up and drop-off locations
                if (v.getRentalVendor().getListOfLocations() == null) {
                    System.out.println("   ❌ Vendor locations is NULL");
                    return false;
                }
                
                boolean hasPickUpLocation = v.getRentalVendor().getListOfLocations()
                    .contains(criteria.getPickUpLocation());
                boolean hasDropOffLocation = v.getRentalVendor().getListOfLocations()
                    .contains(criteria.getDropOffLocation());
                
                System.out.println("   Has Pick-up Location: " + hasPickUpLocation);
                System.out.println("   Has Drop-off Location: " + hasDropOffLocation);
                
                boolean isValid = hasPickUpLocation && hasDropOffLocation;
                System.out.println("   ✅ Include: " + isValid);
                
                return isValid;
            })
            .collect(Collectors.toList());

        System.out.println("\n✅ After location filter: " + filteredVehicles.size() + " vehicles");

        // Convert to DTO and calculate total price
        List<AvailableVehicleResponseDTO> vehicleDTOs = filteredVehicles.stream()
            .map(vehicle -> {
                // ✅ HANYA base cost (tanpa driver)
                double baseCost = rentalDays * vehicle.getPrice();

                System.out.println("💰 Vehicle " + vehicle.getBrand() + " " + vehicle.getModel());
                System.out.println("   Base Cost: " + baseCost);

                return AvailableVehicleResponseDTO.builder()
                    .id(vehicle.getId())
                    .type(vehicle.getType())
                    .brand(vehicle.getBrand())
                    .model(vehicle.getModel())
                    .transmission(vehicle.getTransmission())
                    .fuelType(vehicle.getFuelType())
                    .capacity(vehicle.getCapacity())
                    .pricePerDay(vehicle.getPrice())
                    .totalPrice(baseCost)  // ✅ BASE PRICE ONLY - driver cost dihitung di frontend
                    .rentalDays(rentalDays)
                    .build();
            })
            .sorted((a, b) -> Double.compare(a.getTotalPrice(), b.getTotalPrice()))
            .collect(Collectors.toList());

        System.out.println("✅ Search completed. Total vehicles: " + vehicleDTOs.size());

        return SearchVehiclesResponseDTO.builder()
            .availableVehicles(vehicleDTOs)
            .rentalDays(rentalDays)
            .driverCostPerDay(DRIVER_COST_PER_DAY)  // ✅ Kirim driver cost per day
            .includeDriver(criteria.getIncludeDriver())
            .build();
    }

    @Override
    public RentalBooking createBookingWithAddOns(CreateBookingRequestDTO bookingDTO, AddAddOnsRequestDTO addOnsDTO) {
        // Validate vehicle exists
        Vehicle vehicle = vehicleRepository.findById(addOnsDTO.getVehicleId())
            .orElseThrow(() -> new RuntimeException("Vehicle not found"));

        // Generate booking ID
        String bookingId = generateBookingId();

        // Calculate rental days
        long hours = ChronoUnit.HOURS.between(bookingDTO.getPickUpTime(), bookingDTO.getDropOffTime());
        int rentalDays = (int) Math.ceil((double) hours / 24);
        if (rentalDays == 0) rentalDays = 1;

        // Calculate total price
        double basePrice = rentalDays * vehicle.getPrice();
        double driverCost = bookingDTO.getIncludeDriver() ? rentalDays * DRIVER_COST_PER_DAY : 0;

        // Get selected add-ons
        List<RentalAddOn> addOns = rentalAddOnRepository.findAllById(addOnsDTO.getSelectedAddOnIds());
        double addOnsCost = addOns.stream().mapToDouble(RentalAddOn::getPrice).sum();

        double totalPrice = basePrice + driverCost + addOnsCost;

        // Determine status
        String status = determineBookingStatus(bookingDTO.getPickUpTime());

        // Create booking
        RentalBooking booking = new RentalBooking();
        booking.setId(bookingId);
        booking.setVehicle(vehicle);
        booking.setPickUpTime(bookingDTO.getPickUpTime());
        booking.setDropOffTime(bookingDTO.getDropOffTime());
        booking.setPickUpLocation(bookingDTO.getPickUpLocation());
        booking.setDropOffLocation(bookingDTO.getDropOffLocation());
        booking.setCapacityNeeded(bookingDTO.getCapacityNeeded());
        booking.setTransmissionNeeded(bookingDTO.getTransmissionNeeded());
        booking.setTotalPrice(totalPrice);
        booking.setIncludeDriver(bookingDTO.getIncludeDriver());
        booking.setStatus(status);
        booking.setListOfAddOns(addOns);

        return rentalBookingRepository.save(booking);
    }

    @Override
    public String generateBookingId() {
        List<RentalBooking> allBookings = rentalBookingRepository.findAllOrderByIdDesc();
        
        int nextSequence = 1;
        if (!allBookings.isEmpty()) {
            String lastId = allBookings.get(0).getId();
            try {
                // Extract numeric part from "VR000001"
                String numericPart = lastId.substring(2);
                int lastSequence = Integer.parseInt(numericPart);
                nextSequence = lastSequence + 1;
            } catch (NumberFormatException e) {
                System.err.println("⚠️ Could not parse booking ID: " + lastId);
            }
        }
        
        return String.format("VR%06d", nextSequence);
    }

    @Override
    public List<RentalAddOn> getAllAddOns() {
        return rentalAddOnRepository.findAll();
    }

    @Override
    public List<String> getAllProvinces() {
        locationService.initializeProvinces();
        return locationService.getProvinceList();
    }

    private String determineBookingStatus(LocalDateTime pickUpTime) {
        if (pickUpTime.isAfter(LocalDateTime.now())) {
            return "Upcoming";
        } else {
            return "Ongoing";
        }
    }
}