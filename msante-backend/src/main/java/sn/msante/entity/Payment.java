package sn.msante.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Payment entity for transaction tracking
 */
@Entity
@Table(name = "payment")
@Data
@EqualsAndHashCode(callSuper = true)
public class Payment extends BaseEntity {
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;
    
    @Column(name = "method", nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentMethod method;
    
    @Column(name = "amount_cfa", nullable = false)
    private Integer amountCfa;
    
    @Column(name = "commission_cfa", nullable = false)
    private Integer commissionCfa = 0;
    
    @Column(name = "net_amount_cfa", nullable = false)
    private Integer netAmountCfa;
    
    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentStatus status = PaymentStatus.INITIATED;
    
    @Column(name = "reference")
    private String reference; // External payment reference
    
    @Column(name = "proof_url")
    private String proofUrl; // Receipt/proof document URL
    
    @Column(name = "gateway_response", columnDefinition = "jsonb")
    private String gatewayResponse; // JSON response from payment gateway
    
    @Column(name = "processed_at")
    private java.time.LocalDateTime processedAt;
    
    @Column(name = "failed_reason")
    private String failedReason;
    
    @Column(name = "refunded_at")
    private java.time.LocalDateTime refundedAt;
    
    @Column(name = "refund_reason")
    private String refundReason;
    
    @Column(name = "refund_amount_cfa")
    private Integer refundAmountCfa;
    
    public enum PaymentMethod {
        ORANGE_MONEY, WAVE, FREE_MONEY, CARD, BANK_TRANSFER, CASH
    }
    
    public enum PaymentStatus {
        INITIATED, PENDING, SUCCEEDED, FAILED, CANCELLED, REFUNDED, CHARGEBACK
    }
    
    public boolean isSuccessful() {
        return status == PaymentStatus.SUCCEEDED;
    }
    
    public boolean canBeRefunded() {
        return status == PaymentStatus.SUCCEEDED && refundedAt == null;
    }
    
    public Integer getPractitionerAmount() {
        return netAmountCfa - commissionCfa;
    }
}