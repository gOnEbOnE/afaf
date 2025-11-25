package apap.ti._5.vehicle_rental_2306245592_be.service;

import apap.ti._5.vehicle_rental_2306245592_be.model.Coupon;
import apap.ti._5.vehicle_rental_2306245592_be.model.CustomerLoyalty;
import apap.ti._5.vehicle_rental_2306245592_be.model.PurchasedCoupon;
import apap.ti._5.vehicle_rental_2306245592_be.restdto.request.loyalty.CreateCouponRequestDTO;
import apap.ti._5.vehicle_rental_2306245592_be.restdto.request.loyalty.UpdateCouponRequestDTO;

import java.util.List;

public interface LoyaltyService {
    
    // Coupon operations
    List<Coupon> getAllCoupons();
    Coupon getCouponById(String id);
    Coupon createCoupon(CreateCouponRequestDTO dto);
    Coupon updateCoupon(String id, UpdateCouponRequestDTO dto);
    
    // Customer loyalty operations
    CustomerLoyalty getCustomerLoyalty(String customerId);
    List<PurchasedCoupon> getPurchasedCoupons(String customerId);
}
