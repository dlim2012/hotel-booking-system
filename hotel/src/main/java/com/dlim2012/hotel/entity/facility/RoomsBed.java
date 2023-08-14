package com.dlim2012.hotel.entity.facility;

import com.dlim2012.clients.entity.Bed;
import com.dlim2012.hotel.entity.Rooms;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "rooms_bed")
public class RoomsBed {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rooms_id", nullable = false)
    private Rooms rooms;

    @Enumerated(EnumType.ORDINAL)
    private Bed bed;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Override
    public String toString() {
        return "RoomsBed{" +
                "id=" + id +
                ", rooms=" + (rooms == null ? null : rooms.getId()) +
                ", bed=" + bed +
                ", quantity=" + quantity +
                '}';
    }
}
