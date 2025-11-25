package apap.ti._5.vehicle_rental_2306245592_be.repository;

import apap.ti._5.vehicle_rental_2306245592_be.model.PurchasedCoupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PurchasedCouponRepository extends JpaRepository<PurchasedCoupon, Long> {
    // Method ini mengikuti path relasi: PurchasedCoupon -> CustomerLoyalty -> customerId
    List<PurchasedCoupon> findByCustomerLoyaltyCustomerId(String customerId);
}
