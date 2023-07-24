package com.dlim2012.hotel.repository.facility;

import com.dlim2012.hotel.entity.facility.RoomsBed;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomsBedRepository extends JpaRepository<RoomsBed, Integer> {
}
