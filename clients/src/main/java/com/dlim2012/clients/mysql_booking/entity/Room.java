package com.dlim2012.clients.mysql_booking.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.Set;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "room")
public class Room implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable=false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rooms_id", nullable = false)
    private Rooms rooms;

    @Column(name = "room_number", nullable = false)
    private Integer roomNumber;

    @OneToMany(mappedBy = "room", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<Dates> datesSet;

    @Column(name = "dates_version", nullable = false)
    private Long datesVersion;

    @Override
    public String toString() {
        return "Room{" +
                "id=" + id +
                ", rooms=" + (rooms == null ? null : rooms.getId()) +
                ", roomNumber=" + roomNumber +
                ", datesSet=" + "?" +
                ", datesVersion=" + datesVersion +
                '}';
    }
}
