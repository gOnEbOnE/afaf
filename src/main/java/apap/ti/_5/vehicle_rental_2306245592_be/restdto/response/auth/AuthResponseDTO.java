package apap.ti._5.vehicle_rental_2306245592_be.restdto.response.auth;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AuthResponseDTO {
    private int status;
    private String message;
    private String timestamp;
    private AuthUserDTO data;

    // Constructors
    public AuthResponseDTO() {}

    // Getters and Setters
    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
    
    public AuthUserDTO getData() { return data; }
    public void setData(AuthUserDTO data) { this.data = data; }
}