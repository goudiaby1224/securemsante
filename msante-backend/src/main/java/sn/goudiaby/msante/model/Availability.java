package sn.goudiaby.msante.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Entity
@Table(name = "availabilities")
@Data
@EqualsAndHashCode(of = "id")
public class Availability {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column(nullable = false)
    private LocalDateTime endTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.AVAILABLE;

    @OneToOne(mappedBy = "availability", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Appointment appointment;

    public enum Status {
        AVAILABLE, BOOKED, CANCELLED
    }
}