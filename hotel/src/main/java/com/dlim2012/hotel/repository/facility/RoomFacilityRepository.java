package com.dlim2012.hotel.repository.facility;

import com.dlim2012.hotel.entity.facility.RoomFacility;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomFacilityRepository extends JpaRepository<RoomFacility, Integer> {
    boolean existsByFacilityId(Integer facilityId);
    List<RoomFacility> findByRoomId(Integer roomId);
    void deleteByRoomId(Integer roomId);
}
