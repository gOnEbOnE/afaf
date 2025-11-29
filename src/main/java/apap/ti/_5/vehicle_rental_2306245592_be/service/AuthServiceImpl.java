package apap.ti._5.vehicle_rental_2306245592_be.service;

import apap.ti._5.vehicle_rental_2306245592_be.restdto.response.auth.AuthResponseDTO;
import apap.ti._5.vehicle_rental_2306245592_be.restdto.response.auth.AuthUserDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import lombok.RequiredArgsConstructor;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    
    private static final String AUTH_API_URL = "https://acc-be.beel.my.id/api/auth/me";
    private final RestTemplate restTemplate;
    
    @Override
    public AuthUserDTO getCurrentUser(String token) {
        try {
            if (token == null || token.trim().isEmpty()) {
                throw new RuntimeException("Token is empty or null");
            }
            
            log.info("Attempting to verify token with external auth service");
            
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + token);
            
            HttpEntity<String> entity = new HttpEntity<>(headers);
            
            ResponseEntity<AuthResponseDTO> response = restTemplate.exchange(
                AUTH_API_URL,
                HttpMethod.GET,
                entity,
                AuthResponseDTO.class
            );
            
            if (response.getBody() != null && response.getBody().getData() != null) {
                log.info("Token verified successfully. User role: {}", response.getBody().getData().getRole());
                return response.getBody().getData();
            }
            
            throw new RuntimeException("Failed to get user information from auth service");
        } catch (HttpClientErrorException.Unauthorized e) {
            log.error("Unauthorized: Token is invalid or expired");
            throw new RuntimeException("Invalid or expired token");
        } catch (HttpClientErrorException e) {
            log.error("HTTP Error from auth service: {}", e.getMessage());
            throw new RuntimeException("Authentication service error: " + e.getMessage());
        } catch (Exception e) {
            log.error("Authentication failed: {}", e.getMessage());
            throw new RuntimeException("Authentication failed: " + e.getMessage());
        }
    }
    
    @Override
    public boolean isSuperadmin(String token) {
        try {
            AuthUserDTO user = getCurrentUser(token);
            return "Superadmin".equalsIgnoreCase(user.getRole());
        } catch (Exception e) {
            log.error("Error checking superadmin role: {}", e.getMessage());
            return false;
        }
    }
    
    @Override
    public boolean isCustomer(String token) {
        try {
            AuthUserDTO user = getCurrentUser(token);
            return "Customer".equalsIgnoreCase(user.getRole());
        } catch (Exception e) {
            log.error("Error checking customer role: {}", e.getMessage());
            return false;
        }
    }
    
    @Override
    public boolean isRentalVendor(String token) {
        try {
            AuthUserDTO user = getCurrentUser(token);
            return "RentalVendor".equalsIgnoreCase(user.getRole());
        } catch (Exception e) {
            log.error("Error checking vendor role: {}", e.getMessage());
            return false;
        }
    }
    
    @Override
    public boolean hasVehicleAccess(String token) {
        return isSuperadmin(token) || isRentalVendor(token);
    }
    
    @Override
    public boolean hasBookingReadAccess(String token) {
        return isSuperadmin(token) || isRentalVendor(token) || isCustomer(token);
    }
    
    @Override
    public boolean hasBookingCreateAccess(String token) {
        // Hanya Superadmin dan Customer, BUKAN Rental Vendor
        return isSuperadmin(token) || isCustomer(token);
    }
}