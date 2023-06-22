package com.dlim2012.clients.mysql_booking.entity;

import com.dlim2012.clients.entity.BookingEntity;
import com.dlim2012.clients.entity.BookingStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;


@Data
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

    @Column(name = "room_id", nullable = false)
    private Integer roomId;

    @Column(name = "hotel_id", nullable = false)
    private Integer hotelId;

    @Column(name = "start_date_time", nullable = false)
    private LocalDateTime startDateTime;

    @Column(name = "end_date", nullable = false)
    private LocalDateTime endDateTime;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "status", nullable = false)
    private BookingStatus status;

    @Column(name = "price_in_cents", nullable = false)
    private Long priceInCents;

    @Column(name = "invoice_id")
    private String invoiceId; // Invoice.id without foreign key constraint

    @Column(name = "invoice_confirm_time")
    private LocalDateTime invoiceConfirmTime;
}

