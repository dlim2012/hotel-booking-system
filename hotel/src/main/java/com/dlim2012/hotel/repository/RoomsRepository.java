package com.dlim2012.hotel.repository;

import com.dlim2012.hotel.entity.Rooms;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoomsRepository extends JpaRepository<Rooms, Integer> {
    boolean existsByHotelIdAndId(Integer hotelId, Integer id);
    Optional<Rooms> findByHotelIdAndDisplayName(Integer hotelId, String displayName);
    Optional<Rooms> findByHotelIdAndId(Integer hotelId, Integer id);
    List<Rooms> findByHotelId(Integer hotelId);

    @Query("SELECT r.id from rooms r")
    List<Integer> findIdsByHotelId(Integer hotelId);

    @Query(
            value = "SELECT r FROM rooms r " +
                    "JOIN hotel h on r.hotel = h " +
                    "WHERE h.id = ?1 AND r.id = ?2 AND h.hotelManagerId = ?3"
    )
    Optional<Rooms> findByHotelIdAndIdAndHotelManagerId(Integer hotelId, Integer roomsId, Integer hotelManagerId);

    @Modifying
    @Transactional
    @Query(
            value = "DELETE r FROM rooms r " +
                    "JOIN hotel h on r.hotel_id = h.id " +
                    "WHERE h.id = ?1 AND r.id = ?2 AND h.hotel_manager_id = ?3",
            nativeQuery = true
    )
    void deleteByHotelIdAndIdAndHotelManagerId(Integer hotelId, Integer roomsId, Integer hotelManagerId);

    @Query(
            value = "SELECT r FROM rooms r " +
                    "JOIN hotel h on r.hotel = h " +
                    "WHERE h.id = ?1 AND r.id = ?2 AND h.hotelManagerId = ?3 AND r.isActive = ?4"
    )
    Optional<Rooms> findByHotelIdAndIdAndHotelManagerIdAndIsActive(Integer hotelId, Integer roomsId, Integer userId, boolean isActive);

    @Query(
            value = "SELECT r FROM rooms r " +
                    "JOIN hotel h on r.hotel = h " +
                    "WHERE h.id = ?1 AND h.hotelManagerId = ?2"
    )
    List<Rooms> findByHotelIdAndHotelManagerId(Integer hotelId, Integer userId);
}
