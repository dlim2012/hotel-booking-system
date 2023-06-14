package com.dlim2012.hotel.repository;

import com.dlim2012.hotel.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<Room, Integer> {
    boolean existsByHotelIdAndDisplayName(Integer hotelId, String displayName);
    boolean existsByHotelIdAndId(Integer hotelId, Integer id);
    Optional<Room> findByHotelIdAndDisplayName(Integer hotelId, String displayName);
    Optional<Room> findByHotelIdAndId(Integer hotelId, Integer id);
    List<Room> findByHotelId(Integer hotelId);
}
