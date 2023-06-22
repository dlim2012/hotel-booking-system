package com.dlim2012.hotel.entity;

import com.dlim2012.hotel.entity.facility.RoomFacility;
import com.dlim2012.hotel.entity.file.RoomImage;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "room")
@Table(name = "room")
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "hotel_id", nullable = false)
    private Hotel hotel;

    @Column(name = "display_name", nullable = false)
    private String displayName;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "is_active", nullable = false)
    private boolean isActive;

    @Column(name = "max_adult", nullable = false)
    private Integer maxAdult;

    @Column(name = "max_child", nullable = false)
    private Integer maxChild;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "price_min", nullable = false)
    private Double priceMin;

    @Column(name = "price_max", nullable = false)
    private Double priceMax;

    @Column(name = "check_in_time", nullable = false)
    private Integer checkInTime;

    @Column(name = "check_out_time", nullable = false)
    private Integer checkOutTime;

    @Column(name = "available_from")
    private LocalDate availableFrom;

    @Column(name = "available_until")
    private LocalDate availableUntil;

    @Column(name = "updated_time", nullable = false)
    private LocalDateTime updatedTime;


    // Images
    @OneToMany(mappedBy = "room", fetch = FetchType.LAZY)
    private List<RoomImage> roomImages = new ArrayList<>();

    // Facilities
    @OneToMany(mappedBy = "room", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<RoomFacility> roomFacilities = new ArrayList<>();


}
