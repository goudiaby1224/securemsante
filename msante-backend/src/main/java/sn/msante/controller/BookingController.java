package sn.msante.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import sn.msante.dto.BookingDto;
import sn.msante.dto.CreateBookingDto;
import sn.msante.security.MSantePrincipal;
import sn.msante.service.BookingService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Booking controller for appointment management
 */
@RestController
@RequestMapping("/api/bookings")
@Tag(name = "Bookings", description = "Appointment booking and management endpoints")
public class BookingController {
    
    private final BookingService bookingService;
    
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }
    
    @PostMapping
    @Operation(summary = "Create Booking", description = "Create a new appointment booking (supports guest bookings)")
    public ResponseEntity<BookingDto> createBooking(
            @Valid @RequestBody CreateBookingDto request,
            Authentication authentication) {
        
        String currentUserId = null;
        if (authentication != null && authentication.getPrincipal() instanceof MSantePrincipal principal) {
            currentUserId = principal.getUserId();
        }
        
        BookingDto booking = bookingService.createBooking(request, currentUserId);
        return ResponseEntity.status(HttpStatus.CREATED).body(booking);
    }
    
    @GetMapping("/my")
    @Operation(summary = "Get My Bookings", description = "Get current user's bookings")
    @SecurityRequirement(name = "bearer-key")
    public ResponseEntity<List<BookingDto>> getMyBookings(Authentication authentication) {
        MSantePrincipal principal = (MSantePrincipal) authentication.getPrincipal();
        List<BookingDto> bookings = bookingService.getUserBookings(principal.getUserId());
        return ResponseEntity.ok(bookings);
    }
    
    @GetMapping("/guest")
    @Operation(summary = "Get Guest Bookings", description = "Get bookings for a phone number (guest mode)")
    public ResponseEntity<List<BookingDto>> getGuestBookings(@RequestParam String phone) {
        List<BookingDto> bookings = bookingService.getGuestBookings(phone);
        return ResponseEntity.ok(bookings);
    }
    
    @PutMapping("/{id}/confirm")
    @Operation(summary = "Confirm Booking", description = "Confirm booking after successful payment")
    @SecurityRequirement(name = "bearer-key")
    public ResponseEntity<BookingDto> confirmBooking(@PathVariable String id) {
        BookingDto booking = bookingService.confirmBooking(UUID.fromString(id));
        return ResponseEntity.ok(booking);
    }
    
    @PutMapping("/{id}/cancel")
    @Operation(summary = "Cancel Booking", description = "Cancel an existing booking")
    @SecurityRequirement(name = "bearer-key")
    public ResponseEntity<BookingDto> cancelBooking(
            @PathVariable String id,
            @RequestParam(required = false) String reason,
            Authentication authentication) {
        
        MSantePrincipal principal = (MSantePrincipal) authentication.getPrincipal();
        BookingDto booking = bookingService.cancelBooking(
            UUID.fromString(id), 
            reason, 
            principal.getUserId()
        );
        return ResponseEntity.ok(booking);
    }
    
    @GetMapping("/practitioner/{practitionerId}")
    @Operation(summary = "Get Practitioner Bookings", description = "Get bookings for a specific practitioner")
    @SecurityRequirement(name = "bearer-key")
    public ResponseEntity<List<BookingDto>> getPractitionerBookings(
            @PathVariable String practitionerId,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            Authentication authentication) {
        
        MSantePrincipal principal = (MSantePrincipal) authentication.getPrincipal();
        
        // Practitioners can only see their own bookings, admins can see any
        if (!principal.isAdmin() && !practitionerId.equals(principal.getUserId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        LocalDateTime start = startDate != null ? LocalDateTime.parse(startDate) : LocalDateTime.now().withHour(0).withMinute(0);
        LocalDateTime end = endDate != null ? LocalDateTime.parse(endDate) : start.plusDays(30);
        
        List<BookingDto> bookings = bookingService.getPractitionerBookings(practitionerId, start, end);
        return ResponseEntity.ok(bookings);
    }
}