package apap.ti._5.vehicle_rental_2306245592_be.controller;

import apap.ti._5.vehicle_rental_2306245592_be.model.Coupon;
import apap.ti._5.vehicle_rental_2306245592_be.model.CustomerLoyalty;
import apap.ti._5.vehicle_rental_2306245592_be.model.PurchasedCoupon;
import apap.ti._5.vehicle_rental_2306245592_be.restdto.BaseResponseDTO;
import apap.ti._5.vehicle_rental_2306245592_be.restdto.request.loyalty.CreateCouponRequestDTO;
import apap.ti._5.vehicle_rental_2306245592_be.restdto.request.loyalty.UpdateCouponRequestDTO;
import apap.ti._5.vehicle_rental_2306245592_be.restdto.response.auth.AuthUserDTO;
import apap.ti._5.vehicle_rental_2306245592_be.restdto.response.loyalty.CouponResponseDTO;
import apap.ti._5.vehicle_rental_2306245592_be.restdto.response.loyalty.CustomerLoyaltyResponseDTO;
import apap.ti._5.vehicle_rental_2306245592_be.service.AuthService;
import apap.ti._5.vehicle_rental_2306245592_be.service.LoyaltyService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/loyalty")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:8080"})
public class LoyaltyController {
    
    @Autowired
    private LoyaltyService loyaltyService;
    
    @Autowired
    private AuthService authService;
    
    // [GET] Get All Available Coupons - Superadmin, Customer
    @GetMapping("/coupons")
    public ResponseEntity<BaseResponseDTO<List<CouponResponseDTO>>> getAllCoupons(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            // Verify authentication (optional - if you want to allow public access, remove this)
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                authService.getCurrentUser(token); // Verify token is valid
            }
            
            List<Coupon> coupons = loyaltyService.getAllCoupons();
            List<CouponResponseDTO> couponDTOs = coupons.stream()
                .map(this::mapToCouponResponseDTO)
                .collect(Collectors.toList());
            
            BaseResponseDTO<List<CouponResponseDTO>> response = new BaseResponseDTO<>(
                200, 
                "Coupons retrieved successfully", 
                new Date(), 
                couponDTOs
            );
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            BaseResponseDTO<List<CouponResponseDTO>> response = new BaseResponseDTO<>(
                500,
                "Error retrieving coupons: " + e.getMessage(),
                new Date(),
                null
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    // [GET] Get All Purchased Coupons - Customer only
    @GetMapping("/coupons/purchased")
    public ResponseEntity<BaseResponseDTO<List<CouponResponseDTO>>> getPurchasedCoupons(
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7);
            AuthUserDTO currentUser = authService.getCurrentUser(token);
            
            // Validasi hanya Customer yang bisa akses
            if (!authService.isCustomer(token)) {
                BaseResponseDTO<List<CouponResponseDTO>> response = new BaseResponseDTO<>(
                    403,
                    "Forbidden: Only Customers can view purchased coupons",
                    new Date(),
                    null
                );
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }
            
            // Ambil customerId dari user yang sedang login
            String customerId = currentUser.getId();
            
            List<PurchasedCoupon> purchasedCoupons = loyaltyService.getPurchasedCoupons(customerId);
            List<CouponResponseDTO> couponDTOs = purchasedCoupons.stream()
                .map(pc -> mapToCouponResponseDTO(pc.getCoupon()))
                .collect(Collectors.toList());
            
            BaseResponseDTO<List<CouponResponseDTO>> response = new BaseResponseDTO<>(
                200,
                "Purchased coupons retrieved successfully",
                new Date(),
                couponDTOs
            );
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            BaseResponseDTO<List<CouponResponseDTO>> response = new BaseResponseDTO<>(
                401,
                "Unauthorized: " + e.getMessage(),
                new Date(),
                null
            );
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }
    
    // [GET] Get Customer Loyalty Points - Customer only
    @GetMapping("/points")
    public ResponseEntity<BaseResponseDTO<CustomerLoyaltyResponseDTO>> getCustomerLoyaltyPoints(
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7);
            AuthUserDTO currentUser = authService.getCurrentUser(token);
            
            // Validasi hanya Customer yang bisa akses
            if (!authService.isCustomer(token)) {
                BaseResponseDTO<CustomerLoyaltyResponseDTO> response = new BaseResponseDTO<>(
                    403,
                    "Forbidden: Only Customers can view loyalty points",
                    new Date(),
                    null
                );
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }
            
            // Ambil customerId dari user yang sedang login
            String customerId = currentUser.getId();
            
            CustomerLoyalty loyalty = loyaltyService.getCustomerLoyalty(customerId);
            CustomerLoyaltyResponseDTO dto = new CustomerLoyaltyResponseDTO(
                loyalty.getCustomerId(),
                loyalty.getLoyaltyPoints()
            );
            
            BaseResponseDTO<CustomerLoyaltyResponseDTO> response = new BaseResponseDTO<>(
                200,
                "Customer loyalty points retrieved successfully",
                new Date(),
                dto
            );
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            BaseResponseDTO<CustomerLoyaltyResponseDTO> response = new BaseResponseDTO<>(
                401,
                "Unauthorized: " + e.getMessage(),
                new Date(),
                null
            );
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }
    
    // [POST] Create New Coupon - Superadmin only
    @PostMapping("/coupons")
    public ResponseEntity<BaseResponseDTO<CouponResponseDTO>> createCoupon(
            @Valid @RequestBody CreateCouponRequestDTO dto,
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7);
            
            // Verify only Superadmin can create coupons
            if (!authService.isSuperadmin(token)) {
                BaseResponseDTO<CouponResponseDTO> response = new BaseResponseDTO<>(
                    403,
                    "Forbidden: Only Superadmin can create coupons",
                    new Date(),
                    null
                );
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }
            
            Coupon coupon = loyaltyService.createCoupon(dto);
            CouponResponseDTO couponDTO = mapToCouponResponseDTO(coupon);
            
            BaseResponseDTO<CouponResponseDTO> response = new BaseResponseDTO<>(
                201,
                "Coupon created successfully",
                new Date(),
                couponDTO
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            BaseResponseDTO<CouponResponseDTO> response = new BaseResponseDTO<>(
                401,
                "Unauthorized: " + e.getMessage(),
                new Date(),
                null
            );
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }
    
    // [PUT] Update Coupon Details - Superadmin only
    @PutMapping("/coupons/{id}")
    public ResponseEntity<BaseResponseDTO<CouponResponseDTO>> updateCoupon(
            @PathVariable String id,
            @Valid @RequestBody UpdateCouponRequestDTO dto,
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7);
            
            // Verify only Superadmin can update coupons
            if (!authService.isSuperadmin(token)) {
                BaseResponseDTO<CouponResponseDTO> response = new BaseResponseDTO<>(
                    403,
                    "Forbidden: Only Superadmin can update coupons",
                    new Date(),
                    null
                );
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }
            
            Coupon coupon = loyaltyService.updateCoupon(id, dto);
            CouponResponseDTO couponDTO = mapToCouponResponseDTO(coupon);
            
            BaseResponseDTO<CouponResponseDTO> response = new BaseResponseDTO<>(
                200,
                "Coupon updated successfully",
                new Date(),
                couponDTO
            );
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            BaseResponseDTO<CouponResponseDTO> response = new BaseResponseDTO<>(
                404,
                "Coupon not found: " + e.getMessage(),
                new Date(),
                null
            );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (Exception e) {
            BaseResponseDTO<CouponResponseDTO> response = new BaseResponseDTO<>(
                401,
                "Unauthorized: " + e.getMessage(),
                new Date(),
                null
            );
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }
    
    // Helper method to map Coupon to CouponResponseDTO
    private CouponResponseDTO mapToCouponResponseDTO(Coupon coupon) {
        return new CouponResponseDTO(
            coupon.getId(),
            coupon.getName(),
            coupon.getDescription(),
            coupon.getPoints(),
            coupon.getPercentOff()
        );
    }
}
