package com.dlim2012.hotel.repository;

import com.dlim2012.hotel.entity.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HotelRepository extends JpaRepository<Hotel, Integer> {
    boolean existsByLocalityId(Integer localityId);

    boolean existsByNameAndDescriptionAndLocalityId (String name, String description, Integer localityId);

}
