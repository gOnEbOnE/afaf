package apap.ti._5.vehicle_rental_2306245592_be.controller;

import apap.ti._5.vehicle_rental_2306245592_be.model.Coupon;
import apap.ti._5.vehicle_rental_2306245592_be.model.LoyaltyPoints;
import apap.ti._5.vehicle_rental_2306245592_be.model.PurchasedCoupon;
import apap.ti._5.vehicle_rental_2306245592_be.restdto.BaseResponseDTO;
import apap.ti._5.vehicle_rental_2306245592_be.restdto.request.loyalty.AddLoyaltyPointsRequestDTO;
import apap.ti._5.vehicle_rental_2306245592_be.restdto.request.loyalty.CreateCouponRequestDTO;
import apap.ti._5.vehicle_rental_2306245592_be.restdto.request.loyalty.UpdateCouponRequestDTO;
import apap.ti._5.vehicle_rental_2306245592_be.restdto.request.loyalty.UseCouponRequestDTO;
import apap.ti._5.vehicle_rental_2306245592_be.restdto.request.loyalty.PurchaseCouponRequestDTO;
import apap.ti._5.vehicle_rental_2306245592_be.restdto.response.auth.AuthUserDTO;
import apap.ti._5.vehicle_rental_2306245592_be.restdto.response.loyalty.CouponResponseDTO;
import apap.ti._5.vehicle_rental_2306245592_be.restdto.response.loyalty.CustomerLoyaltyResponseDTO;
import apap.ti._5.vehicle_rental_2306245592_be.restdto.response.loyalty.LoyaltyPointsResponseDTO;
import apap.ti._5.vehicle_rental_2306245592_be.restdto.response.loyalty.PurchasedCouponResponseDTO;
import apap.ti._5.vehicle_rental_2306245592_be.restdto.response.loyalty.UseCouponResponseDTO;
import apap.ti._5.vehicle_rental_2306245592_be.service.AuthService;
import apap.ti._5.vehicle_rental_2306245592_be.service.LoyaltyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/loyalty")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:8080"})
@RequiredArgsConstructor
@Slf4j
public class LoyaltyController {
    
    @Autowired
    private LoyaltyService loyaltyService;
    
    @Autowired
    private AuthService authService;
    
    @Value("${api.key:default-api-key}")
    private String apiKey;
    
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
    public ResponseEntity<BaseResponseDTO<List<PurchasedCouponResponseDTO>>> getPurchasedCoupons(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            if (authHeader == null || authHeader.trim().isEmpty()) {
                // jadi PurchasedCouponResponseDTO
                BaseResponseDTO<List<PurchasedCouponResponseDTO>> response = new BaseResponseDTO<>(
                    401,
                    "Unauthorized: Authorization header is required",
                    new Date(),
                    null
                );
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            
            if (!authHeader.startsWith("Bearer ")) {
                BaseResponseDTO<List<PurchasedCouponResponseDTO>> response = new BaseResponseDTO<>(
                    401,
                    "Unauthorized: Invalid token format. Use 'Bearer <token>'",
                    new Date(),
                    null
                );
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            
            String token = authHeader.substring(7);
            AuthUserDTO currentUser = authService.getCurrentUser(token);
            
            if (!authService.isCustomer(token)) {
                BaseResponseDTO<List<PurchasedCouponResponseDTO>> response = new BaseResponseDTO<>(
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
            
            // MAPPING DATA (Ini bagian terpenting, sudah benar)
            List<PurchasedCouponResponseDTO> responseDTOs = purchasedCoupons.stream()
                .map(pc -> new PurchasedCouponResponseDTO(
                    pc.getId(),
                    pc.getCustomerId(),
                    pc.getCoupon().getId(),
                    pc.getCoupon().getName(),
                    pc.getCouponCode(), 
                    pc.getCoupon().getPercentOff(),
                    pc.getIsUsed(),
                    pc.getPurchasedAt(),
                    0 
                ))
                .collect(Collectors.toList());
            
            BaseResponseDTO<List<PurchasedCouponResponseDTO>> response = new BaseResponseDTO<>(
                200,
                "Purchased coupons retrieved successfully",
                new Date(),
                responseDTOs
            );
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error retrieving purchased coupons: {}", e.getMessage());
            
            BaseResponseDTO<List<PurchasedCouponResponseDTO>> response = new BaseResponseDTO<>(
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
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            // Validasi Authorization header ada
            if (authHeader == null || authHeader.trim().isEmpty()) {
                BaseResponseDTO<CustomerLoyaltyResponseDTO> response = new BaseResponseDTO<>(
                    401,
                    "Unauthorized: Authorization header is required",
                    new Date(),
                    null
                );
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            
            // Validasi format Bearer token
            if (!authHeader.startsWith("Bearer ")) {
                BaseResponseDTO<CustomerLoyaltyResponseDTO> response = new BaseResponseDTO<>(
                    401,
                    "Unauthorized: Invalid token format. Use 'Bearer <token>'",
                    new Date(),
                    null
                );
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            
            String token = authHeader.substring(7);
            AuthUserDTO currentUser = authService.getCurrentUser(token);
            
            if (!authService.isCustomer(token)) {
                BaseResponseDTO<CustomerLoyaltyResponseDTO> response = new BaseResponseDTO<>(
                    403,
                    "Forbidden: Only Customers can view loyalty points",
                    new Date(),
                    null
                );
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }
            
            String customerId = currentUser.getId();
            
            LoyaltyPoints loyalty = loyaltyService.getCustomerLoyalty(customerId);
            CustomerLoyaltyResponseDTO dto = new CustomerLoyaltyResponseDTO(
                loyalty.getCustomerId(),
                loyalty.getTotalPoints()
            );
            
            BaseResponseDTO<CustomerLoyaltyResponseDTO> response = new BaseResponseDTO<>(
                200,
                "Customer loyalty points retrieved successfully",
                new Date(),
                dto
            );
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error retrieving loyalty points: {}", e.getMessage());
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
    
    // [POST] Add Loyalty Points - Public endpoint (no auth required)
    @PostMapping("/points/add")
    public ResponseEntity<BaseResponseDTO<LoyaltyPointsResponseDTO>> addLoyaltyPoints(
            @Valid @RequestBody AddLoyaltyPointsRequestDTO request) {
        try {
            log.info("Request to add {} points to customer: {}", request.getPoints(), request.getCustomerId());
            
            LoyaltyPointsResponseDTO response = loyaltyService.addLoyaltyPoints(request);
            
            return ResponseEntity.ok(new BaseResponseDTO<>(
                    200,
                    "Loyalty points added successfully",
                    new Date(),
                    response
            ));
        } catch (RuntimeException e) {
            log.error("Error adding loyalty points: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new BaseResponseDTO<>(
                            HttpStatus.NOT_FOUND.value(),
                            e.getMessage(),
                            new Date(),
                            null
                    ));
        } catch (Exception e) {
            log.error("Unexpected error adding loyalty points: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponseDTO<>(
                            HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            "Error adding loyalty points: " + e.getMessage(),
                            new Date(),
                            null
                    ));
        }
    }
    
    // [POST] Use/Redeem Coupon
    @PostMapping("/coupons/use")
    public ResponseEntity<BaseResponseDTO<UseCouponResponseDTO>> useCoupon(
            @Valid @RequestBody UseCouponRequestDTO request) {
        try {
            log.info("Request to use coupon. Customer: {}, Code: {}", request.getCustomerId(), request.getCouponCode());
            
            UseCouponResponseDTO response = loyaltyService.useCoupon(request.getCustomerId(), request.getCouponCode());
            
            // Return 200 OK untuk kedua kasus (valid dan invalid)
            return ResponseEntity.ok(new BaseResponseDTO<>(
                    200,
                    response.getIsValid() ? "Coupon used successfully" : "Coupon validation failed",
                    new Date(),
                    response
            ));
        } catch (Exception e) {
            log.error("Unexpected error using coupon: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponseDTO<>(
                            HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            "Error using coupon: " + e.getMessage(),
                            new Date(),
                            null
                    ));
        }
    }
    
    // [POST] Purchase Coupon - Customer only
    @PostMapping("/coupons/purchase")
    public ResponseEntity<BaseResponseDTO<PurchasedCouponResponseDTO>> purchaseCoupon(
            @Valid @RequestBody PurchaseCouponRequestDTO request,
            @RequestHeader("Authorization") String authHeader) {
        try {
            // Validasi Authorization header
            if (authHeader == null || authHeader.trim().isEmpty() || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new BaseResponseDTO<>(
                                HttpStatus.UNAUTHORIZED.value(),
                                "Unauthorized: Invalid or missing Authorization header",
                                new Date(),
                                null
                        ));
            }
            
            String token = authHeader.substring(7);
            AuthUserDTO currentUser = authService.getCurrentUser(token);
            
            // Validasi hanya Customer yang bisa purchase coupon
            if (!authService.isCustomer(token)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new BaseResponseDTO<>(
                                HttpStatus.FORBIDDEN.value(),
                                "Forbidden: Only Customers can purchase coupons",
                                new Date(),
                                null
                        ));
            }
            
            String customerId = currentUser.getId();
            String customerName = currentUser.getName(); // Ambil dari token
            
            log.info("Customer {} ({}) requesting to purchase coupon {}", customerId, customerName, request.getCouponId());
            
            // Pass customerName ke service
            PurchasedCouponResponseDTO response = loyaltyService.purchaseCoupon(
                    customerId, 
                    request.getCouponId(),
                    customerName
            );
            
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new BaseResponseDTO<>(
                            HttpStatus.CREATED.value(),
                            "Coupon purchased successfully",
                            new Date(),
                            response
                    ));
        } catch (RuntimeException e) {
            log.error("Error purchasing coupon: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new BaseResponseDTO<>(
                            HttpStatus.BAD_REQUEST.value(),
                            e.getMessage(),
                            new Date(),
                            null
                    ));
        } catch (Exception e) {
            log.error("Unexpected error purchasing coupon: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponseDTO<>(
                            HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            "Error purchasing coupon: " + e.getMessage(),
                            new Date(),
                            null
                    ));
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
