package apap.ti._5.vehicle_rental_2306245592_be.service;

import apap.ti._5.vehicle_rental_2306245592_be.model.RentalAddOn;
import apap.ti._5.vehicle_rental_2306245592_be.model.RentalBooking;
import apap.ti._5.vehicle_rental_2306245592_be.model.Vehicle;
import apap.ti._5.vehicle_rental_2306245592_be.repository.RentalAddOnRepository;
import apap.ti._5.vehicle_rental_2306245592_be.repository.RentalBookingRepository;
import apap.ti._5.vehicle_rental_2306245592_be.repository.VehicleRepository;
import apap.ti._5.vehicle_rental_2306245592_be.restdto.request.booking.AddAddOnsRequestDTO;
import apap.ti._5.vehicle_rental_2306245592_be.restdto.request.booking.CreateBookingRequestDTO;
import apap.ti._5.vehicle_rental_2306245592_be.restdto.request.booking.UpdateBookingRequestDTO;
import apap.ti._5.vehicle_rental_2306245592_be.restdto.request.booking.UpdateBookingStatusDTO;
import apap.ti._5.vehicle_rental_2306245592_be.restdto.request.booking.UpdateAddOnsRequestDTO;
import apap.ti._5.vehicle_rental_2306245592_be.restdto.response.booking.AvailableVehicleResponseDTO;
import apap.ti._5.vehicle_rental_2306245592_be.restdto.response.booking.SearchVehiclesResponseDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
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
        // ✅ UPDATED: Use soft delete query
        return rentalBookingRepository.findAllNotDeleted();
    }

    @Override
    public Optional<RentalBooking> getBookingById(String id) {
        // ✅ UPDATED: Use soft delete query
        return rentalBookingRepository.findByIdNotDeleted(id);
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
        // ✅ UPDATED: Use soft delete query
        return rentalBookingRepository.findByStatusNotDeleted(status);
    }

    @Override
    public List<RentalBooking> getBookingsByVehicleId(String vehicleId) {
        // ✅ UPDATED: Use soft delete query
        return rentalBookingRepository.findByVehicleIdNotDeleted(vehicleId);
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
        // ✅ UPDATED: Use soft delete query
        List<RentalBooking> allBookings = rentalBookingRepository.findAllNotDeletedOrderByIdDesc();
        
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

    @Override
    public Optional<RentalBooking> getBookingForUpdate(String id) {
        System.out.println("🔍 [SERVICE] Getting booking for update: " + id);
        
        Optional<RentalBooking> booking = rentalBookingRepository.findById(id);
        
        if (booking.isPresent()) {
            // Check if booking is in "Upcoming" status only
            if (!"Upcoming".equals(booking.get().getStatus())) {
                System.out.println("❌ Booking status is: " + booking.get().getStatus());
                throw new RuntimeException("Hanya booking dengan status 'Upcoming' yang dapat diubah");
            }
            System.out.println("✅ Booking found and is Upcoming");
            return booking;
        }
        
        System.out.println("❌ Booking not found with ID: " + id);
        throw new RuntimeException("Booking tidak ditemukan dengan ID: " + id);
    }

    @Override
    public RentalBooking updateBookingDetails(String id, UpdateBookingRequestDTO updateDTO) {
        System.out.println("📝 Updating booking: " + id);
        
        // Validate pickup time is in future
        LocalDateTime now = LocalDateTime.now(java.time.ZoneOffset.UTC);
        if (updateDTO.getPickUpTime().isBefore(now.minusMinutes(5))) {
            throw new RuntimeException("Waktu pengambilan tidak boleh di masa lalu");
        }
        
        // Validate time range
        if (updateDTO.getPickUpTime().isAfter(updateDTO.getDropOffTime())) {
            throw new RuntimeException("Waktu pengambilan harus sebelum waktu pengembalian");
        }
        
        // Get existing booking
        RentalBooking booking = rentalBookingRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Booking tidak ditemukan"));
        
        // Check if booking is Upcoming
        if (!"Upcoming".equals(booking.getStatus())) {
            throw new RuntimeException("Hanya booking dengan status 'Upcoming' yang dapat diubah");
        }
        
        // Validate vendor has both locations
        if (booking.getVehicle().getRentalVendor().getListOfLocations() == null) {
            throw new RuntimeException("Vendor tidak memiliki lokasi yang tersedia");
        }
        
        boolean hasPickUp = booking.getVehicle().getRentalVendor().getListOfLocations()
            .contains(updateDTO.getPickUpLocation());
        boolean hasDropOff = booking.getVehicle().getRentalVendor().getListOfLocations()
            .contains(updateDTO.getDropOffLocation());
        
        if (!hasPickUp || !hasDropOff) {
            throw new RuntimeException("Vendor tidak beroperasi di salah satu atau kedua lokasi yang dipilih");
        }
        
        // Calculate new rental days and price
        long hours = ChronoUnit.HOURS.between(updateDTO.getPickUpTime(), updateDTO.getDropOffTime());
        int rentalDays = (int) Math.ceil((double) hours / 24);
        if (rentalDays == 0) rentalDays = 1;
        
        // Calculate new total price (vehicle + driver + existing add-ons)
        double basePrice = rentalDays * booking.getVehicle().getPrice();
        double driverCost = updateDTO.getIncludeDriver() ? rentalDays * DRIVER_COST_PER_DAY : 0;
        
        // Keep existing add-ons cost
        double addOnsCost = booking.getListOfAddOns() != null 
            ? booking.getListOfAddOns().stream().mapToDouble(RentalAddOn::getPrice).sum()
            : 0;
        
        double totalPrice = basePrice + driverCost + addOnsCost;
        
        System.out.println("💰 New price calculation:");
        System.out.println("   Base: " + basePrice + ", Driver: " + driverCost + ", Add-ons: " + addOnsCost + ", Total: " + totalPrice);
        
        // Update booking
        booking.setPickUpLocation(updateDTO.getPickUpLocation());
        booking.setDropOffLocation(updateDTO.getDropOffLocation());
        booking.setPickUpTime(updateDTO.getPickUpTime());
        booking.setDropOffTime(updateDTO.getDropOffTime());
        booking.setCapacityNeeded(updateDTO.getCapacityNeeded());
        booking.setTransmissionNeeded(updateDTO.getTransmissionNeeded());
        booking.setIncludeDriver(updateDTO.getIncludeDriver());
        booking.setTotalPrice(totalPrice);
        
        // Update status based on new pickup time
        booking.setStatus(determineBookingStatus(updateDTO.getPickUpTime()));
        
        RentalBooking updatedBooking = rentalBookingRepository.save(booking);
        System.out.println("✅ Booking updated successfully: " + updatedBooking.getId());
        
        return updatedBooking;
    }

    @Override
    public Optional<RentalBooking> getBookingForUpdateStatus(String id) {
        System.out.println("════════════════════════════════════════════════════");
        System.out.println("🔍 [SERVICE] getBookingForUpdateStatus called");
        System.out.println("   Booking ID: " + id);
        System.out.println("   Timestamp: " + new Date());
        
        try {
            Optional<RentalBooking> booking = rentalBookingRepository.findById(id);
            
            if (booking.isPresent()) {
                System.out.println("✅ Booking found");
                System.out.println("   Status: " + booking.get().getStatus());
                System.out.println("   Vehicle ID: " + booking.get().getVehicle().getId());
                System.out.println("   Pick-up time: " + booking.get().getPickUpTime());
                System.out.println("   Drop-off time: " + booking.get().getDropOffTime());
                return booking;
            }
            
            System.out.println("❌ Booking NOT found in database");
            throw new RuntimeException("Booking tidak ditemukan dengan ID: " + id);
        } catch (Exception e) {
            System.err.println("❌ [SERVICE] Error: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        } finally {
            System.out.println("════════════════════════════════════════════════════");
        }
    }

    @Override
    public List<String> getAvailableStatusTransitions(String currentStatus, String bookingId) {
        System.out.println("════════════════════════════════════════════════════");
        System.out.println("🔍 [SERVICE] getAvailableStatusTransitions called");
        System.out.println("   Current Status: " + currentStatus);
        System.out.println("   Booking ID: " + bookingId);
        
        List<String> availableStatuses = new ArrayList<>();
        
        try {
            RentalBooking booking = rentalBookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking tidak ditemukan"));
            
            LocalDateTime now = LocalDateTime.now();
            System.out.println("   Current time: " + now);
            System.out.println("   Pick-up time: " + booking.getPickUpTime());
            System.out.println("   Drop-off time: " + booking.getDropOffTime());
            
            if ("Upcoming".equals(currentStatus)) {
                System.out.println("📋 Checking transitions from Upcoming...");
                
                boolean isPickupTimeReached = now.isAfter(booking.getPickUpTime());
                boolean isDropoffNotPassed = now.isBefore(booking.getDropOffTime());
                boolean isVehicleAvailable = "Available".equals(booking.getVehicle().getStatus());
                boolean isVehicleAtPickupLocation = booking.getPickUpLocation()
                    .equals(booking.getVehicle().getLocation());
                
                System.out.println("   ✓ Pickup time reached: " + isPickupTimeReached);
                System.out.println("   ✓ Dropoff not passed: " + isDropoffNotPassed);
                System.out.println("   ✓ Vehicle available: " + isVehicleAvailable + " (status: " + booking.getVehicle().getStatus() + ")");
                System.out.println("   ✓ Vehicle at pickup location: " + isVehicleAtPickupLocation + " (vehicle location: " + booking.getVehicle().getLocation() + ")");
                
                if (isPickupTimeReached && isDropoffNotPassed && isVehicleAvailable && isVehicleAtPickupLocation) {
                    availableStatuses.add("Ongoing");
                    System.out.println("✅ Can transition to: Ongoing");
                } else {
                    System.out.println("❌ Cannot transition to Ongoing - conditions not met");
                }
                
            } else if ("Ongoing".equals(currentStatus)) {
                System.out.println("📋 Checking transitions from Ongoing...");
                
                availableStatuses.add("Done");
                System.out.println("✅ Can transition to: Done");
                
            } else if ("Done".equals(currentStatus)) {
                System.out.println("📋 Status is Done - no transitions available");
            } else {
                System.out.println("⚠️  Unknown status: " + currentStatus);
            }
            
            System.out.println("📊 Final available transitions: " + availableStatuses);
            return availableStatuses;
            
        } catch (Exception e) {
            System.err.println("❌ [SERVICE] Error: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        } finally {
            System.out.println("════════════════════════════════════════════════════");
        }
    }

    @Override
    public RentalBooking updateBookingStatus(String id, UpdateBookingStatusDTO updateDTO) {
        System.out.println("════════════════════════════════════════════════════");
        System.out.println("📝 [SERVICE] updateBookingStatus called");
        System.out.println("   Booking ID: " + id);
        System.out.println("   New Status: " + updateDTO.getNewStatus());
        System.out.println("   Timestamp: " + new Date());
        
        try {
            RentalBooking booking = rentalBookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking tidak ditemukan"));
            
            String currentStatus = booking.getStatus();
            String newStatus = updateDTO.getNewStatus();
            
            System.out.println("   Current Status: " + currentStatus);
            System.out.println("   Transition: " + currentStatus + " -> " + newStatus);
            
            // Validate status transition
            List<String> availableTransitions = getAvailableStatusTransitions(currentStatus, id);
            if (!availableTransitions.contains(newStatus)) {
                System.out.println("❌ Invalid transition - not in available list");
                throw new RuntimeException("Status tidak dapat diubah dari " + currentStatus + " menjadi " + newStatus);
            }
            
            System.out.println("✅ Transition is valid");
            
            LocalDateTime now = LocalDateTime.now();
            Vehicle vehicle = booking.getVehicle();
            
            if ("Upcoming".equals(currentStatus) && "Ongoing".equals(newStatus)) {
                System.out.println("🔄 Processing: Upcoming -> Ongoing");
                System.out.println("   Setting booking status to: Ongoing");
                booking.setStatus("Ongoing");
                
                System.out.println("   Setting vehicle status to: In Use");
                vehicle.setStatus("In Use");
                vehicleRepository.save(vehicle);
                
                System.out.println("✅ Transition Upcoming -> Ongoing completed");
                
            } else if ("Ongoing".equals(currentStatus) && "Done".equals(newStatus)) {
                System.out.println("🔄 Processing: Ongoing -> Done");
                
                // Check if late return and calculate penalty
                if (now.isAfter(booking.getDropOffTime())) {
                    long minutesLate = ChronoUnit.MINUTES.between(booking.getDropOffTime(), now);
                    long hoursLate = (long) Math.ceil((double) minutesLate / 60.0);
                    
                    System.out.println("⏰ LATE RETURN DETECTED!");
                    System.out.println("   Drop-off time: " + booking.getDropOffTime());
                    System.out.println("   Current time: " + now);
                    System.out.println("   Minutes late: " + minutesLate);
                    System.out.println("   Hours late (rounded up): " + hoursLate);
                    
                    double penaltyPerHour = 20000.0;
                    double totalPenalty = hoursLate * penaltyPerHour;
                    
                    System.out.println("   Penalty per hour: Rp " + String.format("%.0f", penaltyPerHour));
                    System.out.println("   Total penalty: Rp " + String.format("%.0f", totalPenalty));
                    
                    // Add penalty to total price
                    double oldTotalPrice = booking.getTotalPrice();
                    double newTotalPrice = oldTotalPrice + totalPenalty;
                    booking.setTotalPrice(newTotalPrice);
                    
                    System.out.println("   Old total price: Rp " + String.format("%.0f", oldTotalPrice));
                    System.out.println("   New total price: Rp " + String.format("%.0f", newTotalPrice));
                } else {
                    System.out.println("✅ On-time return (no penalty)");
                }
                
                // Update booking status
                System.out.println("   Setting booking status to: Done");
                booking.setStatus("Done");
                
                // Update vehicle status back to "Available"
                System.out.println("   Setting vehicle status to: Available");
                vehicle.setStatus("Available");
                
                // Update vehicle location to drop-off location
                System.out.println("   Setting vehicle location to: " + booking.getDropOffLocation());
                vehicle.setLocation(booking.getDropOffLocation());
                vehicleRepository.save(vehicle);
                
                System.out.println("✅ Transition Ongoing -> Done completed");
            }
            
            RentalBooking updatedBooking = rentalBookingRepository.save(booking);
            System.out.println("✅ Booking saved successfully");
            System.out.println("   Final status: " + updatedBooking.getStatus());
            System.out.println("   Final price: Rp " + String.format("%.0f", updatedBooking.getTotalPrice()));
            
            return updatedBooking;
            
        } catch (Exception e) {
            System.err.println("❌ [SERVICE] Error: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        } finally {
            System.out.println("════════════════════════════════════════════════════");
        }
    }

    @Override
    public Optional<RentalBooking> getBookingForUpdateAddOns(String id) {
        System.out.println("════════════════════════════════════════════════════");
        System.out.println("🔍 [SERVICE] getBookingForUpdateAddOns called");
        System.out.println("   Booking ID: " + id);
        
        try {
            Optional<RentalBooking> booking = rentalBookingRepository.findById(id);
            
            if (booking.isPresent()) {
                System.out.println("✅ Booking found");
                System.out.println("   Status: " + booking.get().getStatus());
                System.out.println("   Current add-ons: " + (booking.get().getListOfAddOns() != null 
                    ? booking.get().getListOfAddOns().size() : 0));
                
                // Check if booking is in "Upcoming" status only
                if (!"Upcoming".equals(booking.get().getStatus())) {
                    System.out.println("❌ Booking status is: " + booking.get().getStatus());
                    throw new RuntimeException("Hanya booking dengan status 'Upcoming' yang dapat diubah");
                }
                
                return booking;
            }
            
            System.out.println("❌ Booking NOT found in database");
            throw new RuntimeException("Booking tidak ditemukan dengan ID: " + id);
        } catch (Exception e) {
            System.err.println("❌ [SERVICE] Error: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        } finally {
            System.out.println("════════════════════════════════════════════════════");
        }
    }

    @Override
    public RentalBooking updateBookingAddOns(String id, UpdateAddOnsRequestDTO updateDTO) {
        System.out.println("════════════════════════════════════════════════════");
        System.out.println("📝 [SERVICE] updateBookingAddOns called");
        System.out.println("   Booking ID: " + id);
        System.out.println("   Timestamp: " + new Date());
        System.out.println("   Selected add-ons: " + updateDTO.getSelectedAddOnIds());
        
        try {
            RentalBooking booking = rentalBookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking tidak ditemukan"));
            
            // Check if booking is Upcoming
            if (!"Upcoming".equals(booking.getStatus())) {
                System.out.println("❌ Booking status is: " + booking.getStatus());
                throw new RuntimeException("Hanya booking dengan status 'Upcoming' yang dapat diubah");
            }
            
            System.out.println("✅ Booking is Upcoming - can be modified");
            
            // Get old add-ons cost
            double oldAddOnsCost = booking.getListOfAddOns() != null 
                ? booking.getListOfAddOns().stream().mapToDouble(RentalAddOn::getPrice).sum()
                : 0;
            
            System.out.println("💰 Old add-ons cost: " + oldAddOnsCost);
            
            // Get new add-ons
            List<RentalAddOn> newAddOns = updateDTO.getSelectedAddOnIds() != null && !updateDTO.getSelectedAddOnIds().isEmpty()
                ? rentalAddOnRepository.findAllById(updateDTO.getSelectedAddOnIds().stream()
                    .map(UUID::fromString)
                    .collect(Collectors.toList()))
                : new ArrayList<>();
            
            double newAddOnsCost = newAddOns.stream().mapToDouble(RentalAddOn::getPrice).sum();
            
            System.out.println("✅ New add-ons selected: " + newAddOns.size());
            System.out.println("💰 New add-ons cost: " + newAddOnsCost);
            
            // Recalculate total price
            // Total = Base (vehicle + driver) + New add-ons cost
            double basePrice = booking.getTotalPrice() - oldAddOnsCost;
            double newTotalPrice = basePrice + newAddOnsCost;
            
            System.out.println("💰 Price Calculation:");
            System.out.println("   Old total: " + booking.getTotalPrice());
            System.out.println("   Base (vehicle + driver): " + basePrice);
            System.out.println("   Old add-ons cost: -" + oldAddOnsCost);
            System.out.println("   New add-ons cost: +" + newAddOnsCost);
            System.out.println("   New total: " + newTotalPrice);
            
            // Update booking
            booking.setListOfAddOns(newAddOns);
            booking.setTotalPrice(newTotalPrice);
            
            RentalBooking updatedBooking = rentalBookingRepository.save(booking);
            System.out.println("✅ Booking add-ons updated successfully");
            System.out.println("   Final total price: " + updatedBooking.getTotalPrice());
            
            return updatedBooking;
            
        } catch (Exception e) {
            System.err.println("❌ [SERVICE] Error: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        } finally {
            System.out.println("════════════════════════════════════════════════════");
        }
    }

    // ✅ NEW: Cancel booking (soft delete)
    @Override
    public RentalBooking cancelBooking(String id) {
        System.out.println("════════════════════════════════════════════════════");
        System.out.println("🗑️  [SERVICE] cancelBooking called");
        System.out.println("   Booking ID: " + id);
        System.out.println("   Timestamp: " + new Date());
        
        try {
            RentalBooking booking = rentalBookingRepository.findByIdNotDeleted(id)
                .orElseThrow(() -> new RuntimeException("Booking tidak ditemukan atau sudah dibatalkan"));
            
            System.out.println("✅ Booking found");
            System.out.println("   Status: " + booking.getStatus());
            System.out.println("   Pick-up time: " + booking.getPickUpTime());
            
            // Check if booking is Upcoming only
            if (!"Upcoming".equals(booking.getStatus())) {
                System.out.println("❌ Booking status is: " + booking.getStatus());
                throw new RuntimeException("Hanya booking dengan status 'Upcoming' yang dapat dibatalkan");
            }
            
            System.out.println("✅ Booking is Upcoming - can be cancelled");
            
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime pickUpTime = booking.getPickUpTime();
            
            // Check if pickup time has passed
            boolean isPickupTimePassed = now.isAfter(pickUpTime);
            
            System.out.println("⏰ Current time: " + now);
            System.out.println("⏰ Pick-up time: " + pickUpTime);
            System.out.println("⏰ Pickup time passed: " + isPickupTimePassed);
            
            double oldTotalPrice = booking.getTotalPrice();
            
            // If cancelled BEFORE pickup time: total price becomes 0
            if (!isPickupTimePassed) {
                System.out.println("💰 Cancellation BEFORE pickup time - setting price to 0");
                booking.setTotalPrice(0.0);
            } else {
                System.out.println("💰 Cancellation AFTER pickup time - price remains: " + oldTotalPrice);
            }
            
            // Update booking status to Done
            System.out.println("   Setting booking status to: Done");
            booking.setStatus("Done");
            
            // Set deleted_at timestamp (soft delete)
            booking.setDeletedAt(LocalDateTime.now());
            System.out.println("   Setting deletedAt: " + booking.getDeletedAt());
            
            // Update vehicle status back to Available
            Vehicle vehicle = booking.getVehicle();
            System.out.println("🚗 Updating vehicle: " + vehicle.getId());
            System.out.println("   Setting vehicle status to: Available");
            vehicle.setStatus("Available");
            
            // Update vehicle location to drop-off location
            System.out.println("   Setting vehicle location to: " + booking.getDropOffLocation());
            vehicle.setLocation(booking.getDropOffLocation());
            vehicleRepository.save(vehicle);
            
            RentalBooking cancelledBooking = rentalBookingRepository.save(booking);
            
            System.out.println("✅ Booking cancelled successfully (soft delete)");
            System.out.println("   Final status: " + cancelledBooking.getStatus());
            System.out.println("   Final total price: " + cancelledBooking.getTotalPrice());
            System.out.println("   Deleted at: " + cancelledBooking.getDeletedAt());
            
            return cancelledBooking;
            
        } catch (Exception e) {
            System.err.println("❌ [SERVICE] Error: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        } finally {
            System.out.println("════════════════════════════════════════════════════");
        }
    }

    private String determineBookingStatus(LocalDateTime pickUpTime) {
        if (pickUpTime.isAfter(LocalDateTime.now())) {
            return "Upcoming";
        } else {
            return "Ongoing";
        }
    }
}