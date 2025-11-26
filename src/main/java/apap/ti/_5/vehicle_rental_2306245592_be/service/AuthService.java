package apap.ti._5.vehicle_rental_2306245592_be.service;

import apap.ti._5.vehicle_rental_2306245592_be.restdto.response.auth.AuthUserDTO;

public interface AuthService {
    AuthUserDTO getCurrentUser(String token);
    boolean isSuperadmin(String token);
    boolean isCustomer(String token);
}