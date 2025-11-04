package apap.ti._5.vehicle_rental_2306245592_be;

import apap.ti._5.vehicle_rental_2306245592_be.model.RentalAddOn;
import apap.ti._5.vehicle_rental_2306245592_be.model.RentalVendor;
import apap.ti._5.vehicle_rental_2306245592_be.repository.RentalAddOnRepository;
import apap.ti._5.vehicle_rental_2306245592_be.repository.RentalVendorRepository;
import com.github.javafaker.Faker;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@SpringBootApplication
public class VehicleRental2306245592BeApplication {

    public static void main(String[] args) {
        SpringApplication.run(VehicleRental2306245592BeApplication.class, args);
    }

    @Bean
    @Profile("!test")
    public CommandLineRunner createDummyData(
            RentalVendorRepository rentalVendorRepository,
            RentalAddOnRepository rentalAddOnRepository) {
        return args -> {
            System.out.println("Generating dummy data...");
            Faker faker = new Faker(Locale.of("id_ID"));

            // Generate RentalVendors
            System.out.println("Generating dummy rental vendors...");
            for (int i = 0; i < 10; i++) {
                RentalVendor vendor = new RentalVendor();
                vendor.setName(faker.company().name() + " Rental");
                vendor.setEmail(faker.internet().emailAddress());
                vendor.setPhone(faker.phoneNumber().phoneNumber());
                
                List<String> locations = new ArrayList<>();
                for (int j = 0; j < 3; j++) {
                    locations.add(faker.address().city());
                }
                vendor.setListOfLocations(locations);
                
                rentalVendorRepository.save(vendor);
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
            
            System.out.println("All dummy data generation complete.");
        };
    }
}
