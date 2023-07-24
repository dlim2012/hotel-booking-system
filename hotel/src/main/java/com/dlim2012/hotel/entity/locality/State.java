package com.dlim2012.hotel.entity.locality;


import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "state")
public class State {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "initials", length = 10)
    private String initials;

    @Column(name = "area_code", length = 10)
    private String areaCode;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "country_id", nullable = false)
    private Country country;

    @OneToMany(mappedBy = "state", fetch = FetchType.LAZY)
    private List<City> cities = new ArrayList<>();

    @Override
    public String toString() {
        return "State{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", initials='" + initials + '\'' +
                ", areaCode='" + areaCode + '\'' +
                ", country=" + country +
                '}';
    }
}
