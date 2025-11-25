package apap.ti._5.vehicle_rental_2306245592_be.service;

import apap.ti._5.vehicle_rental_2306245592_be.model.Coupon;
import apap.ti._5.vehicle_rental_2306245592_be.model.CustomerLoyalty;
import apap.ti._5.vehicle_rental_2306245592_be.model.PurchasedCoupon;
import apap.ti._5.vehicle_rental_2306245592_be.repository.CouponRepository;
import apap.ti._5.vehicle_rental_2306245592_be.repository.CustomerLoyaltyRepository;
import apap.ti._5.vehicle_rental_2306245592_be.repository.PurchasedCouponRepository;
import apap.ti._5.vehicle_rental_2306245592_be.restdto.request.loyalty.CreateCouponRequestDTO;
import apap.ti._5.vehicle_rental_2306245592_be.restdto.request.loyalty.UpdateCouponRequestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class LoyaltyServiceImpl implements LoyaltyService {
    
    @Autowired
    private CouponRepository couponRepository;
    
    @Autowired
    private CustomerLoyaltyRepository customerLoyaltyRepository;
    
    @Autowired
    private PurchasedCouponRepository purchasedCouponRepository;
    
    @Override
    public List<Coupon> getAllCoupons() {
        return couponRepository.findAll();
    }
    
    @Override
    public Coupon getCouponById(String id) {
        return couponRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Coupon not found with id: " + id));
    }
    
    @Override
    public Coupon createCoupon(CreateCouponRequestDTO dto) {
        Coupon coupon = new Coupon();
        coupon.setId(UUID.randomUUID().toString());
        coupon.setName(dto.getName());
        coupon.setDescription(dto.getDescription());
        coupon.setPoints(dto.getPoints());
        coupon.setPercentOff(dto.getPercentOff());
        
        return couponRepository.save(coupon);
    }
    
    @Override
    public Coupon updateCoupon(String id, UpdateCouponRequestDTO dto) {
        Coupon coupon = getCouponById(id);
        
        if (dto.getName() != null) {
            coupon.setName(dto.getName());
        }
        if (dto.getDescription() != null) {
            coupon.setDescription(dto.getDescription());
        }
        if (dto.getPoints() != null) {
            coupon.setPoints(dto.getPoints());
        }
        if (dto.getPercentOff() != null) {
            coupon.setPercentOff(dto.getPercentOff());
        }
        
        return couponRepository.save(coupon);
    }
    
    @Override
    public CustomerLoyalty getCustomerLoyalty(String customerId) {
        return customerLoyaltyRepository.findByCustomerId(customerId)
            .orElseGet(() -> {
                // Create new customer loyalty if not exists
                CustomerLoyalty newLoyalty = new CustomerLoyalty();
                newLoyalty.setCustomerId(customerId);
                newLoyalty.setLoyaltyPoints(0);
                return customerLoyaltyRepository.save(newLoyalty);
            });
    }
    
    @Override
    public List<PurchasedCoupon> getPurchasedCoupons(String customerId) {
        return purchasedCouponRepository.findByCustomerLoyaltyCustomerId(customerId);
    }
}
