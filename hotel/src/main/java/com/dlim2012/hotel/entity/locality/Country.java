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
@Entity(name = "country")
@Table(name = "country")
public class Country {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)

    private Integer id;

    @Column(name = "name", length = 50, unique = true, nullable = false)
    private String name;

    @Column(name = "initials", length = 3)
    private String initials;

    @OneToMany(mappedBy = "country", fetch = FetchType.LAZY)
    private List<State> states = new ArrayList<>();

    @Override
    public String toString() {
        return "Country{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", initials='" + initials + '\'' +
                '}';
    }
}
