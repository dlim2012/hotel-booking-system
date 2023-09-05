package com.dlim2012.clients.mysql_booking.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "booking_rooms")
public class BookingRooms implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    @Column(name = "rooms_id", nullable = false)
    private Integer roomsId;

    @Column(name = "rooms_display_name", nullable = false)
    private String roomsDisplayName;

    @Column(name = "rooms_short_name", nullable = false)
    private String roomsShortName;

    @Column(name = "prepay_until")
    private LocalDate prepayUntil;

    @Column(name = "free_cancellation_until")
    private LocalDate freeCancellationUntil;

    @Column(name = "price_per_room_in_cents", nullable = false)
    private Long pricePerRoomInCents;

    @OneToMany(mappedBy = "bookingRooms", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<BookingRoom> bookingRoomSet;


    @Override
    public String toString() {
        return "BookingRooms{" +
                "id=" + id +
                ", booking=" + (booking == null ? null : booking.getId()) +
                ", roomsId=" + roomsId +
                ", roomsDisplayName='" + roomsDisplayName + '\'' +
                ", roomsShortName='" + roomsShortName + '\'' +
                ", prepayUntil=" + prepayUntil +
                ", freeCancellationUntil=" + freeCancellationUntil +
                ", pricePerRoomInCents=" + pricePerRoomInCents +
                ", bookingRoomList=" + bookingRoomSet +
                '}';
    }
}
