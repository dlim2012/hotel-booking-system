package com.dlim2012.clients.mysql_booking.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "room")
public class Room {

    @Id
    @Column(name = "room_id", nullable = false)
    private Integer roomId;

    @Column(name = "hotel_id", nullable = false)
    private Integer hotelId;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "price_min", nullable = false)
    private Double priceMin;

    @Column(name = "price_max", nullable = false)
    private Double priceMax;

}
