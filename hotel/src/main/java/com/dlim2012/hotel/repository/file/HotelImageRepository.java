package com.dlim2012.hotel.repository.file;

import com.dlim2012.hotel.entity.file.HotelImage;
import com.dlim2012.hotel.entity.file.ImageType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HotelImageRepository extends JpaRepository<HotelImage, Integer> {
    boolean existsByHotelIdAndImageTypeAndId(Integer hotelId, ImageType imageType, Integer id);
    List<HotelImage> findByHotelIdAndImageType(Integer hotelId, ImageType imageType);
}
