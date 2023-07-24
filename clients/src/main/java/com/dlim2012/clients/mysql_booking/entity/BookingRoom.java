package com.dlim2012.clients.mysql_booking.entity;

import com.dlim2012.clients.entity.BookingStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "booking_room")
public class BookingRoom {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_rooms_id", nullable = false)
    private BookingRooms bookingRooms;

    @Column(name = "room_id", nullable = false)
    private Long roomId;

    @Column(name = "start_date_time", nullable = false)
    private LocalDateTime startDateTime;

    @Column(name = "end_date_time", nullable = false)
    private LocalDateTime endDateTime;

    @Column(name = "status", nullable = false)
    BookingStatus status;

    @Column(name = "guest_name", nullable = true)
    private String guestName;

    @Column(name = "guest_email", nullable = true)
    private String guestEmail;

    @Override
    public String toString() {
        return "BookingRoom{" +
                "id=" + id +
                ", bookingRooms=" + (bookingRooms == null ? null : bookingRooms.getId()) +
                ", roomId=" + roomId +
                ", startDateTime=" + startDateTime +
                ", endDateTime=" + endDateTime +
                ", status=" + status +
                ", guestName='" + guestName + '\'' +
                ", guestEmail='" + guestEmail + '\'' +
                '}';
    }
}
