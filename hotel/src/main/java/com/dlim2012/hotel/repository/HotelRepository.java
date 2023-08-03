package com.dlim2012.hotel.repository;

import com.dlim2012.hotel.dto.query.HotelIsActiveQuery;
import com.dlim2012.hotel.entity.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface HotelRepository extends JpaRepository<Hotel, Integer> {
    boolean existsByLocalityId(Integer localityId);

    boolean existsByIdAndHotelManagerId(Integer id, Integer userId);

    boolean existsByNameAndDescriptionAndLocalityId (String name, String description, Integer localityId);

    List<Hotel> findByHotelManagerId(Integer userId);

    boolean existsByNameAndHotelManagerId(String name, Integer userId);

    Optional<Hotel> findByIdAndHotelManagerId(Integer hotelId, Integer userId);

    Optional<Hotel> findByIdAndHotelManagerIdAndIsActive(Integer hotelId, Integer userId, boolean b);

    @Transactional
    @Modifying
    void deleteByHotelManagerId(Integer userId);

    @Query(
            value = "SELECT h.* FROM hotel h " +
                    "JOIN saved_user u ON h.id = u.hotel_id",
            nativeQuery = true
    )
    List<Hotel> findBySavedUserId(Integer userId);

    @Query(
            value = "SELECT h.is_active as isActive,  sum(if (r.is_active IS TRUE, 1, 0)) as activeRoomsCount "+
                    "FROM hotel h " +
                    "JOIN rooms r ON h.id = r.hotel_id " +
                    "WHERE h.id = ?1 AND h.hotel_manager_id = ?2",
            nativeQuery = true
    )
    HotelIsActiveQuery findIsActiveInfo(Integer hotelId, Integer userId);

}
