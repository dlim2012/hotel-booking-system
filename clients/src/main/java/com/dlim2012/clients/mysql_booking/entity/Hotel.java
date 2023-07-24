package com.dlim2012.clients.mysql_booking.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "hotel")
public class Hotel {

    @Id
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "hotel_manager_id", nullable = false)
    private Integer hotelManagerId;


    @OneToMany(mappedBy = "hotel", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<Rooms> roomsSet;

    @Override
    public String toString() {
        return "Hotel{" +
                "id=" + id +
                ", hotelManagerId=" + hotelManagerId +
                ", roomsSet=" + roomsSet +
                '}';
    }
}
