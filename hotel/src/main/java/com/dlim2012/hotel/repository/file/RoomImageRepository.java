package com.dlim2012.hotel.repository.file;

import com.dlim2012.hotel.entity.file.ImageType;
import com.dlim2012.hotel.entity.file.RoomsImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoomImageRepository extends JpaRepository<RoomsImage, Integer> {
    boolean existsByRoomsIdAndImageTypeAndId(Integer roomsId, ImageType imageType, Integer id);
    List<RoomsImage> findByRoomsId(Integer roomsId);
    Optional<RoomsImage> findByRoomsIdAndId(Integer roomsId, Integer roomsImageId);
    List<RoomsImage> findByRoomsIdAndImageType(Integer roomsId, ImageType imageType);
    Optional<RoomsImage> findByRoomsIdAndIdAndImageType(Integer roomsId, Integer roomsImageId, ImageType imageType);
}
