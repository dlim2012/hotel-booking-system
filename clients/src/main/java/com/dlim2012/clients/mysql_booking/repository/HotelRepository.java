package com.dlim2012.clients.mysql_booking.repository;

import com.dlim2012.clients.mysql_booking.entity.Hotel;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface HotelRepository extends JpaRepository<Hotel, Integer> {
    @Transactional
    void deleteByHotelManagerId(Integer hotelManagerId);

    Optional<Hotel> findByIdAndHotelManagerId(Integer hotelId, Integer userId);

    @Query(
            value = "SELECT max(id) FROM hotel h",
            nativeQuery = true
    )
    Integer findMaxId();

    List<Hotel> findByIdGreaterThanEqualAndIdLessThan(int i, int i1);

    @Transactional
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query(
            value = "SELECT h FROM Hotel h WHERE h.id >= :i1 AND h.id < :i2"
    )
    List<Hotel> findByIdGreaterThanEqualAndIdLessThanWithLock(
            @Param("i1") int i1,
            @Param("i2") int i2);

}
