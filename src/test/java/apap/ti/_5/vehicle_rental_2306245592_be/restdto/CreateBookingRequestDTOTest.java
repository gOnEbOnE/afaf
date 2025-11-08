package apap.ti._5.vehicle_rental_2306245592_be.restdto;

import apap.ti._5.vehicle_rental_2306245592_be.restdto.request.booking.CreateBookingRequestDTO;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class CreateBookingRequestDTOTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testValidCreateBookingRequestDTO() {
        CreateBookingRequestDTO dto = CreateBookingRequestDTO.builder()
                .pickUpLocation("Jakarta")
                .dropOffLocation("Bandung")
                .pickUpTime(LocalDateTime.now().plusDays(1))
                .dropOffTime(LocalDateTime.now().plusDays(3))
                .capacityNeeded(5)
                .transmissionNeeded("Automatic")
                .includeDriver(false)
                .build();

        Set<ConstraintViolation<CreateBookingRequestDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testMissingPickUpLocation() {
        CreateBookingRequestDTO dto = CreateBookingRequestDTO.builder()
                .dropOffLocation("Bandung")
                .pickUpTime(LocalDateTime.now().plusDays(1))
                .dropOffTime(LocalDateTime.now().plusDays(3))
                .capacityNeeded(5)
                .transmissionNeeded("Automatic")
                .build();

        Set<ConstraintViolation<CreateBookingRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
    }

    @Test
    void testMissingDropOffLocation() {
        CreateBookingRequestDTO dto = CreateBookingRequestDTO.builder()
                .pickUpLocation("Jakarta")
                .pickUpTime(LocalDateTime.now().plusDays(1))
                .dropOffTime(LocalDateTime.now().plusDays(3))
                .capacityNeeded(5)
                .transmissionNeeded("Automatic")
                .build();

        Set<ConstraintViolation<CreateBookingRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
    }

    @Test
    void testMissingCapacity() {
        CreateBookingRequestDTO dto = CreateBookingRequestDTO.builder()
                .pickUpLocation("Jakarta")
                .dropOffLocation("Bandung")
                .pickUpTime(LocalDateTime.now().plusDays(1))
                .dropOffTime(LocalDateTime.now().plusDays(3))
                .transmissionNeeded("Automatic")
                .build();

        Set<ConstraintViolation<CreateBookingRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
    }

    @Test
    void testGettersAndSetters() {
        CreateBookingRequestDTO dto = new CreateBookingRequestDTO();
        LocalDateTime pickUp = LocalDateTime.now().plusDays(1);
        LocalDateTime dropOff = LocalDateTime.now().plusDays(3);

        dto.setPickUpLocation("Jakarta");
        dto.setDropOffLocation("Surabaya");
        dto.setPickUpTime(pickUp);
        dto.setDropOffTime(dropOff);
        dto.setCapacityNeeded(7);
        dto.setTransmissionNeeded("Manual");
        dto.setIncludeDriver(true);

        assertEquals("Jakarta", dto.getPickUpLocation());
        assertEquals("Surabaya", dto.getDropOffLocation());
        assertEquals(pickUp, dto.getPickUpTime());
        assertEquals(dropOff, dto.getDropOffTime());
        assertEquals(7, dto.getCapacityNeeded());
        assertEquals("Manual", dto.getTransmissionNeeded());
        assertTrue(dto.isIncludeDriver());
    }

    @Test
    void testBuilder() {
        LocalDateTime pickUp = LocalDateTime.now().plusDays(2);
        LocalDateTime dropOff = LocalDateTime.now().plusDays(5);

        CreateBookingRequestDTO dto = CreateBookingRequestDTO.builder()
                .pickUpLocation("Bali")
                .dropOffLocation("Lombok")
                .pickUpTime(pickUp)
                .dropOffTime(dropOff)
                .capacityNeeded(4)
                .transmissionNeeded("Automatic")
                .includeDriver(false)
                .build();

        assertNotNull(dto);
        assertEquals("Bali", dto.getPickUpLocation());
        assertEquals("Lombok", dto.getDropOffLocation());
        assertEquals(4, dto.getCapacityNeeded());
        assertFalse(dto.isIncludeDriver());
    }

    @Test
    void testNoArgsConstructor() {
        CreateBookingRequestDTO dto = new CreateBookingRequestDTO();
        assertNotNull(dto);
    }

    @Test
    void testAllArgsConstructor() {
        LocalDateTime pickUp = LocalDateTime.now().plusDays(1);
        LocalDateTime dropOff = LocalDateTime.now().plusDays(3);

        CreateBookingRequestDTO dto = new CreateBookingRequestDTO(
                true,
                "Jakarta",
                "Bandung",
                pickUp,
                dropOff,
                5,
                "Automatic"
        );

        assertEquals("Jakarta", dto.getPickUpLocation());
        assertEquals("Bandung", dto.getDropOffLocation());
        assertTrue(dto.isIncludeDriver());
        assertEquals(5, dto.getCapacityNeeded());
    }

    @Test
    void testIncludeDriverFlag() {
        CreateBookingRequestDTO dto = new CreateBookingRequestDTO();
        
        dto.setIncludeDriver(true);
        assertTrue(dto.isIncludeDriver());
        
        dto.setIncludeDriver(false);
        assertFalse(dto.isIncludeDriver());
    }

    @Test
    void testTransmissionOptions() {
        CreateBookingRequestDTO dto = new CreateBookingRequestDTO();
        
        dto.setTransmissionNeeded("Automatic");
        assertEquals("Automatic", dto.getTransmissionNeeded());
        
        dto.setTransmissionNeeded("Manual");
        assertEquals("Manual", dto.getTransmissionNeeded());
    }

    @Test
    void testCapacityValidation() {
        CreateBookingRequestDTO dto = CreateBookingRequestDTO.builder()
                .pickUpLocation("Jakarta")
                .dropOffLocation("Bandung")
                .pickUpTime(LocalDateTime.now().plusDays(1))
                .dropOffTime(LocalDateTime.now().plusDays(3))
                .capacityNeeded(0) // Invalid: less than 1
                .transmissionNeeded("Automatic")
                .build();

        Set<ConstraintViolation<CreateBookingRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
    }

    @Test
    void testTimeValidation() {
        CreateBookingRequestDTO dto = CreateBookingRequestDTO.builder()
                .pickUpLocation("Jakarta")
                .dropOffLocation("Bandung")
                .pickUpTime(null) // Missing
                .dropOffTime(LocalDateTime.now().plusDays(3))
                .capacityNeeded(5)
                .transmissionNeeded("Automatic")
                .build();

        Set<ConstraintViolation<CreateBookingRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
    }

    @Test
    void testEqualsAndHashCode() {
        LocalDateTime pickUp = LocalDateTime.now().plusDays(1);
        LocalDateTime dropOff = LocalDateTime.now().plusDays(3);

        CreateBookingRequestDTO dto1 = CreateBookingRequestDTO.builder()
                .pickUpLocation("Jakarta")
                .dropOffLocation("Bandung")
                .pickUpTime(pickUp)
                .dropOffTime(dropOff)
                .capacityNeeded(5)
                .transmissionNeeded("Automatic")
                .includeDriver(false)
                .build();

        CreateBookingRequestDTO dto2 = CreateBookingRequestDTO.builder()
                .pickUpLocation("Jakarta")
                .dropOffLocation("Bandung")
                .pickUpTime(pickUp)
                .dropOffTime(dropOff)
                .capacityNeeded(5)
                .transmissionNeeded("Automatic")
                .includeDriver(false)
                .build();

        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    void testToString() {
        CreateBookingRequestDTO dto = CreateBookingRequestDTO.builder()
                .pickUpLocation("Jakarta")
                .dropOffLocation("Bandung")
                .capacityNeeded(5)
                .transmissionNeeded("Automatic")
                .build();

        String toString = dto.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("Jakarta"));
        assertTrue(toString.contains("Bandung"));
    }
}
