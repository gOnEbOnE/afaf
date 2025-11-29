package apap.ti._5.vehicle_rental_2306245592_be.repository;

import apap.ti._5.vehicle_rental_2306245592_be.model.CustomerLoyalty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerLoyaltyRepository extends JpaRepository<CustomerLoyalty, Long> {
    Optional<CustomerLoyalty> findByCustomerId(String customerId);
}
