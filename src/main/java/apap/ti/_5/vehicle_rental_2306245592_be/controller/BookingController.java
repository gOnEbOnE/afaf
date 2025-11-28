package apap.ti._5.vehicle_rental_2306245592_be.controller;

import apap.ti._5.vehicle_rental_2306245592_be.model.RentalAddOn;
import apap.ti._5.vehicle_rental_2306245592_be.model.RentalBooking;
import apap.ti._5.vehicle_rental_2306245592_be.restdto.BaseResponseDTO;
import apap.ti._5.vehicle_rental_2306245592_be.restdto.request.booking.*;
import apap.ti._5.vehicle_rental_2306245592_be.restdto.response.booking.*;
import apap.ti._5.vehicle_rental_2306245592_be.restdto.response.RentalAddOn.RentalAddOnResponseDTO;
import apap.ti._5.vehicle_rental_2306245592_be.service.AuthService;
import apap.ti._5.vehicle_rental_2306245592_be.service.BookingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/bookings")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:8080"})
@RequiredArgsConstructor
@Slf4j
public class BookingController {

    private final BookingService bookingService;
    private final AuthService authService;

    private RentalBookingResponseDTO convertToDTO(RentalBooking booking) {
        List<RentalAddOnResponseDTO> addOnDTOs = booking.getListOfAddOns() != null ?
            booking.getListOfAddOns().stream()
                .map(addOn -> new RentalAddOnResponseDTO(
                    addOn.getId(),
                    addOn.getName(),
                    addOn.getPrice(),
                    addOn.getCreatedAt(),
                    addOn.getUpdatedAt()
                ))
                .collect(Collectors.toList()) : null;

        return new RentalBookingResponseDTO(
            booking.getId(),
            booking.getVehicle().getId(),
            booking.getVehicle().getBrand(),
            booking.getVehicle().getModel(),
            booking.getPickUpTime(),
            booking.getDropOffTime(),
            booking.getPickUpLocation(),
            booking.getDropOffLocation(),
            booking.getCapacityNeeded(),
            booking.getTransmissionNeeded(),
            booking.getTotalPrice(),
            booking.getIncludeDriver(),
            booking.getStatus(),
            addOnDTOs,
            booking.getCreatedAt(),
            booking.getUpdatedAt()
        );
    }

    // ============ PBI-BE-V7: GET ALL BOOKINGS ============
    @GetMapping
    public ResponseEntity<BaseResponseDTO<List<RentalBookingResponseDTO>>> getAllBookings(
            @RequestParam(required = false) String status,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        
        try {
            // RBAC: Superadmin, Rental Vendor, Customer
            if (authHeader == null || authHeader.trim().isEmpty() || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new BaseResponseDTO<>(
                                HttpStatus.UNAUTHORIZED.value(),
                                "Unauthorized: Missing or invalid Authorization header",
                                new Date(),
                                null
                        ));
            }
            
            String token = authHeader.substring(7);
            
            if (!authService.hasBookingReadAccess(token)) {
                log.warn("Unauthorized booking access attempt");
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new BaseResponseDTO<>(
                                HttpStatus.FORBIDDEN.value(),
                                "Forbidden: You don't have permission to view bookings",
                                new Date(),
                                null
                        ));
            }
            
            List<RentalBooking> bookings;
            
            if (status != null && !status.isEmpty()) {
                bookings = bookingService.getBookingsByStatus(status);
            } else {
                bookings = bookingService.getAllBookings();
            }
            
            List<RentalBookingResponseDTO> response = bookings.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
            
            BaseResponseDTO<List<RentalBookingResponseDTO>> apiResponse = new BaseResponseDTO<>(
                200, "Bookings retrieved successfully (Total: " + bookings.size() + ")", new Date(), response
            );
            return ResponseEntity.ok(apiResponse);
        } catch (Exception e) {
            log.error("Error retrieving bookings: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponseDTO<>(
                            HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            "Error retrieving bookings: " + e.getMessage(),
                            new Date(),
                            null
                    ));
        }
    }

    @GetMapping("/count")
    public ResponseEntity<BaseResponseDTO<Integer>> getBookingCount(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        
        try {
            if (authHeader == null || authHeader.trim().isEmpty() || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new BaseResponseDTO<>(
                                HttpStatus.UNAUTHORIZED.value(),
                                "Unauthorized: Missing or invalid Authorization header",
                                new Date(),
                                null
                        ));
            }
            
            String token = authHeader.substring(7);
            
            if (!authService.hasBookingReadAccess(token)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new BaseResponseDTO<>(
                                HttpStatus.FORBIDDEN.value(),
                                "Forbidden",
                                new Date(),
                                null
                        ));
            }
            
            int count = bookingService.getAllBookings().size();
            
            BaseResponseDTO<Integer> response = new BaseResponseDTO<>(
                200, "Success", new Date(), count
            );
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponseDTO<>(
                            HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            e.getMessage(),
                            new Date(),
                            null
                    ));
        }
    }

    // ============ PBI-BE-V8: GET BOOKING BY ID ============
    @GetMapping("/{id}")
    public ResponseEntity<BaseResponseDTO<RentalBookingResponseDTO>> getBookingById(
            @PathVariable String id,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        
        try {
            if (authHeader == null || authHeader.trim().isEmpty() || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new BaseResponseDTO<>(
                                HttpStatus.UNAUTHORIZED.value(),
                                "Unauthorized: Missing or invalid Authorization header",
                                new Date(),
                                null
                        ));
            }
            
            String token = authHeader.substring(7);
            
            if (!authService.hasBookingReadAccess(token)) {
                log.warn("Unauthorized booking access attempt for ID: {}", id);
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new BaseResponseDTO<>(
                                HttpStatus.FORBIDDEN.value(),
                                "Forbidden: You don't have permission to view booking details",
                                new Date(),
                                null
                        ));
            }
            
            Optional<RentalBooking> booking = bookingService.getBookingById(id);
            
            if (booking.isPresent()) {
                RentalBookingResponseDTO dto = convertToDTO(booking.get());
                BaseResponseDTO<RentalBookingResponseDTO> response = new BaseResponseDTO<>(
                    200, "Success", new Date(), dto
                );
                return ResponseEntity.ok(response);
            }
            
            BaseResponseDTO<RentalBookingResponseDTO> response = new BaseResponseDTO<>(
                404, "Booking not found", new Date(), null
            );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (Exception e) {
            log.error("Error retrieving booking: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponseDTO<>(
                            HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            "Error retrieving booking: " + e.getMessage(),
                            new Date(),
                            null
                    ));
        }
    }

    @PostMapping
    public ResponseEntity<BaseResponseDTO<RentalBookingResponseDTO>> createBooking(
            @RequestBody RentalBooking booking,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        
        try {
            if (authHeader == null || authHeader.trim().isEmpty() || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new BaseResponseDTO<>(
                                HttpStatus.UNAUTHORIZED.value(),
                                "Unauthorized: Missing or invalid Authorization header",
                                new Date(),
                                null
                        ));
            }
            
            String token = authHeader.substring(7);
            
            // PBI-BE-V10: Only Superadmin and Customer
            if (!authService.hasBookingCreateAccess(token)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new BaseResponseDTO<>(
                                HttpStatus.FORBIDDEN.value(),
                                "Forbidden: Only Superadmin and Customer can create bookings",
                                new Date(),
                                null
                        ));
            }
            
            RentalBooking createdBooking = bookingService.createBooking(booking);
            RentalBookingResponseDTO dto = convertToDTO(createdBooking);
            BaseResponseDTO<RentalBookingResponseDTO> response = new BaseResponseDTO<>(
                201, "Booking created successfully", new Date(), dto
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new BaseResponseDTO<>(
                            HttpStatus.BAD_REQUEST.value(),
                            e.getMessage(),
                            new Date(),
                            null
                    ));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<BaseResponseDTO<RentalBookingResponseDTO>> updateBooking(
            @PathVariable String id, 
            @RequestBody RentalBooking booking,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        
        try {
            if (authHeader == null || authHeader.trim().isEmpty() || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new BaseResponseDTO<>(
                                HttpStatus.UNAUTHORIZED.value(),
                                "Unauthorized: Missing or invalid Authorization header",
                                new Date(),
                                null
                        ));
            }
            
            String token = authHeader.substring(7);
            
            if (!authService.hasBookingCreateAccess(token)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new BaseResponseDTO<>(
                                HttpStatus.FORBIDDEN.value(),
                                "Forbidden",
                                new Date(),
                                null
                        ));
            }
            
            RentalBooking updatedBooking = bookingService.updateBooking(id, booking);
            RentalBookingResponseDTO dto = convertToDTO(updatedBooking);
            BaseResponseDTO<RentalBookingResponseDTO> response = new BaseResponseDTO<>(
                200, "Booking updated successfully", new Date(), dto
            );
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            BaseResponseDTO<RentalBookingResponseDTO> response = new BaseResponseDTO<>(
                404, e.getMessage(), new Date(), null
            );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponseDTO<Void>> deleteBooking(
            @PathVariable String id,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        
        try {
            if (authHeader == null || authHeader.trim().isEmpty() || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new BaseResponseDTO<>(
                                HttpStatus.UNAUTHORIZED.value(),
                                "Unauthorized: Missing or invalid Authorization header",
                                new Date(),
                                null
                        ));
            }
            
            String token = authHeader.substring(7);
            
            if (!authService.hasBookingCreateAccess(token)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new BaseResponseDTO<>(
                                HttpStatus.FORBIDDEN.value(),
                                "Forbidden",
                                new Date(),
                                null
                        ));
            }
            
            bookingService.deleteBooking(id);
            BaseResponseDTO<Void> response = new BaseResponseDTO<>(
                200, "Booking deleted successfully", new Date(), null
            );
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            BaseResponseDTO<Void> response = new BaseResponseDTO<>(
                404, e.getMessage(), new Date(), null
            );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    // ============ PBI-BE-V9: SEARCH AVAILABLE VEHICLES ============
    @PostMapping("/search")
    public ResponseEntity<BaseResponseDTO<SearchVehiclesResponseDTO>> searchAvailableVehicles(
            @RequestBody CreateBookingRequestDTO criteria,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        
        try {
            if (authHeader == null || authHeader.trim().isEmpty() || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new BaseResponseDTO<>(
                                HttpStatus.UNAUTHORIZED.value(),
                                "Unauthorized: Missing or invalid Authorization header",
                                new Date(),
                                null
                        ));
            }
            
            String token = authHeader.substring(7);
            
            // RBAC: Superadmin, Rental Vendor, Customer
            if (!authService.hasBookingReadAccess(token)) {
                log.warn("Unauthorized search attempt");
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new BaseResponseDTO<>(
                                HttpStatus.FORBIDDEN.value(),
                                "Forbidden: You don't have permission to search vehicles",
                                new Date(),
                                null
                        ));
            }
            
            System.out.println("🔍 [API] Search request received");
            SearchVehiclesResponseDTO result = bookingService.searchAvailableVehicles(criteria);
            
            String message = result.getAvailableVehicles().isEmpty() 
                ? "Tidak ada kendaraan yang tersedia sesuai dengan kriteria pencarian" 
                : "Vehicles found: " + result.getAvailableVehicles().size();
            
            BaseResponseDTO<SearchVehiclesResponseDTO> response = new BaseResponseDTO<>(
                200, message, new Date(), result
            );
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            System.err.println("❌ [API] Search error: " + e.getMessage());
            BaseResponseDTO<SearchVehiclesResponseDTO> response = new BaseResponseDTO<>(
                400, e.getMessage(), new Date(), null
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            System.err.println("❌ [API] Unexpected error: " + e.getMessage());
            e.printStackTrace();
            BaseResponseDTO<SearchVehiclesResponseDTO> response = new BaseResponseDTO<>(
                500, "Terjadi kesalahan pada server", new Date(), null
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // ============ PBI-BE-V10: FINALIZE BOOKING (CREATE) ============
    @PostMapping("/finalize")
    public ResponseEntity<BaseResponseDTO<RentalBookingResponseDTO>> finalizeBooking(
            @RequestBody FinalizBookingRequestDTO request,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        
        try {
            if (authHeader == null || authHeader.trim().isEmpty() || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new BaseResponseDTO<>(
                                HttpStatus.UNAUTHORIZED.value(),
                                "Unauthorized: Missing or invalid Authorization header",
                                new Date(),
                                null
                        ));
            }
            
            String token = authHeader.substring(7);
            
            // RBAC: PBI-BE-V10 - HANYA Superadmin dan Customer (BUKAN Rental Vendor)
            if (!authService.hasBookingCreateAccess(token)) {
                log.warn("Unauthorized booking creation attempt");
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new BaseResponseDTO<>(
                                HttpStatus.FORBIDDEN.value(),
                                "Forbidden: Only Superadmin and Customer can create bookings",
                                new Date(),
                                null
                        ));
            }
            
            System.out.println("📝 [API] Finalize booking request received");
            
            if (request.getBookingDTO() == null || request.getAddOnsDTO() == null) {
                throw new RuntimeException("Data booking atau add-ons tidak boleh kosong");
            }
            
            RentalBooking createdBooking = bookingService.createBookingWithAddOns(
                request.getBookingDTO(), 
                request.getAddOnsDTO()
            );
            
            RentalBookingResponseDTO dto = convertToDTO(createdBooking);
            BaseResponseDTO<RentalBookingResponseDTO> response = new BaseResponseDTO<>(
                201, "Booking berhasil dibuat: " + createdBooking.getId(), new Date(), dto
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            System.err.println("❌ [API] Finalize error: " + e.getMessage());
            BaseResponseDTO<RentalBookingResponseDTO> response = new BaseResponseDTO<>(
                400, e.getMessage(), new Date(), null
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            System.err.println("❌ [API] Unexpected error: " + e.getMessage());
            e.printStackTrace();
            BaseResponseDTO<RentalBookingResponseDTO> response = new BaseResponseDTO<>(
                500, "Terjadi kesalahan pada server", new Date(), null
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // ============ EXISTING METHODS - Keep all as is, just add RBAC ============

    @GetMapping("/addons")
    public ResponseEntity<BaseResponseDTO<List<RentalAddOnResponseDTO>>> getAllAddOns(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        
        try {
            if (authHeader == null || authHeader.trim().isEmpty() || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new BaseResponseDTO<>(
                                HttpStatus.UNAUTHORIZED.value(),
                                "Unauthorized",
                                new Date(),
                                null
                        ));
            }
            
            String token = authHeader.substring(7);
            if (!authService.hasBookingReadAccess(token)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new BaseResponseDTO<>(
                                HttpStatus.FORBIDDEN.value(),
                                "Forbidden",
                                new Date(),
                                null
                        ));
            }
            
            List<RentalAddOn> addOns = bookingService.getAllAddOns();
            List<RentalAddOnResponseDTO> addOnDTOs = addOns.stream()
                .map(addOn -> new RentalAddOnResponseDTO(
                    addOn.getId(),
                    addOn.getName(),
                    addOn.getPrice(),
                    addOn.getCreatedAt(),
                    addOn.getUpdatedAt()
                ))
                .collect(Collectors.toList());
            
            BaseResponseDTO<List<RentalAddOnResponseDTO>> response = new BaseResponseDTO<>(
                200, "Add-ons retrieved successfully", new Date(), addOnDTOs
            );
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponseDTO<>(
                            HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            e.getMessage(),
                            new Date(),
                            null
                    ));
        }
    }

    @GetMapping("/provinces")
    public ResponseEntity<BaseResponseDTO<List<String>>> getAllProvinces() {
        // Public endpoint - no auth needed
        List<String> provinces = bookingService.getAllProvinces();
        BaseResponseDTO<List<String>> response = new BaseResponseDTO<>(
            200, "Provinces retrieved successfully", new Date(), provinces
        );
        return ResponseEntity.ok(response);
    }

    // Keep all other existing methods (update-details, update-status, update-addons, delete, chart, current-time)
    // Just add RBAC validation at the start of each method following the same pattern

    @GetMapping("/{id}/update-details")
    public ResponseEntity<BaseResponseDTO<RentalBookingResponseDTO>> getBookingForUpdate(
            @PathVariable String id,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            if (authHeader == null || authHeader.trim().isEmpty() || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new BaseResponseDTO<>(HttpStatus.UNAUTHORIZED.value(), "Unauthorized", new Date(), null));
            }
            
            String token = authHeader.substring(7);
            if (!authService.hasBookingCreateAccess(token)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new BaseResponseDTO<>(HttpStatus.FORBIDDEN.value(), "Forbidden", new Date(), null));
            }
            
            System.out.println("🔍 [API] GET /bookings/" + id + "/update-details");
            Optional<RentalBooking> booking = bookingService.getBookingForUpdate(id);
            
            if (booking.isPresent()) {
                RentalBookingResponseDTO dto = convertToDTO(booking.get());
                BaseResponseDTO<RentalBookingResponseDTO> response = new BaseResponseDTO<>(
                    200, "Booking details retrieved for update", new Date(), dto
                );
                return ResponseEntity.ok(response);
            }
            
            BaseResponseDTO<RentalBookingResponseDTO> response = new BaseResponseDTO<>(
                404, "Booking not found", new Date(), null
            );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (RuntimeException e) {
            System.err.println("❌ [API] Get booking for update error: " + e.getMessage());
            BaseResponseDTO<RentalBookingResponseDTO> response = new BaseResponseDTO<>(
                400, e.getMessage(), new Date(), null
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @PutMapping("/update-details")
    public ResponseEntity<BaseResponseDTO<RentalBookingResponseDTO>> updateBookingDetails(
            @RequestBody UpdateBookingRequestDTO updateDTO,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            if (authHeader == null || authHeader.trim().isEmpty() || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new BaseResponseDTO<>(HttpStatus.UNAUTHORIZED.value(), "Unauthorized", new Date(), null));
            }
            
            String token = authHeader.substring(7);
            if (!authService.hasBookingCreateAccess(token)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new BaseResponseDTO<>(HttpStatus.FORBIDDEN.value(), "Forbidden", new Date(), null));
            }
            
            System.out.println("📝 [API] PUT /bookings/update-details");
            System.out.println("   Booking ID: " + updateDTO.getId());
            
            RentalBooking updatedBooking = bookingService.updateBookingDetails(updateDTO.getId(), updateDTO);
            RentalBookingResponseDTO dto = convertToDTO(updatedBooking);
            
            BaseResponseDTO<RentalBookingResponseDTO> response = new BaseResponseDTO<>(
                200, "Booking details updated successfully", new Date(), dto
            );
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            System.err.println("❌ [API] Update booking error: " + e.getMessage());
            BaseResponseDTO<RentalBookingResponseDTO> response = new BaseResponseDTO<>(
                400, e.getMessage(), new Date(), null
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            System.err.println("❌ [API] Unexpected error: " + e.getMessage());
            e.printStackTrace();
            BaseResponseDTO<RentalBookingResponseDTO> response = new BaseResponseDTO<>(
                500, "Terjadi kesalahan pada server", new Date(), null
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/{id}/update-status")
    public ResponseEntity<BaseResponseDTO<RentalBookingResponseDTO>> getBookingForUpdateStatus(
            @PathVariable String id,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            if (authHeader == null || authHeader.trim().isEmpty() || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new BaseResponseDTO<>(HttpStatus.UNAUTHORIZED.value(), "Unauthorized", new Date(), null));
            }
            
            String token = authHeader.substring(7);
            // Only Superadmin and Rental Vendor can update status
            if (!authService.hasVehicleAccess(token)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new BaseResponseDTO<>(HttpStatus.FORBIDDEN.value(), "Forbidden", new Date(), null));
            }
            
            System.out.println("🔍 [API] GET /bookings/" + id + "/update-status");
            System.out.println("   Timestamp: " + new Date());
            
            Optional<RentalBooking> booking = bookingService.getBookingForUpdateStatus(id);
            
            if (booking.isPresent()) {
                System.out.println("✅ Booking found with status: " + booking.get().getStatus());
                RentalBookingResponseDTO dto = convertToDTO(booking.get());
                BaseResponseDTO<RentalBookingResponseDTO> response = new BaseResponseDTO<>(
                    200, "Booking retrieved for status update", new Date(), dto
                );
                System.out.println("✅ Returning booking DTO");
                return ResponseEntity.ok(response);
            }
            
            System.out.println("❌ Booking not found");
            BaseResponseDTO<RentalBookingResponseDTO> response = new BaseResponseDTO<>(
                404, "Booking not found", new Date(), null
            );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (RuntimeException e) {
            System.err.println("❌ [API] Get booking for update status error: " + e.getMessage());
            e.printStackTrace();
            BaseResponseDTO<RentalBookingResponseDTO> response = new BaseResponseDTO<>(
                400, e.getMessage(), new Date(), null
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            System.err.println("❌ [API] Unexpected error: " + e.getMessage());
            e.printStackTrace();
            BaseResponseDTO<RentalBookingResponseDTO> response = new BaseResponseDTO<>(
                500, "Terjadi kesalahan pada server", new Date(), null
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/{id}/available-status-transitions")
    public ResponseEntity<BaseResponseDTO<List<String>>> getAvailableStatusTransitions(
            @PathVariable String id,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            if (authHeader == null || authHeader.trim().isEmpty() || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new BaseResponseDTO<>(HttpStatus.UNAUTHORIZED.value(), "Unauthorized", new Date(), null));
            }
            
            String token = authHeader.substring(7);
            if (!authService.hasVehicleAccess(token)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new BaseResponseDTO<>(HttpStatus.FORBIDDEN.value(), "Forbidden", new Date(), null));
            }
            
            System.out.println("🔍 [API] GET /bookings/" + id + "/available-status-transitions");
            System.out.println("   Timestamp: " + new Date());
            
            Optional<RentalBooking> booking = bookingService.getBookingForUpdateStatus(id);
            
            if (booking.isPresent()) {
                System.out.println("✅ Booking found, current status: " + booking.get().getStatus());
                List<String> transitions = bookingService.getAvailableStatusTransitions(
                    booking.get().getStatus(), 
                    id
                );
                System.out.println("✅ Available transitions: " + transitions);
                BaseResponseDTO<List<String>> response = new BaseResponseDTO<>(
                    200, "Available transitions retrieved", new Date(), transitions
                );
                return ResponseEntity.ok(response);
            }
            
            System.out.println("❌ Booking not found");
            BaseResponseDTO<List<String>> response = new BaseResponseDTO<>(
                404, "Booking not found", new Date(), null
            );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (RuntimeException e) {
            System.err.println("❌ [API] Get transitions error: " + e.getMessage());
            e.printStackTrace();
            BaseResponseDTO<List<String>> response = new BaseResponseDTO<>(
                400, e.getMessage(), new Date(), null
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            System.err.println("❌ [API] Unexpected error: " + e.getMessage());
            e.printStackTrace();
            BaseResponseDTO<List<String>> response = new BaseResponseDTO<>(
                500, "Terjadi kesalahan pada server", new Date(), null
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PutMapping("/update-status")
    public ResponseEntity<BaseResponseDTO<RentalBookingResponseDTO>> updateBookingStatus(
            @RequestBody UpdateBookingStatusDTO updateDTO,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            if (authHeader == null || authHeader.trim().isEmpty() || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new BaseResponseDTO<>(HttpStatus.UNAUTHORIZED.value(), "Unauthorized", new Date(), null));
            }
            
            String token = authHeader.substring(7);
            if (!authService.hasVehicleAccess(token)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new BaseResponseDTO<>(HttpStatus.FORBIDDEN.value(), "Forbidden", new Date(), null));
            }
            
            System.out.println("📝 [API] PUT /bookings/update-status");
            System.out.println("   Timestamp: " + new Date());
            System.out.println("   Booking ID: " + updateDTO.getBookingId());
            System.out.println("   New Status: " + updateDTO.getNewStatus());
            
            RentalBooking updatedBooking = bookingService.updateBookingStatus(updateDTO.getBookingId(), updateDTO);
            System.out.println("✅ Booking status updated successfully");
            
            RentalBookingResponseDTO dto = convertToDTO(updatedBooking);
            
            BaseResponseDTO<RentalBookingResponseDTO> response = new BaseResponseDTO<>(
                200, "Booking status updated successfully", new Date(), dto
            );
            System.out.println("✅ Returning updated booking DTO");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            System.err.println("❌ [API] Update status error: " + e.getMessage());
            e.printStackTrace();
            BaseResponseDTO<RentalBookingResponseDTO> response = new BaseResponseDTO<>(
                400, e.getMessage(), new Date(), null
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            System.err.println("❌ [API] Unexpected error: " + e.getMessage());
            e.printStackTrace();
            BaseResponseDTO<RentalBookingResponseDTO> response = new BaseResponseDTO<>(
                500, "Terjadi kesalahan pada server", new Date(), null
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/{id}/update-addons")
    public ResponseEntity<BaseResponseDTO<RentalBookingResponseDTO>> getBookingForUpdateAddOns(
            @PathVariable String id,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            if (authHeader == null || authHeader.trim().isEmpty() || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new BaseResponseDTO<>(HttpStatus.UNAUTHORIZED.value(), "Unauthorized", new Date(), null));
            }
            
            String token = authHeader.substring(7);
            if (!authService.hasBookingCreateAccess(token)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new BaseResponseDTO<>(HttpStatus.FORBIDDEN.value(), "Forbidden", new Date(), null));
            }
            
            System.out.println("🔍 [API] GET /bookings/" + id + "/update-addons");
            System.out.println("   Timestamp: " + new Date());
            
            Optional<RentalBooking> booking = bookingService.getBookingForUpdateAddOns(id);
            
            if (booking.isPresent()) {
                System.out.println("✅ Booking found for update add-ons");
                RentalBookingResponseDTO dto = convertToDTO(booking.get());
                BaseResponseDTO<RentalBookingResponseDTO> response = new BaseResponseDTO<>(
                    200, "Booking retrieved for add-ons update", new Date(), dto
                );
                System.out.println("✅ Returning booking DTO");
                return ResponseEntity.ok(response);
            }
            
            System.out.println("❌ Booking not found");
            BaseResponseDTO<RentalBookingResponseDTO> response = new BaseResponseDTO<>(
                404, "Booking not found", new Date(), null
            );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (RuntimeException e) {
            System.err.println("❌ [API] Get booking for update add-ons error: " + e.getMessage());
            e.printStackTrace();
            BaseResponseDTO<RentalBookingResponseDTO> response = new BaseResponseDTO<>(
                400, e.getMessage(), new Date(), null
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            System.err.println("❌ [API] Unexpected error: " + e.getMessage());
            e.printStackTrace();
            BaseResponseDTO<RentalBookingResponseDTO> response = new BaseResponseDTO<>(
                500, "Terjadi kesalahan pada server", new Date(), null
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PutMapping("/update-addons")
    public ResponseEntity<BaseResponseDTO<RentalBookingResponseDTO>> updateBookingAddOns(
            @RequestBody UpdateAddOnsRequestDTO updateDTO,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            if (authHeader == null || authHeader.trim().isEmpty() || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new BaseResponseDTO<>(HttpStatus.UNAUTHORIZED.value(), "Unauthorized", new Date(), null));
            }
            
            String token = authHeader.substring(7);
            if (!authService.hasBookingCreateAccess(token)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new BaseResponseDTO<>(HttpStatus.FORBIDDEN.value(), "Forbidden", new Date(), null));
            }
            
            System.out.println("📝 [API] PUT /bookings/update-addons");
            System.out.println("   Timestamp: " + new Date());
            System.out.println("   Booking ID: " + updateDTO.getBookingId());
            System.out.println("   Selected add-ons: " + updateDTO.getSelectedAddOnIds());
            
            RentalBooking updatedBooking = bookingService.updateBookingAddOns(updateDTO.getBookingId(), updateDTO);
            System.out.println("✅ Add-ons updated successfully");
            
            RentalBookingResponseDTO dto = convertToDTO(updatedBooking);
            
            BaseResponseDTO<RentalBookingResponseDTO> response = new BaseResponseDTO<>(
                200, "Add-ons updated successfully", new Date(), dto
            );
            System.out.println("✅ Returning updated booking DTO");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            System.err.println("❌ [API] Update add-ons error: " + e.getMessage());
            e.printStackTrace();
            BaseResponseDTO<RentalBookingResponseDTO> response = new BaseResponseDTO<>(
                400, e.getMessage(), new Date(), null
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            System.err.println("❌ [API] Unexpected error: " + e.getMessage());
            e.printStackTrace();
            BaseResponseDTO<RentalBookingResponseDTO> response = new BaseResponseDTO<>(
                500, "Terjadi kesalahan pada server", new Date(), null
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @DeleteMapping("/{id}/delete")
    public ResponseEntity<BaseResponseDTO<RentalBookingResponseDTO>> cancelBooking(
            @PathVariable String id,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            if (authHeader == null || authHeader.trim().isEmpty() || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new BaseResponseDTO<>(HttpStatus.UNAUTHORIZED.value(), "Unauthorized", new Date(), null));
            }
            
            String token = authHeader.substring(7);
            if (!authService.hasBookingCreateAccess(token)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new BaseResponseDTO<>(HttpStatus.FORBIDDEN.value(), "Forbidden", new Date(), null));
            }
            
            System.out.println("🗑️  [API] DELETE /bookings/" + id + "/delete");
            System.out.println("   Timestamp: " + new Date());
            
            RentalBooking cancelledBooking = bookingService.cancelBooking(id);
            System.out.println("✅ Booking cancelled successfully");
            
            RentalBookingResponseDTO dto = convertToDTO(cancelledBooking);
            
            BaseResponseDTO<RentalBookingResponseDTO> response = new BaseResponseDTO<>(
                200, "Booking dibatalkan dan dihapus dari daftar pesanan", new Date(), dto
            );
            System.out.println("✅ Returning cancelled booking DTO");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            System.err.println("❌ [API] Cancel booking error: " + e.getMessage());
            e.printStackTrace();
            BaseResponseDTO<RentalBookingResponseDTO> response = new BaseResponseDTO<>(
                400, e.getMessage(), new Date(), null
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            System.err.println("❌ [API] Unexpected error: " + e.getMessage());
            e.printStackTrace();
            BaseResponseDTO<RentalBookingResponseDTO> response = new BaseResponseDTO<>(
                500, "Terjadi kesalahan pada server", new Date(), null
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/chart") 
    @CrossOrigin(origins = "http://localhost:5173")
    public ResponseEntity<?> getBookingChart(
            @RequestParam(value = "period", defaultValue = "Monthly") String period,
            @RequestParam(value = "year", defaultValue = "2025") int year,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        
        System.out.println("════════════════════════════════════════════════════");
        System.out.println("📊 [CONTROLLER] GET /api/bookings/chart");
        System.out.println("   Period: " + period);
        System.out.println("   Year: " + year);
        System.out.println("   Timestamp: " + new java.util.Date());
        
        try {
            if (authHeader == null || authHeader.trim().isEmpty() || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    BookingChartResponseDTO.builder()
                        .status(401)
                        .message("Unauthorized")
                        .timestamp(new java.util.Date().toString())
                        .build()
                );
            }
            
            String token = authHeader.substring(7);
            if (!authService.hasBookingReadAccess(token)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                    BookingChartResponseDTO.builder()
                        .status(403)
                        .message("Forbidden")
                        .timestamp(new java.util.Date().toString())
                        .build()
                );
            }
            
            BookingChartDataDTO chartData = bookingService.getBookingChartData(period, year);
            
            System.out.println("✅ Chart data retrieved");
            System.out.println("   Period: " + chartData.getPeriod());
            System.out.println("   Year: " + chartData.getYear());
            System.out.println("   Data points: " + chartData.getData().size());
            
            BookingChartResponseDTO response = BookingChartResponseDTO.builder()
                .status(200)
                .message("Chart data retrieved successfully")
                .timestamp(new java.util.Date().toString())
                .data(chartData)
                .build();
            
            System.out.println("📤 Response sent successfully");
            System.out.println("════════════════════════════════════════════════════");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            System.err.println("❌ Error: " + e.getMessage());
            e.printStackTrace();
            
            BookingChartResponseDTO errorResponse = BookingChartResponseDTO.builder()
                .status(400)
                .message(e.getMessage())
                .timestamp(new java.util.Date().toString())
                .build();
            
            System.err.println("════════════════════════════════════════════════════");
            
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @GetMapping("/current-time")
    public ResponseEntity<?> getCurrentTime() {
        // Public endpoint - no auth needed
        System.out.println("📡 [BE] getCurrentTime endpoint called");
        long currentTimeMillis = System.currentTimeMillis();
        LocalDateTime serverTime = LocalDateTime.now();
        
        System.out.println("   Server time: " + serverTime);
        System.out.println("   Millis: " + currentTimeMillis);
        
        return ResponseEntity.ok().body(Map.of(
            "status", 200,
            "message", "OK",
            "data", Map.of(
                "timestamp", currentTimeMillis,
                "serverTime", serverTime.toString()
            )
        ));
    }
}