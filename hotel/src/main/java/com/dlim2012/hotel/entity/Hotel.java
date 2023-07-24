package com.dlim2012.hotel.entity;

import com.dlim2012.clients.entity.PropertyType;
import com.dlim2012.hotel.entity.facility.HotelFacility;
import com.dlim2012.hotel.entity.file.HotelImage;
import com.dlim2012.hotel.entity.locality.Locality;
import com.dlim2012.hotel.entity.saved.SavedUser;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
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

    @Column(name = "descriptions", nullable = false, length = 512)
    private String description;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @Column(name = "address_line_1", nullable = false)
    private String addressLine1;

    @Column(name = "address_line_2")
    private String addressLine2;

    @Column(name = "neighborhood")
    private String neighborhood;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "locality_id", nullable = false)
    private Locality locality;

//    @Column(name = "property_type")
    @Enumerated(value = EnumType.ORDINAL)
    private PropertyType propertyType;

    @Column(name = "phone")
    private String phone;

    @Column(name = "fax")
    private String fax;

    @Column(name = "website")
    private String website;

    @Column(name = "email")
    private String email;

    @Column(name = "property_rating")
    private Integer propertyRating;

    @Column(name = "latitude", nullable = false)
    private Double latitude;

    @Column(name = "longitude", nullable = false)
    private Double longitude;

//    @Column(name = "distance_from_center")
//    private Double distanceFromCenter;

    // Rooms
    @OneToMany(mappedBy = "hotel", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Rooms> rooms = new ArrayList<>();

    // Images
    @Column(name = "main_image_id", nullable = true)
    private Integer mainImageId;

    @Column(name = "updated_time", nullable = false)
    private LocalDateTime updatedTime;

    @OneToMany(mappedBy = "hotel" , fetch = FetchType.LAZY)
    private List<HotelImage> hotelImages = new ArrayList<>();

    // Facilities
    @OneToMany(mappedBy = "hotel", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<HotelFacility> hotelFacilities = new ArrayList<>();

    // Saved users
    @OneToMany(mappedBy = "hotel", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<SavedUser> savedUsers = new ArrayList<>();

}
