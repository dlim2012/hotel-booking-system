package com.dlim2012.hotel.entity.file;

import com.dlim2012.hotel.entity.Rooms;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "rooms_image")
public class RoomsImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rooms_id", nullable = false)
    private Rooms rooms;

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
        return "RoomsImage{" +
                "id=" + id +
                ", rooms=" + (rooms == null ? null : rooms.getId()) +
                ", imageType=" + imageType +
                ", contentType='" + contentType + '\'' +
                ", name='" + name + '\'' +
                ", filePath='" + filePath + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
