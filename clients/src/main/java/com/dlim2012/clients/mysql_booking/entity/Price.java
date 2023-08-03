package com.dlim2012.clients.mysql_booking.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "price")
public class Price implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rooms_id", nullable = false)
    private Rooms rooms;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "price_in_cents", nullable = false)
    private Long priceInCents;

    @Override
    public String toString() {
        return "Price{" +
                "id=" + id +
                ", rooms=" + (rooms == null ? null : rooms.getId()) +
                ", date=" + date +
                ", priceInCents=" + priceInCents +
                '}';
    }
}
