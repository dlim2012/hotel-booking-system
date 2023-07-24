package com.dlim2012.hotel.entity;

import com.dlim2012.hotel.entity.facility.RoomsBed;
import com.dlim2012.hotel.entity.facility.RoomsFacility;
import com.dlim2012.hotel.entity.file.RoomsImage;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "rooms")
@Table(name = "rooms")
public class Rooms {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "hotel_id", nullable = false)
    private Hotel hotel;

    @Column(name = "display_name", nullable = false)
    private String displayName;

    @Column(name = "short_name", nullable = false) // cannot have any number
    private String shortName;

    @Column(name = "description", nullable = false, length = 512)
    private String description;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @Column(name = "max_adult", nullable = false)
    private Integer maxAdult;

    @Column(name = "max_child", nullable = false)
    private Integer maxChild;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

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

//    @Enumerated(EnumType.ORDINAL)
//    private PaymentOption paymentOption;

    @Column(name = "updated_time", nullable = false)
    private LocalDateTime updatedTime;

    // Images
    @OneToMany(mappedBy = "rooms", fetch = FetchType.LAZY)
    private List<RoomsImage> roomsImages = new ArrayList<>();

    // Facilities
    @OneToMany(mappedBy = "rooms", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<RoomsFacility> roomFacilities = new ArrayList<>();

    // Beds
    @OneToMany(mappedBy = "rooms", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<RoomsBed> roomsBeds = new ArrayList<>();

    @Override
    public String toString() {
        return "Rooms{" +
                "id=" + id +
                ", hotel=" + (hotel == null ? null : hotel.getId()) +
                ", displayName='" + displayName + '\'' +
                ", description='" + description + '\'' +
                ", isActive=" + isActive +
                ", maxAdult=" + maxAdult +
                ", maxChild=" + maxChild +
                ", quantity=" + quantity +
                ", priceMin=" + priceMin +
                ", priceMax=" + priceMax +
                ", checkInTime=" + checkInTime +
                ", checkOutTime=" + checkOutTime +
                ", availableFrom=" + availableFrom +
                ", availableUntil=" + availableUntil +
                ", freeCancellationDays=" + freeCancellationDays +
                ", noPrepaymentDays=" + noPrepaymentDays +
                ", updatedTime=" + updatedTime +
                ", roomsImages=" + roomsImages +
                ", roomFacilities=" + roomFacilities +
                ", roomsBeds=" + roomsBeds +
                '}';
    }
}
