package com.dlim2012.hotel.entity.facility;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "facility")
public class Facility {

    @Id
//    @GeneratedValue(strategy = GenerationType.TABLE)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "display_name", nullable = false)
    private String displayName;

    @Override
    public String toString() {
        return "Facility{" +
                "id=" + id +
                ", displayName='" + displayName + '\'' +
                '}';
    }
}
