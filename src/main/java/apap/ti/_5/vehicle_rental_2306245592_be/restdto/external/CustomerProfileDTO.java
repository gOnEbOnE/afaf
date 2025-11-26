package apap.ti._5.vehicle_rental_2306245592_be.restdto.external;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerProfileDTO {
    private String id;
    private String username;
    private String name;
    private String email;
    private Boolean gender;
    private String role;
    private String createdAt;
    private String updatedAt;
    private Long saldo;
}