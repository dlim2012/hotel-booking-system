package com.dlim2012.clients.mysql_booking.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "dates")
public class Dates {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable=false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private Room room;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Override
    public String toString() {
        return "Dates{" +
                "id=" + id +
                ", room=" + (room == null ? null : room.getId()) +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                '}';
    }
}
