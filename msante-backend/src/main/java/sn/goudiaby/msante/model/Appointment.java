package sn.goudiaby.msante.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "appointments")
@Data
@EqualsAndHashCode(of = "id")
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @OneToOne
    @JoinColumn(name = "availability_id", nullable = false, unique = true)
    private Availability availability;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.PENDING;

    @Column(columnDefinition = "TEXT")
    private String notes;

    public enum Status {
        PENDING, CONFIRMED, CANCELLED, COMPLETED
    }
    //created_at and updated_at fields can be added later if needed

    @Column(name = "created_at", updatable = false)
    private java.time.LocalDateTime createdAt;
    @Column(name = "updated_at")
    private java.time.LocalDateTime updatedAt;
}