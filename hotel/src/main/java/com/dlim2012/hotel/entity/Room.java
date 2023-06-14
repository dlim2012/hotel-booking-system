package com.dlim2012.hotel.entity;

import com.dlim2012.hotel.entity.facility.RoomFacility;
import com.dlim2012.hotel.entity.file.RoomImage;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
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

    @Column(name = "is_active", nullable = false)
    private boolean isActive;

    // todo: add description, maximum number of adult, and maximum number of child
    @Column(name = "description")
    private String description;

    @Column(name = "max_adult")
    private Integer maxAdult;

    @Column(name = "max_child")
    private Integer maxChild;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "price_min")
    private Double priceMin;

    @Column(name = "price_max")
    private Double priceMax;

    // Images
    @OneToMany(mappedBy = "room", fetch = FetchType.LAZY)
    private List<RoomImage> roomImages = new ArrayList<>();

    // Facilities
    @OneToMany(mappedBy = "room", fetch = FetchType.EAGER)
    private List<RoomFacility> roomFacilities = new ArrayList<>();

}
