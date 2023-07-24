package com.dlim2012.clients.mysql_booking.entity;

import com.dlim2012.clients.entity.BookingMainStatus;
import com.dlim2012.clients.entity.BookingStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "booking")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Integer userId; // Foreign key to User ID

    @Column(name = "hotel_id", nullable = false)
    private Integer hotelId;

    @Column(name = "hotel_manager_id", nullable = false)
    private Integer hotelManagerId;

    @Column(name = "reservation_time", nullable = false)
    private LocalDateTime reservationTime;

    @Column(name = "hotel_name", nullable = false)
    private String hotelName;

    @Column(name = "neighborhood", nullable = false)
    private String neighborhood;

    @Column(name = "city", nullable = false)
    private String city;

    @Column(name = "state", nullable = false)
    private String state;

    @Column(name = "country", nullable = false)
    private String country;

    @Column(name = "booking_rooms", nullable = false)
    @OneToMany(mappedBy = "booking", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<BookingRooms> bookingRooms;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "email")
    private String email;

    @Column(name = "special_requests")
    private String specialRequests;

    @Column(name = "estimated_arrival_hour")
    private Integer estimatedArrivalHour;

//    @Column(name = "quantity", nullable = false)
//    private Integer quantity;

    @Column(name = "start_date_time", nullable = false)
    private LocalDateTime startDateTime;

    @Column(name = "end_date_time", nullable = false)
    private LocalDateTime endDateTime;

    @Column(name = "main_status", nullable = false)
    private BookingMainStatus mainStatus;

    @Column(name = "status", nullable = false)
    private BookingStatus status;

    @Column(name = "price_in_cents", nullable = false)
    private Long priceInCents;

    @Column(name = "invoice_id")
    private String invoiceId; // Invoice.id without foreign key constraint

    @Column(name = "invoice_confirm_time")
    private LocalDateTime invoiceConfirmTime;

    @Override
    public String toString() {
        return "Booking{" +
                "id=" + id +
                ", userId=" + userId +
                ", hotelId=" + hotelId +
                ", rooms=" + bookingRooms +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", specialRequests='" + specialRequests + '\'' +
                ", estimatedArrivalHour=" + estimatedArrivalHour +
                ", status=" + status +
                ", priceInCents=" + priceInCents +
                ", invoiceId='" + invoiceId + '\'' +
                ", invoiceConfirmTime=" + invoiceConfirmTime +
                '}';
    }
}

