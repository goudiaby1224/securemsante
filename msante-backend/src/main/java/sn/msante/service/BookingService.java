package sn.msante.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.msante.dto.BookingDto;
import sn.msante.dto.CreateBookingDto;
import sn.msante.entity.AvailabilitySlot;
import sn.msante.entity.Booking;
import sn.msante.entity.UserAccount;
import sn.msante.exception.BookingException;
import sn.msante.repository.AvailabilitySlotRepository;
import sn.msante.repository.BookingRepository;
import sn.msante.repository.UserAccountRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Booking service for appointment management
 */
@Service
@Transactional
public class BookingService {
    
    private final BookingRepository bookingRepository;
    private final AvailabilitySlotRepository slotRepository;
    private final UserAccountRepository userAccountRepository;
    private final NotificationService notificationService;
    
    public BookingService(BookingRepository bookingRepository,
                         AvailabilitySlotRepository slotRepository,
                         UserAccountRepository userAccountRepository,
                         NotificationService notificationService) {
        this.bookingRepository = bookingRepository;
        this.slotRepository = slotRepository;
        this.userAccountRepository = userAccountRepository;
        this.notificationService = notificationService;
    }
    
    /**
     * Create a new booking (supports guest bookings)
     */
    public BookingDto createBooking(CreateBookingDto request, String currentUserId) {
        // Validate and get slot
        UUID slotId = UUID.fromString(request.getSlotId());
        Optional<AvailabilitySlot> slotOpt = slotRepository.findById(slotId);
        
        if (slotOpt.isEmpty()) {
            throw new BookingException("Availability slot not found");
        }
        
        AvailabilitySlot slot = slotOpt.get();
        
        // Check if slot is still available
        if (slot.getIsBooked() || !slot.isAvailable()) {
            throw new BookingException("This time slot is no longer available");
        }
        
        // Get practitioner
        UserAccount practitioner = slot.getPractitioner();
        
        // Create booking
        Booking booking = new Booking();
        booking.setTenant(slot.getTenant());
        booking.setPractitioner(practitioner);
        booking.setSlot(slot);
        booking.setStatus(Booking.BookingStatus.PENDING_PAYMENT);
        booking.setConsultationReason(request.getConsultationReason());
        booking.setNotes(request.getNotes());
        
        // Set patient info (either registered user or guest)
        if (currentUserId != null) {
            // Registered user booking
            Optional<UserAccount> patientOpt = userAccountRepository.findById(UUID.fromString(currentUserId));
            if (patientOpt.isPresent()) {
                booking.setPatient(patientOpt.get());
            }
        } else {
            // Guest booking
            booking.setPatientName(request.getContact().getName());
            booking.setPatientPhone(request.getContact().getPhone());
            booking.setPatientEmail(request.getContact().getEmail());
        }
        
        // Mark slot as booked
        slot.setIsBooked(true);
        slotRepository.save(slot);
        
        // Save booking
        booking = bookingRepository.save(booking);
        
        return convertToDto(booking);
    }
    
    /**
     * Confirm booking after payment
     */
    public BookingDto confirmBooking(UUID bookingId) {
        Optional<Booking> bookingOpt = bookingRepository.findById(bookingId);
        
        if (bookingOpt.isEmpty()) {
            throw new BookingException("Booking not found");
        }
        
        Booking booking = bookingOpt.get();
        
        if (booking.getStatus() != Booking.BookingStatus.PENDING_PAYMENT) {
            throw new BookingException("Booking cannot be confirmed in current status");
        }
        
        booking.setStatus(Booking.BookingStatus.CONFIRMED);
        booking.setConfirmedAt(LocalDateTime.now());
        booking = bookingRepository.save(booking);
        
        // Send confirmation notification
        String phoneNumber = booking.isGuest() ? booking.getPatientPhone() : booking.getPatient().getPhone();
        if (phoneNumber != null) {
            notificationService.sendAppointmentConfirmation(
                phoneNumber,
                booking.getPractitioner().getEmail(), // In real app, use practitioner name
                booking.getSlot().getStartsAt().toString(),
                booking.getId().toString().substring(0, 8)
            );
        }
        
        return convertToDto(booking);
    }
    
    /**
     * Cancel booking
     */
    public BookingDto cancelBooking(UUID bookingId, String reason, String cancelledBy) {
        Optional<Booking> bookingOpt = bookingRepository.findById(bookingId);
        
        if (bookingOpt.isEmpty()) {
            throw new BookingException("Booking not found");
        }
        
        Booking booking = bookingOpt.get();
        
        if (!booking.canBeCancelled()) {
            throw new BookingException("Booking cannot be cancelled");
        }
        
        // Check cancellation policy (72 hours by default)
        LocalDateTime cancellationDeadline = booking.getSlot().getStartsAt().minusHours(72);
        if (LocalDateTime.now().isAfter(cancellationDeadline)) {
            throw new BookingException("Cancellation deadline has passed");
        }
        
        booking.setStatus(Booking.BookingStatus.CANCELLED);
        booking.setCancelledAt(LocalDateTime.now());
        booking.setCancellationReason(reason);
        booking.setCancelledBy(UUID.fromString(cancelledBy));
        
        // Free up the slot
        AvailabilitySlot slot = booking.getSlot();
        slot.setIsBooked(false);
        slotRepository.save(slot);
        
        booking = bookingRepository.save(booking);
        
        return convertToDto(booking);
    }
    
    /**
     * Get user's bookings
     */
    public List<BookingDto> getUserBookings(String userId) {
        List<Booking> bookings = bookingRepository.findByPatientId(UUID.fromString(userId));
        return bookings.stream()
                      .map(this::convertToDto)
                      .collect(Collectors.toList());
    }
    
    /**
     * Get practitioner's bookings
     */
    public List<BookingDto> getPractitionerBookings(String practitionerId, LocalDateTime startDate, LocalDateTime endDate) {
        List<Booking> bookings = bookingRepository.findByPractitionerAndDateRange(
            UUID.fromString(practitionerId), startDate, endDate
        );
        return bookings.stream()
                      .map(this::convertToDto)
                      .collect(Collectors.toList());
    }
    
    /**
     * Get guest bookings by phone
     */
    public List<BookingDto> getGuestBookings(String phone) {
        List<Booking> bookings = bookingRepository.findGuestBookingsByPhone(phone);
        return bookings.stream()
                      .map(this::convertToDto)
                      .collect(Collectors.toList());
    }
    
    private BookingDto convertToDto(Booking booking) {
        BookingDto dto = new BookingDto();
        dto.setBookingId(booking.getId().toString());
        dto.setPractitionerId(booking.getPractitioner().getId().toString());
        dto.setPractitionerName(booking.getPractitioner().getEmail()); // In real app, use name
        dto.setSlotId(booking.getSlot().getId().toString());
        dto.setStartsAt(booking.getSlot().getStartsAt());
        dto.setEndsAt(booking.getSlot().getEndsAt());
        dto.setConsultationMode(booking.getSlot().getMode().toString());
        dto.setPriceCfa(booking.getSlot().getPriceCfa());
        dto.setStatus(booking.getStatus().toString());
        dto.setPatientName(booking.getPatientDisplayName());
        dto.setConsultationReason(booking.getConsultationReason());
        dto.setNotes(booking.getNotes());
        dto.setCreatedAt(booking.getCreatedAt());
        dto.setConfirmedAt(booking.getConfirmedAt());
        dto.setCanBeCancelled(booking.canBeCancelled());
        
        // Calculate cancellation deadline
        if (booking.canBeCancelled()) {
            dto.setCancellationDeadline(booking.getSlot().getStartsAt().minusHours(72));
        }
        
        return dto;
    }
}