package com.dlim2012.hotel.entity.locality;

import com.dlim2012.hotel.entity.Hotel;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "locality")
public class Locality {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "zipcode", length = 20, nullable = false)
    private String zipcode;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "city_id", nullable = false)
    private City city;

    @OneToMany(mappedBy = "locality", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Hotel> hotels;

    @Override
    public String toString() {
        return "Locality{" +
                "id=" + id +
                ", zipcode='" + zipcode + '\'' +
                ", city=" + city +
                '}';
    }
}
