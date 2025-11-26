package apap.ti._5.vehicle_rental_2306245592_be.service;

import apap.ti._5.vehicle_rental_2306245592_be.model.Coupon;
import apap.ti._5.vehicle_rental_2306245592_be.model.LoyaltyPoints;
import apap.ti._5.vehicle_rental_2306245592_be.model.PurchasedCoupon;
import apap.ti._5.vehicle_rental_2306245592_be.restdto.request.loyalty.AddLoyaltyPointsRequestDTO;
import apap.ti._5.vehicle_rental_2306245592_be.restdto.request.loyalty.CreateCouponRequestDTO;
import apap.ti._5.vehicle_rental_2306245592_be.restdto.request.loyalty.UpdateCouponRequestDTO;
import apap.ti._5.vehicle_rental_2306245592_be.restdto.response.loyalty.LoyaltyPointsResponseDTO;
import apap.ti._5.vehicle_rental_2306245592_be.restdto.response.loyalty.PurchasedCouponResponseDTO;
import apap.ti._5.vehicle_rental_2306245592_be.restdto.response.loyalty.UseCouponResponseDTO;

import java.util.List;

public interface LoyaltyService {
    // Loyalty Points
    LoyaltyPointsResponseDTO addLoyaltyPoints(AddLoyaltyPointsRequestDTO request);
    LoyaltyPoints getCustomerLoyalty(String customerId);
    
    // Coupons
    List<Coupon> getAllCoupons();
    Coupon createCoupon(CreateCouponRequestDTO dto);
    Coupon updateCoupon(String id, UpdateCouponRequestDTO dto);
    
    // Purchased Coupons
    PurchasedCouponResponseDTO purchaseCoupon(String customerId, String couponId, String customerName);
    List<PurchasedCoupon> getPurchasedCoupons(String customerId);
    UseCouponResponseDTO useCoupon(String customerId, String couponCode);
}