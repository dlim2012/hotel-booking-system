package com.dlim2012.clients.mysql_booking.repository;

import com.dlim2012.clients.mysql_booking.dto.RoomDatesUpdateInfo;
import com.dlim2012.clients.mysql_booking.entity.Room;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface RoomRepository extends JpaRepository<Room, Long> {


    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Transactional
    @Query(
            value = "SELECT r FROM Room r WHERE r.id = ?1"
    )
    Optional<Room> findByIdWithLock(Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Transactional
    @Query(
            value = "SELECT r FROM Room r " +
                    "JOIN Rooms rs ON r.rooms = rs " +
                    "JOIN Hotel h ON rs.hotel = h " +
                    "WHERE h.id = :hotelId AND h.hotelManagerId = :hotelManagerId"
    )
    Optional<Room> findByIdAndHotelIdAndHotelManagerIdWithLock(Long id, Integer hotelId, Integer hotelMangaerId);

    @Query(
            value = "SELECT r.id, r.rooms_id FROM room r " +
                    "JOIN rooms rs ON r.rooms_id = rs.id " +
                    "WHERE rs.hotel_id = ?1",
            nativeQuery = true
    )
    List<Room> findByHotelId(Integer hotelId);



    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Transactional
    @Query(
            value = "SELECT r FROM Room r " +
                    "JOIN Rooms rs ON r.rooms = rs " +
                    "WHERE rs.hotel.id = ?1"
    )
    List<Room> findByHotelIdWithLock(Integer hotelId);


    List<Room> findByRoomsId(Integer roomsId);



    @Query(
            value = "SELECT r.id as id, r.rooms_id as rooms_id, r.room_number as room_number " +
                    "FROM room r " +
                    "JOIN rooms rs ON r.rooms_id = rs.id " +
                    "JOIN hotel h ON rs.hotel_id = h.id " +
                    "WHERE d.id = :roomId AND h.hotel_manager_id = :hotelManagerId " +
                    "FOR UPDATE"
            ,
            nativeQuery = true
    )
    Optional<Room> findByIdAndHotelManagerId(
            @Param("roomId") Long roomId,
            @Param("hotelManagerId") Integer hotelManagerId);

    @Query(
            value = "SELECT r.id AS roomId, rs.id AS roomsId, rs.datesAddedUntil AS datesAddedUntil, h.id AS hotelId " +
                    "FROM Room r " +
                    "JOIN Rooms rs ON r.rooms = rs " +
                    "JOIN Hotel h ON rs.hotel = h " +
                    "WHERE h.id >= :idStart AND h.id < :idEnd"
    )
    List<RoomDatesUpdateInfo> findIdsByHotelIdRange(
            @Param("idStart") int idStart,
            @Param("idEnd") int idEnd);
}
