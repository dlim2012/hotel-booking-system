package com.dlim2012.hotel.repository.facility;

import com.dlim2012.hotel.entity.facility.RoomsFacility;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface RoomFacilityRepository extends JpaRepository<RoomsFacility, Integer> {
    boolean existsByFacilityId(Integer facilityId);
    boolean existsByRoomsIdAndFacilityId(Integer roomsId, Integer facilityId);
    Set<RoomsFacility> findByRoomsId(Integer roomsId);
    void deleteByRoomsId(Integer roomsId);

    @Query(
            value = "SELECT rf FROM RoomsFacility rf " +
                    "JOIN rooms r ON rf.rooms = r " +
                    "JOIN hotel h ON r.hotel = h " +
                    "WHERE h.id = ?1 AND r.id = ?2 AND h.hotelManagerId = ?3"
    )
    Set<RoomsFacility> findByHotelIdAndRoomsIdAndHotelManagerId(
            Integer hotelId,
            Integer roomsId,
            Integer hotelManagerId
    );
}
