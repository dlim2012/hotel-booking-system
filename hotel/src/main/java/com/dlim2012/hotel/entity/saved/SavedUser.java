package com.dlim2012.hotel.entity.saved;

import com.dlim2012.hotel.entity.Hotel;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "saved_user")
public class SavedUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_id", nullable = false)
    private Hotel hotel;

    @Column(name = "user_id")
    private Integer userId;

    @Override
    public String toString() {
        return "SavedUser{" +
                "id=" + id +
                ", hotel=" + (hotel == null ? null : hotel.getId()) +
                ", userId=" + userId +
                '}';
    }
}
