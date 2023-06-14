package com.dlim2012.hotel.entity;

import com.dlim2012.hotel.entity.facility.HotelFacility;
import com.dlim2012.hotel.entity.file.HotelImage;
import com.dlim2012.hotel.entity.locality.Locality;
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
@Table(name = "hotel")
public class Hotel {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "descriptions")
    private String description;

    @Column(name = "is_active", nullable = false)
    private boolean isActive;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "locality_id", nullable = false)
    private Locality locality;

    @Column(name = "address_line_1", nullable = false)
    private String addressLine1;

    @Column(name = "address_line_2")
    private String addressLine2;

    // Rooms
    @OneToMany(mappedBy = "hotel", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Room> rooms = new ArrayList<>();

    // Images
    @OneToMany(mappedBy = "hotel" , fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<HotelImage> hotelImages = new ArrayList<>();

    // Facilities
    @OneToMany(mappedBy = "hotel", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<HotelFacility> hotelFacilities = new ArrayList<>();

}
