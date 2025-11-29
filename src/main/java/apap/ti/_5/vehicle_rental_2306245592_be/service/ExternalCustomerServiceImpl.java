package apap.ti._5.vehicle_rental_2306245592_be.service;

import apap.ti._5.vehicle_rental_2306245592_be.restdto.external.CustomerProfileDTO;
import apap.ti._5.vehicle_rental_2306245592_be.restdto.external.ExternalApiResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExternalCustomerServiceImpl implements ExternalCustomerService {
    
    private final RestTemplate restTemplate;
    
    @Value("${external.api.base-url:https://acc-be.beel.my.id/api}")
    private String baseUrl;
    
    @Override
    public CustomerProfileDTO getCustomerById(String customerId) {
        try {
            String url = baseUrl + "/profile/" + customerId;
            
            ResponseEntity<ExternalApiResponseDTO<CustomerProfileDTO>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<ExternalApiResponseDTO<CustomerProfileDTO>>() {}
            );
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                ExternalApiResponseDTO<CustomerProfileDTO> apiResponse = response.getBody();
                if (apiResponse.getStatus() == 200 && apiResponse.getData() != null) {
                    log.info("Successfully fetched customer profile: {}", customerId);
                    return apiResponse.getData();
                }
            }
            
            throw new RuntimeException("Failed to fetch customer profile from external API");
        } catch (RestClientException e) {
            log.error("Error calling external customer API for customer: {}", customerId, e);
            throw new RuntimeException("Error fetching customer profile: " + e.getMessage());
        }
    }

    @Override
    public String getCustomerName(String customerId) {
        try {
            CustomerProfileDTO customer = getCustomerById(customerId);
            return customer.getName() != null ? customer.getName() : "CUST";
        } catch (Exception e) {
            log.warn("Failed to get customer name, using default: {}", e.getMessage());
            return "CUST";
        }
    }
}