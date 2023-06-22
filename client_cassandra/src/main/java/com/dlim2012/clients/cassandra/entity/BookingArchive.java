package com.dlim2012.clients.cassandra.entity;

import com.dlim2012.clients.entity.BookingEntity;
import com.dlim2012.clients.entity.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(value = "booking_archive")
public class BookingArchive {

    @PrimaryKeyColumn(name = "booking_id", type = PrimaryKeyType.PARTITIONED)
    private Long bookingId;

    @Column(value = "user_id")
    private Integer userId;

    @Column(value = "hotel_id")
    private Integer hotelId;

    @Column(value = "room_id")
    private Integer roomId;

    @Column(value = "start_date_time")
    private LocalDateTime startDateTime;

    @Column(value = "end_date_time")
    private LocalDateTime endDateTime;

    @Column(value = "quantity")
    private Integer quantity;

    @Column(value = "status")
    private BookingStatus status;

    @Column(value = "price_in_cents")
    private Long priceInCents;

    @Column(value = "invoice_id")
    private String invoiceId;

    @Column(value = "invoice_confirm_time")
    private LocalDateTime invoiceConfirmTime;
}
