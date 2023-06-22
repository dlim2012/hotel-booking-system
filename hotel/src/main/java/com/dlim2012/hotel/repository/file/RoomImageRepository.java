package com.dlim2012.hotel.repository.file;

import com.dlim2012.hotel.entity.file.ImageType;
import com.dlim2012.hotel.entity.file.RoomImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoomImageRepository extends JpaRepository<RoomImage, Integer> {
    boolean existsByRoomIdAndImageTypeAndId(Integer roomId, ImageType imageType, Integer id);
    List<RoomImage> findByRoomId(Integer roomId);
    Optional<RoomImage> findByRoomIdAndId(Integer roomId, Integer roomImageId);
    List<RoomImage> findByRoomIdAndImageType(Integer roomId, ImageType imageType);
    Optional<RoomImage> findByRoomIdAndIdAndImageType(Integer roomId, Integer roomImageId, ImageType imageType);
}
