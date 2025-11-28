package apap.ti._5.vehicle_rental_2306245592_be.restdto.response.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data               // Membuat Getter, Setter, toString, equals, hashCode
@NoArgsConstructor  // Membuat Constructor kosong (Wajib untuk Jackson/JSON)
@AllArgsConstructor // Membuat Constructor dengan semua argumen (Opsional, tapi berguna)
public class AuthUserDTO {
    private String id;
    private String username;
    private String name;
    private String email;
    private Boolean gender;
    private String role;
    
    @JsonProperty("createdAt")
    private String createdAt;
    
    @JsonProperty("updatedAt")
    private String updatedAt;
    
    private Long saldo;

}