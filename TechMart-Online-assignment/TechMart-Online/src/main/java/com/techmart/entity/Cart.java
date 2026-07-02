package com.techmart.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "cart")
@Cacheable(false)
@Getter
@Setter
@NoArgsConstructor
public class Cart implements Serializable {

    public static final String STATUS_ACTIVE = "ACTIVE";
    public static final String STATUS_CHECKED_OUT = "CHECKED_OUT";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cart_id")
    private Integer cartId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false, unique = true)
    private Customer customer;

    @Column(name = "status", length = 20, nullable = false)
    private String status = STATUS_ACTIVE;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "cart", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> items = new ArrayList<>();

    @PrePersist
    @PreUpdate
    protected void onChange() {
        updatedAt = LocalDateTime.now();
    }
}