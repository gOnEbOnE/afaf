package apap.ti._5.vehicle_rental_2306245592_be.restdto.external;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExternalApiResponseDTO<T> {
    private Integer status;
    private String message;
    private String timestamp;
    private T data;
}