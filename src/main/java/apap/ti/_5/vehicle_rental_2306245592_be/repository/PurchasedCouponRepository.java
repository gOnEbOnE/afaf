package apap.ti._5.vehicle_rental_2306245592_be.repository;

import apap.ti._5.vehicle_rental_2306245592_be.model.PurchasedCoupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface PurchasedCouponRepository extends JpaRepository<PurchasedCoupon, String> {
    List<PurchasedCoupon> findByCustomerId(String customerId);
    Optional<PurchasedCoupon> findByCouponCode(String couponCode);
    Long countByCustomerIdAndCoupon_Id(String customerId, String couponId);
}
