package apap.ti._5.vehicle_rental_2306245592_be.service;

import apap.ti._5.vehicle_rental_2306245592_be.restdto.response.auth.AuthUserDTO;

public interface AuthService {
    AuthUserDTO getCurrentUser(String token);
    boolean isSuperadmin(String token);
    boolean isCustomer(String token);
    boolean isRentalVendor(String token);
    
    // Helper methods untuk RBAC
    boolean hasVehicleAccess(String token); // Superadmin OR Rental Vendor
    boolean hasBookingReadAccess(String token); // Superadmin OR Rental Vendor OR Customer
    boolean hasBookingCreateAccess(String token); // Superadmin OR Customer (NOT Rental Vendor)
}