package apap.ti._5.vehicle_rental_2306245592_be.service;

import apap.ti._5.vehicle_rental_2306245592_be.restdto.external.CustomerProfileDTO;
import apap.ti._5.vehicle_rental_2306245592_be.restdto.external.ExternalApiResponseDTO;

public interface ExternalCustomerService {
    CustomerProfileDTO getCustomerById(String customerId);
    String getCustomerName(String customerId);
}