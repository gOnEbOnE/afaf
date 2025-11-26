package apap.ti._5.vehicle_rental_2306245592_be.repository;

import apap.ti._5.vehicle_rental_2306245592_be.model.LoyaltyPoints;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LoyaltyPointsRepository extends JpaRepository<LoyaltyPoints, String> {
    Optional<LoyaltyPoints> findByCustomerId(String customerId);
}