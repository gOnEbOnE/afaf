package apap.ti._5.vehicle_rental_2306245592_be.repository;

import apap.ti._5.vehicle_rental_2306245592_be.model.RentalVendor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RentalVendorRepository extends JpaRepository<RentalVendor, Integer> {
    List<RentalVendor> findAll();
    Optional<RentalVendor> findByEmail(String email);
}