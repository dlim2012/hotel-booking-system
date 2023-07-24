package com.dlim2012.hotel.entity.facility;

import com.dlim2012.hotel.entity.Rooms;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "rooms_facility")
public class RoomsFacility {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rooms_id", nullable = false)
    private Rooms rooms;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "facility_id", nullable = false)
    private Facility facility;

    @Column(name = "is_active", nullable = false)
    private boolean isActive;

    @Override
    public String toString() {
        return "RoomsFacility{" +
                "id=" + id +
                ", rooms=" + (rooms == null ? null : rooms.getId()) +
                ", facility=" + facility +
                ", isActive=" + isActive +
                '}';
    }
}
