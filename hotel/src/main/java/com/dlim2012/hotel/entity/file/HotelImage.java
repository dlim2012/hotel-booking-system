package com.dlim2012.hotel.entity.file;

import com.dlim2012.hotel.entity.Hotel;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "hotel_image")
public class HotelImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_id", nullable = false)
    private Hotel hotel;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "image_type")
    private ImageType imageType;

    @Column(name = "content_type")
    private String contentType;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "file_path", nullable=false)
    private String filePath;

    @Column(name = "url")
    private String url;

    @Override
    public String toString() {
        return "HotelImage{" +
                "id=" + id +
                ", hotel=" + (hotel == null ? null : hotel.getId()) +
                ", imageType=" + imageType +
                ", contentType='" + contentType + '\'' +
                ", name='" + name + '\'' +
                ", filePath='" + filePath + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
