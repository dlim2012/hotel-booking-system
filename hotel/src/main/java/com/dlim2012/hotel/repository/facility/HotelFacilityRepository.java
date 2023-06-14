package com.dlim2012.hotel.repository.facility;

import com.dlim2012.hotel.entity.facility.HotelFacility;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HotelFacilityRepository extends JpaRepository<HotelFacility, Integer> {
    List<HotelFacility> findByHotelId(Integer hotelId);
}
