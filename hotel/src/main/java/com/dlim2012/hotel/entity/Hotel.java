package com.dlim2012.hotel.entity;

import com.dlim2012.hotel.entity.facility.HotelFacility;
import com.dlim2012.hotel.entity.file.HotelImage;
import com.dlim2012.hotel.entity.locality.Locality;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "hotel")
@Table(name = "hotel")
public class Hotel {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "hotel_manager_id", nullable = false)
    private Integer hotelManagerId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "descriptions", nullable = false)
    private String description;

    @Column(name = "is_active", nullable = false)
    private String isActive;

    @Column(name = "address_line_1", nullable = false)
    private String addressLine1;

    @Column(name = "address_line_2")
    private String addressLine2;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "locality_id", nullable = false)
    private Locality locality;

    @Column(name = "updated_time", nullable = false)
    private LocalDateTime updatedTime;

    // Rooms
    @OneToMany(mappedBy = "hotel", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Room> rooms = new ArrayList<>();

    // Images
    @OneToMany(mappedBy = "hotel" , fetch = FetchType.LAZY)
    private List<HotelImage> hotelImages = new ArrayList<>();

    // Facilities
    @OneToMany(mappedBy = "hotel", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<HotelFacility> hotelFacilities = new ArrayList<>();

}
