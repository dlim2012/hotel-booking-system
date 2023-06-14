package com.dlim2012.hotel.repository.file;

import com.dlim2012.hotel.entity.file.ImageType;
import com.dlim2012.hotel.entity.file.RoomImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomImageRepository extends JpaRepository<RoomImage, Integer> {
    boolean existsByRoomIdAndImageTypeAndId(Integer roomId, ImageType imageType, Integer id);
    List<RoomImage> findByRoomIdAndImageType(Integer roomId, ImageType imageType);
}
