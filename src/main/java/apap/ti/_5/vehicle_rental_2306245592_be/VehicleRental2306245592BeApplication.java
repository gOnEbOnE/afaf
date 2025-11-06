package apap.ti._5.vehicle_rental_2306245592_be;

import apap.ti._5.vehicle_rental_2306245592_be.model.RentalAddOn;
import apap.ti._5.vehicle_rental_2306245592_be.model.RentalBooking;
import apap.ti._5.vehicle_rental_2306245592_be.model.RentalVendor;
import apap.ti._5.vehicle_rental_2306245592_be.model.Vehicle;
import apap.ti._5.vehicle_rental_2306245592_be.repository.RentalAddOnRepository;
import apap.ti._5.vehicle_rental_2306245592_be.repository.RentalBookingRepository;
import apap.ti._5.vehicle_rental_2306245592_be.repository.RentalVendorRepository;
import apap.ti._5.vehicle_rental_2306245592_be.repository.VehicleRepository;
import apap.ti._5.vehicle_rental_2306245592_be.service.LocationService;
import com.github.javafaker.Faker;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@SpringBootApplication
public class VehicleRental2306245592BeApplication {

    public static void main(String[] args) {
        SpringApplication.run(VehicleRental2306245592BeApplication.class, args);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    @Profile("!test")
    public CommandLineRunner createDummyData(
            RentalVendorRepository rentalVendorRepository,
            RentalAddOnRepository rentalAddOnRepository,
            VehicleRepository vehicleRepository,
            RentalBookingRepository rentalBookingRepository,
            LocationService locationService) {
        return args -> {
            System.out.println("Generating dummy data...");
            
            // Initialize provinces from API
            locationService.initializeProvinces();
            
            Faker faker = new Faker(Locale.of("id_ID"));

            // Generate RentalVendors
            System.out.println("Generating dummy rental vendors...");
            List<RentalVendor> vendors = new ArrayList<>();
            for (int i = 0; i < 5; i++) {
                RentalVendor vendor = new RentalVendor();
                vendor.setName(faker.company().name() + " Rental");
                vendor.setEmail(faker.internet().emailAddress());
                vendor.setPhone(faker.phoneNumber().phoneNumber());
                
                List<String> locations = new ArrayList<>();
                for (int j = 0; j < 3; j++) {
                    locations.add(locationService.getRandomProvince());
                }
                vendor.setListOfLocations(locations);
                
                vendors.add(rentalVendorRepository.save(vendor));
            }
            System.out.println("✅ Dummy rental vendors generation complete.");

            // Generate RentalAddOns
            System.out.println("Generating dummy rental add-ons...");
            String[] addOnNames = {
                "GPS Navigation",
                "Baby Seat",
                "Child Booster Seat",
                "Extra Insurance",
                "Professional Driver",
                "WiFi Hotspot",
                "Roof Rack",
                "Snow Chains",
                "Extra Luggage Space",
                "Phone Charger"
            };
            
            for (String name : addOnNames) {
                RentalAddOn addOn = new RentalAddOn();
                addOn.setName(name);
                addOn.setPrice(faker.number().randomDouble(2, 50000, 500000));
                rentalAddOnRepository.save(addOn);
            }
            System.out.println("✅ Dummy rental add-ons generation complete.");

            // Generate Vehicles
            System.out.println("Generating dummy vehicles...");
            String[] vehicleTypes = {"SUV", "MPV", "Luxury", "Economy", "Sport"};
            String[] brands = {"Toyota", "Honda", "BMW", "Rolls-Royce", "Mercedes-Benz", "Nissan", "Mitsubishi", "Daihatsu"};
            String[] models = {"Avanza", "Innova", "Fortuner", "Rush", "Veloz", "Raize", "Phantom", "Ghost", "Maybach", "C-Class", "E-Class", "GLC", "A4", "A6", "Q5", "Civic", "Accord", "CR-V", "Pajero", "Outlander"};
            String[] transmissions = {"Manual", "Automatic"};
            String[] fuelTypes = {"Petrol", "Diesel", "Hybrid", "Electric"};

            int vehicleCount = 5;
            List<Vehicle> vehicles = new ArrayList<>();
            for (int i = 0; i < vehicleCount; i++) {
                Vehicle vehicle = new Vehicle();
                vehicle.setId("VEH" + String.format("%04d", i + 1));
                vehicle.setType(vehicleTypes[faker.random().nextInt(vehicleTypes.length)]);
                vehicle.setBrand(brands[faker.random().nextInt(brands.length)]);
                vehicle.setModel(models[faker.random().nextInt(models.length)]);
                vehicle.setYear(faker.random().nextInt(2024 - 2015) + 2015);
                vehicle.setLocation(locationService.getRandomProvince());
                vehicle.setLicensePlate(faker.bothify("?? #### ??"));
                vehicle.setCapacity(faker.random().nextInt(7) + 1);
                vehicle.setTransmission(transmissions[faker.random().nextInt(transmissions.length)]);
                vehicle.setFuelType(fuelTypes[faker.random().nextInt(fuelTypes.length)]);
                vehicle.setPrice((double) (faker.random().nextInt(2000) + 500) * 1000);
                vehicle.setStatus("Available");
                vehicle.setRentalVendor(vendors.get(faker.random().nextInt(vendors.size())));
                
                vehicles.add(vehicleRepository.save(vehicle));
            }
            System.out.println("✅ Dummy vehicles generation complete.");

            // Generate RentalBookings
            System.out.println("Generating dummy rental bookings...");
            String[] bookingStatuses = {"Upcoming", "Ongoing", "Done", "Cancelled"};

            // Booking 1: Upcoming
            RentalBooking booking1 = new RentalBooking();
            booking1.setId("VR00001");
            booking1.setVehicle(vehicles.get(0));
            booking1.setPickUpTime(LocalDateTime.now().plusDays(5).withHour(9).withMinute(0));
            booking1.setDropOffTime(LocalDateTime.now().plusDays(7).withHour(17).withMinute(0));
            booking1.setPickUpLocation(locationService.getRandomProvince());
            booking1.setDropOffLocation(locationService.getRandomProvince());
            booking1.setCapacityNeeded(2);
            booking1.setTransmissionNeeded("Automatic");
            booking1.setTotalPrice(4650000.0);
            booking1.setIncludeDriver(false);
            booking1.setStatus("Upcoming");
            rentalBookingRepository.save(booking1);

            // Booking 2: Ongoing
            RentalBooking booking2 = new RentalBooking();
            booking2.setId("VR00002");
            booking2.setVehicle(vehicles.get(1));
            booking2.setPickUpTime(LocalDateTime.now().minusDays(2).withHour(11).withMinute(5));
            booking2.setDropOffTime(LocalDateTime.now().plusDays(1).withHour(11).withMinute(5));
            booking2.setPickUpLocation(locationService.getRandomProvince());
            booking2.setDropOffLocation(locationService.getRandomProvince());
            booking2.setCapacityNeeded(4);
            booking2.setTransmissionNeeded("Automatic");
            booking2.setTotalPrice(1125000.0);
            booking2.setIncludeDriver(true);
            booking2.setStatus("Ongoing");
            rentalBookingRepository.save(booking2);

            // Booking 3: Done
            RentalBooking booking3 = new RentalBooking();
            booking3.setId("VR00003");
            booking3.setVehicle(vehicles.get(2));
            booking3.setPickUpTime(LocalDateTime.now().minusDays(10).withHour(11).withMinute(5));
            booking3.setDropOffTime(LocalDateTime.now().minusDays(8).withHour(17).withMinute(0));
            booking3.setPickUpLocation(locationService.getRandomProvince());
            booking3.setDropOffLocation(locationService.getRandomProvince());
            booking3.setCapacityNeeded(3);
            booking3.setTransmissionNeeded("Manual");
            booking3.setTotalPrice(910000.0);
            booking3.setIncludeDriver(false);
            booking3.setStatus("Done");
            rentalBookingRepository.save(booking3);

            System.out.println("✅ Dummy rental bookings generation complete.");
            System.out.println("   - Booking VR00001 (Upcoming): " + booking1.getVehicle().getBrand() + " " + booking1.getVehicle().getModel());
            System.out.println("   - Booking VR00002 (Ongoing): " + booking2.getVehicle().getBrand() + " " + booking2.getVehicle().getModel());
            System.out.println("   - Booking VR00003 (Done): " + booking3.getVehicle().getBrand() + " " + booking3.getVehicle().getModel());
            
            System.out.println("\nAll dummy data generation complete.");
        };
    }
}
