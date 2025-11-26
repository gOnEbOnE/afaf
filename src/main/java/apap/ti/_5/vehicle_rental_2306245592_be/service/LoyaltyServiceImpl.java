package apap.ti._5.vehicle_rental_2306245592_be.service;

import apap.ti._5.vehicle_rental_2306245592_be.model.Coupon;
import apap.ti._5.vehicle_rental_2306245592_be.model.LoyaltyPoints;
import apap.ti._5.vehicle_rental_2306245592_be.model.PurchasedCoupon;
import apap.ti._5.vehicle_rental_2306245592_be.repository.CouponRepository;
import apap.ti._5.vehicle_rental_2306245592_be.repository.LoyaltyPointsRepository;
import apap.ti._5.vehicle_rental_2306245592_be.repository.PurchasedCouponRepository;
import apap.ti._5.vehicle_rental_2306245592_be.restdto.external.CustomerProfileDTO;
import apap.ti._5.vehicle_rental_2306245592_be.restdto.request.loyalty.AddLoyaltyPointsRequestDTO;
import apap.ti._5.vehicle_rental_2306245592_be.restdto.request.loyalty.CreateCouponRequestDTO;
import apap.ti._5.vehicle_rental_2306245592_be.restdto.request.loyalty.UpdateCouponRequestDTO;
import apap.ti._5.vehicle_rental_2306245592_be.restdto.response.loyalty.LoyaltyPointsResponseDTO;
import apap.ti._5.vehicle_rental_2306245592_be.restdto.response.loyalty.PurchasedCouponResponseDTO;
import apap.ti._5.vehicle_rental_2306245592_be.restdto.response.loyalty.UseCouponResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class LoyaltyServiceImpl implements LoyaltyService {
    
    private final LoyaltyPointsRepository loyaltyPointsRepository;
    private final CouponRepository couponRepository;
    private final PurchasedCouponRepository purchasedCouponRepository;
    private final ExternalCustomerService externalCustomerService;
    
    @Override
    public LoyaltyPointsResponseDTO addLoyaltyPoints(AddLoyaltyPointsRequestDTO request) {
        log.info("Adding {} points to customer: {}", request.getPoints(), request.getCustomerId());
        
        // Tidak perlu validasi ke external API untuk endpoint ini
        // Asumsi customerId valid dari request
        
        // Get or create loyalty points record
        LoyaltyPoints loyaltyPoints = loyaltyPointsRepository
                .findByCustomerId(request.getCustomerId())
                .orElseGet(() -> {
                    LoyaltyPoints newLoyalty = new LoyaltyPoints();
                    newLoyalty.setCustomerId(request.getCustomerId());
                    newLoyalty.setTotalPoints(0);
                    newLoyalty.setCreatedAt(LocalDateTime.now());
                    return newLoyalty;
                });
        
        Integer previousPoints = loyaltyPoints.getTotalPoints();
        Integer newTotalPoints = previousPoints + request.getPoints();
        
        loyaltyPoints.setTotalPoints(newTotalPoints);
        loyaltyPoints.setUpdatedAt(LocalDateTime.now());
        
        loyaltyPointsRepository.save(loyaltyPoints);
        
        log.info("Successfully added loyalty points. Customer: {}, Previous: {}, Added: {}, Total: {}", 
                request.getCustomerId(), previousPoints, request.getPoints(), newTotalPoints);
        
        return new LoyaltyPointsResponseDTO(
                request.getCustomerId(),
                newTotalPoints,
                request.getPoints(),
                "Loyalty points added successfully"
        );
    }
    
    @Override
    public LoyaltyPoints getCustomerLoyalty(String customerId) {
        // Tidak perlu validasi ke external API lagi
        // Token sudah diverifikasi di controller
        
        log.info("Fetching loyalty points for customer: {}", customerId);
        
        return loyaltyPointsRepository
                .findByCustomerId(customerId)
                .orElseGet(() -> {
                    // Create new loyalty record if not exists
                    LoyaltyPoints newLoyalty = new LoyaltyPoints();
                    newLoyalty.setCustomerId(customerId);
                    newLoyalty.setTotalPoints(0);
                    newLoyalty.setCreatedAt(LocalDateTime.now());
                    newLoyalty.setUpdatedAt(LocalDateTime.now());
                    log.info("Created new loyalty record for customer: {}", customerId);
                    return loyaltyPointsRepository.save(newLoyalty);
                });
    }
    
    @Override
    public List<Coupon> getAllCoupons() {
        return couponRepository.findAll();
    }
    
    @Override
    public Coupon createCoupon(CreateCouponRequestDTO dto) {
        Coupon coupon = new Coupon();
        coupon.setName(dto.getName());
        coupon.setDescription(dto.getDescription());
        coupon.setPoints(dto.getPoints());
        coupon.setPercentOff(dto.getPercentOff());
        
        return couponRepository.save(coupon);
    }
    
    @Override
    public Coupon updateCoupon(String id, UpdateCouponRequestDTO dto) {
        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Coupon not found with ID: " + id));
        
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
    public PurchasedCouponResponseDTO purchaseCoupon(String customerId, String couponId, String customerName) {
        log.info("Customer {} ({}) attempting to purchase coupon {}", customerId, customerName, couponId);
        
        // Get coupon
        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new RuntimeException("Coupon not found with ID: " + couponId));
        
        // Get customer loyalty points
        LoyaltyPoints loyaltyPoints = loyaltyPointsRepository
                .findByCustomerId(customerId)
                .orElseThrow(() -> new RuntimeException("Customer loyalty not found. Please earn points first."));
        
        // Validasi: Check if customer has enough points
        if (loyaltyPoints.getTotalPoints() < coupon.getPoints()) {
            throw new RuntimeException("Insufficient loyalty points. Required: " + coupon.getPoints() + 
                    ", Available: " + loyaltyPoints.getTotalPoints());
        }
        
        // Deduct points
        Integer previousPoints = loyaltyPoints.getTotalPoints();
        loyaltyPoints.setTotalPoints(previousPoints - coupon.getPoints());
        loyaltyPoints.setUpdatedAt(LocalDateTime.now());
        loyaltyPointsRepository.save(loyaltyPoints);
        
        // Generate unique coupon code dengan nama customer dari token
        Long totalPurchasedCoupons = purchasedCouponRepository.count();
        String couponCode = generateUniqueCouponCode(coupon.getName(), customerName, totalPurchasedCoupons + 1);
        
        // Create purchased coupon
        PurchasedCoupon purchasedCoupon = new PurchasedCoupon();
        purchasedCoupon.setCustomerId(customerId);
        purchasedCoupon.setCoupon(coupon);
        purchasedCoupon.setCouponCode(couponCode);
        purchasedCoupon.setIsUsed(false);
        purchasedCoupon.setPurchasedAt(LocalDateTime.now());
        
        PurchasedCoupon savedCoupon = purchasedCouponRepository.save(purchasedCoupon);
        
        log.info("Coupon purchased successfully. Code: {}, Points deducted: {}, Remaining points: {}", 
                couponCode, coupon.getPoints(), loyaltyPoints.getTotalPoints());
        
        return new PurchasedCouponResponseDTO(
                savedCoupon.getId(),
                savedCoupon.getCustomerId(),
                savedCoupon.getCoupon().getId(),
                savedCoupon.getCoupon().getName(),
                savedCoupon.getCouponCode(),
                savedCoupon.getCoupon().getPercentOff(),
                savedCoupon.getIsUsed(),
                savedCoupon.getPurchasedAt(),
                loyaltyPoints.getTotalPoints()
        );
    }
    
    @Override
    public List<PurchasedCoupon> getPurchasedCoupons(String customerId) {
        // Tidak perlu validasi ke external API lagi
        // Token sudah diverifikasi di controller
        
        log.info("Fetching purchased coupons for customer: {}", customerId);
        
        return purchasedCouponRepository.findByCustomerId(customerId);
    }
    
    // @Override
    // public Integer validateAndUseCoupon(String couponCode, String customerId) {
    //     PurchasedCoupon purchasedCoupon = purchasedCouponRepository
    //             .findByCouponCode(couponCode)
    //             .orElse(null);
        
    //     // Invalid coupon code
    //     if (purchasedCoupon == null) {
    //         log.warn("Invalid coupon code: {}", couponCode);
    //         return 0;
    //     }
        
    //     // Check if coupon belongs to customer
    //     if (!purchasedCoupon.getCustomerId().equals(customerId)) {
    //         log.warn("Coupon {} does not belong to customer {}", couponCode, customerId);
    //         return 0;
    //     }
        
    //     // Check if coupon already used
    //     if (purchasedCoupon.getIsUsed()) {
    //         log.warn("Coupon {} already used", couponCode);
    //         return 0;
    //     }
        
    //     // Mark coupon as used
    //     purchasedCoupon.setIsUsed(true);
    //     purchasedCoupon.setUsedAt(LocalDateTime.now());
    //     purchasedCouponRepository.save(purchasedCoupon);
        
    //     log.info("Coupon {} used successfully by customer {}", couponCode, customerId);
    //     return purchasedCoupon.getCoupon().getPercentOff();
    // }
    
    @Override
    public UseCouponResponseDTO useCoupon(String customerId, String couponCode) {
        log.info("Customer {} attempting to use coupon: {}", customerId, couponCode);
        
        // Find purchased coupon by code
        PurchasedCoupon purchasedCoupon = purchasedCouponRepository
                .findByCouponCode(couponCode)
                .orElse(null);
        
        // Validasi: Coupon code not found
        if (purchasedCoupon == null) {
            log.warn("Coupon code not found: {}", couponCode);
            return new UseCouponResponseDTO(
                    couponCode,
                    null,
                    0,
                    false,
                    "Coupon code not found"
            );
        }
        
        // Validasi: Coupon belongs to customer
        if (!purchasedCoupon.getCustomerId().equals(customerId)) {
            log.warn("Coupon {} does not belong to customer {}", couponCode, customerId);
            return new UseCouponResponseDTO(
                    couponCode,
                    purchasedCoupon.getCoupon().getName(),
                    0,
                    false,
                    "Coupon does not belong to this customer"
            );
        }
        
        // Validasi: Coupon not already used
        if (purchasedCoupon.getIsUsed()) {
            log.warn("Coupon {} already used", couponCode);
            return new UseCouponResponseDTO(
                    couponCode,
                    purchasedCoupon.getCoupon().getName(),
                    0,
                    false,
                    "Coupon already used"
            );
        }
        
        // Mark coupon as used
        purchasedCoupon.setIsUsed(true);
        purchasedCoupon.setUsedAt(LocalDateTime.now());
        purchasedCouponRepository.save(purchasedCoupon);
        
        log.info("Coupon {} used successfully by customer {}. Percent off: {}", 
                couponCode, customerId, purchasedCoupon.getCoupon().getPercentOff());
        
        return new UseCouponResponseDTO(
                couponCode,
                purchasedCoupon.getCoupon().getName(),
                purchasedCoupon.getCoupon().getPercentOff(),
                true,
                "Coupon used successfully"
        );
    }
    
    private String generateUniqueCouponCode(String couponName, String customerName, Long count) {
        // Format: [5 char coupon name]-[5 char customer name]-[count]
        String cleanCouponName = couponName.replaceAll("[^a-zA-Z0-9]", "").toUpperCase();
        String cleanCustomerName = customerName.replaceAll("[^a-zA-Z0-9]", "").toUpperCase();
        
        // Ambil 5 karakter pertama (atau kurang jika tidak cukup)
        String couponPrefix = cleanCouponName.length() >= 5 
                ? cleanCouponName.substring(0, 5) 
                : String.format("%-5s", cleanCouponName).replace(' ', 'X');
        
        String customerPrefix = cleanCustomerName.length() >= 5 
                ? cleanCustomerName.substring(0, 5) 
                : String.format("%-5s", cleanCustomerName).replace(' ', 'X');
        
        return String.format("%s-%s-%03d", couponPrefix, customerPrefix, count);
    }
}