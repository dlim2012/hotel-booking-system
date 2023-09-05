package com.dlim2012.clients.mysql_booking.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Set;

@Builder
@NoArgsConstructor(force = true)
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "rooms")
public class Rooms implements Serializable {

    @Id
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_id", nullable = false)
    private Hotel hotel;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "display_name", nullable = false)
    private String displayName;

    @Column(name = "short_name", nullable = false)
    private String shortName;

    @Column(name = "price_min", nullable = false)
    private Long priceMin;

    @Column(name = "price_max", nullable = false)
    private Long priceMax;

    @Column(name = "check_in_time", nullable = false)
    private Integer checkInTime;

    @Column(name = "check_out_time", nullable = false)
    private Integer checkOutTime;

    @Column(name = "available_from", nullable = false)
    private LocalDate availableFrom;

    @Column(name = "available_until")
    private LocalDate availableUntil;

    @Column(name = "free_cancellation_days")
    private Integer freeCancellationDays;

    @Column(name = "no_prepayment_days")
    private Integer noPrepaymentDays;

    @Column(name = "dates_reserved", nullable = false)
    private Integer datesReserved;

    @Column(name = "dates_booked", nullable = false)
    private Integer datesBooked;

    @Column(name = "dates_added_until", nullable = false)
    private LocalDate datesAddedUntil;

    @OneToMany(mappedBy = "rooms", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Room> roomSet;

    @OneToMany(mappedBy = "rooms", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Price> priceList;


    @Override
    public String toString() {
        return "Rooms{" +
                "id=" + id +
                ", hotel=" + (hotel == null ? null : hotel.getId()) +
                ", quantity=" + quantity +
                ", displayName='" + displayName + '\'' +
                ", shortName='" + shortName + '\'' +
                ", priceMin=" + priceMin +
                ", priceMax=" + priceMax +
                ", checkInTime=" + checkInTime +
                ", checkOutTime=" + checkOutTime +
                ", availableFrom=" + availableFrom +
                ", availableUntil=" + availableUntil +
                ", datesReserved=" + datesReserved +
                ", datesBooked=" + datesBooked +
                ", datesAddedUntil=" + datesAddedUntil +
                ", roomSet=" + roomSet +
                ", priceList=" + "?" +
                '}';
    }
}
