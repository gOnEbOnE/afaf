package apap.ti._5.vehicle_rental_2306245592_be.service;

import apap.ti._5.vehicle_rental_2306245592_be.restdto.response.auth.AuthResponseDTO;
import apap.ti._5.vehicle_rental_2306245592_be.restdto.response.auth.AuthUserDTO;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class AuthServiceImpl implements AuthService {
    
    private static final String AUTH_API_URL = "https://acc-be.beel.my.id/api/auth/me";
    private final RestTemplate restTemplate = new RestTemplate();
    
    @Override
    public AuthUserDTO getCurrentUser(String token) {
        try {
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
                return response.getBody().getData();
            }
            
            throw new RuntimeException("Failed to get user information");
        } catch (Exception e) {
            throw new RuntimeException("Authentication failed: " + e.getMessage());
        }
    }
    
    @Override
    public boolean isSuperadmin(String token) {
        AuthUserDTO user = getCurrentUser(token);
        return "Superadmin".equals(user.getRole());
    }
    
    @Override
    public boolean isCustomer(String token) {
        AuthUserDTO user = getCurrentUser(token);
        return "Customer".equals(user.getRole());
    }
}