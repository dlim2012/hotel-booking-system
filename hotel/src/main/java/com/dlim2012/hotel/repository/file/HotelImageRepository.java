package com.dlim2012.hotel.repository.file;

import com.dlim2012.hotel.entity.file.HotelImage;
import com.dlim2012.hotel.entity.file.ImageType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HotelImageRepository extends JpaRepository<HotelImage, Integer> {
    boolean existsByHotelIdAndImageTypeAndId(Integer hotelId, ImageType imageType, Integer id);
    boolean existsByHotelIdAndName(Integer hotelId, String name);
    List<HotelImage> findByHotelId(Integer hotelId);
    Optional<HotelImage> findByHotelIdAndId(Integer hotelId, Integer Id);
    List<HotelImage> findByHotelIdAndImageType(Integer hotelId, ImageType imageType);
    Optional<HotelImage> findByHotelIdAndIdAndImageType(Integer hotelId, Integer Id, ImageType imageType);
    Optional<HotelImage> findByIdAndImageType(Integer Id, ImageType imageType);
}
