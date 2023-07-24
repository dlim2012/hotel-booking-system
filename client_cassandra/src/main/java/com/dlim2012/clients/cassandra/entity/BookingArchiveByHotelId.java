package com.dlim2012.clients.cassandra.entity;

import com.dlim2012.clients.entity.BookingMainStatus;
import com.dlim2012.clients.entity.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.Frozen;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(value = "booking_archive_by_hotel_id")
public class BookingArchiveByHotelId {
    @PrimaryKeyColumn(name = "hotel_id", type = PrimaryKeyType.PARTITIONED)
    private Integer hotelId;

    @PrimaryKeyColumn(name = "main_status", type = PrimaryKeyType.CLUSTERED)
    private BookingMainStatus mainStatus;

    @PrimaryKeyColumn(name = "end_date_time", type = PrimaryKeyType.CLUSTERED)
    private LocalDateTime endDateTime;

    @Column(value = "user_id")
    private Integer userId;

    @Column(value = "booking_id")
    private Long bookingId;

    @Column(value = "hotel_manager_id")
    private Integer hotelManagerId;

    @Column(value = "reservation_time")
    private LocalDateTime reservationTime;

    @Column(value = "first_name")
    private String firstName;

    @Column(value = "last_name")
    private String lastName;

    @Column(value = "email")
    private String email;

    @Column(value = "hotel_name")
    private String hotelName;

    @Column(value = "neighborhood")
    private String neighborhood;

    @Column(value = "city")
    private String city;

    @Column(value = "state")
    private String state;

    @Column(value = "country")
    private String country;

    @Column(value = "rooms")
    private List<@Frozen BookingArchiveRoom> rooms;

    @Column(value = "status")
    private BookingStatus status;

    @Column(value = "start_date_time")
    private LocalDateTime startDateTime;

    @Column(value = "price_in_cents")
    private Long priceInCents;

    @Column(value = "invoice_id")
    private String invoiceId;

    @Column(value = "invoice_confirm_time")
    private LocalDateTime invoiceConfirmTime;



}
