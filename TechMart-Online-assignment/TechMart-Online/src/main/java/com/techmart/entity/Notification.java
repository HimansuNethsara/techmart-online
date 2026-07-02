package com.techmart.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "notification")
@Getter
@Setter
@NoArgsConstructor
public class Notification implements Serializable {

    public static final String TYPE_ORDER_CONFIRMATION = "ORDER_CONFIRMATION";
    public static final String TYPE_LOW_STOCK = "LOW_STOCK";
    public static final String TYPE_SHIPPING_UPDATE = "SHIPPING_UPDATE";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Integer notificationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = true)
    private Customer customer;

    @Column(name = "type", length = 30, nullable = false)
    private String type;

    @Column(name = "message", length = 500, nullable = false)
    private String message;

    @Column(name = "sent_at", nullable = false, updatable = false)
    private LocalDateTime sentAt;

    @PrePersist
    protected void onCreate() {
        if (sentAt == null) {
            sentAt = LocalDateTime.now();
        }
    }
}