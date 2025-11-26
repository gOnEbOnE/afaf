package apap.ti._5.vehicle_rental_2306245592_be.restdto.response.auth;

import com.fasterxml.jackson.annotation.JsonProperty;

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

    // Constructors
    public AuthUserDTO() {}

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public Boolean getGender() { return gender; }
    public void setGender(Boolean gender) { this.gender = gender; }
    
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    
    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
    
    public Long getSaldo() { return saldo; }
    public void setSaldo(Long saldo) { this.saldo = saldo; }
}