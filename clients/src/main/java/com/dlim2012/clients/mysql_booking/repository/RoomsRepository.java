package com.dlim2012.clients.mysql_booking.repository;

import com.dlim2012.clients.mysql_booking.entity.Rooms;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

public interface RoomsRepository extends JpaRepository<Rooms, Integer> {

    Optional<Rooms> findByHotelIdAndId(Integer hotelId, Integer roomsId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Transactional
    @Query(
            value = "SELECT rs FROM Rooms rs " +
                    "WHERE rs.id = ?1"
    )
    Optional<Rooms> findByIdWithLock(Integer roomsId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Transactional
    @Query(
            value = "SELECT rs FROM Rooms rs " +
                    "WHERE rs.hotel.id = ?1"
    )
    List<Rooms> findByHotelIdWithLock(Integer hotelId);


    Set<Rooms> findByHotelId(Integer hotelId);


//    @Lock(LockModeType.PESSIMISTIC_READ)
//    @Transactional
//    @Query("SELECT ra FROM Room ra")
//    List<Room> findAllWithLock();

    @Query("SELECT r from Room r")
    Stream<Rooms> findAllAsStream();


    void deleteByHotelId(Integer hotelId);

    @Query(
            value = "SELECT r FROM Rooms r "+
                    "JOIN Hotel h ON h.id = r.hotel_id " +
                    "WHERE r.id = :roomsId AND h.hotel_manager_id = :hotelManagerId",
            nativeQuery = true
    )
    Optional<Rooms> findByIdAndHotelManagerId(
            @Param("roomsId") Long roomsId,
            @Param("hotelManagerId") Integer hotelManagerId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Transactional
    @Query(
            value = "SELECT r FROM Rooms r " +
                    "WHERE r.hotel.id >= :i1 AND r.hotel.id < :i2"
    )
    Set<Rooms> findByHotelIdRangeWithLock(
            @Param("i1") int i1,
            @Param("i2") int i2
    );

    @Query(
            value = "SELECT rs FROM Rooms rs " +
                    "JOIN Room r ON r.rooms = rs " +
                    "WHERE r.id = :roomId"
    )
    Rooms findByRoomId(
            @Param("roomId") Long roomId
    );
}
