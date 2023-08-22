package com.dlim2012.clients.mysql_booking.repository;

import com.dlim2012.clients.mysql_booking.entity.Dates;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface DatesRepository extends JpaRepository<Dates, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Transactional
    @Query(
            value = "SELECT d FROM Dates d join d.room r " +
                    "WHERE r.id = ?1"
    )
    Set<Dates> findByRoomIdWithLock(Long roomId);


    @Query(
            value = "SELECT d FROM Dates d join d.room r " +
                    "WHERE r.rooms.id = ?1"
    )
    List<Dates> findByRoomsId(Integer roomsId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Transactional
    @Query(
            value = "SELECT d FROM Dates d join d.room r " +
                    "WHERE r.rooms.id = ?1"
    )
    Set<Dates> findByRoomsIdWithLock(Integer roomsId);


    @Query(
            value = "SELECT d.id as id, d.room_id as room_id, d.start_date as start_date, d.end_date as end_date " +
                    "FROM dates d " +
                    "JOIN room r ON d.room_id = r.id " +
                    "JOIN rooms rs ON r.rooms_id = rs.id " +
                    "WHERE rs.hotel_id = ?1"
            ,
            nativeQuery = true
    )
    List<Dates> findByHotelId(Integer hotelId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Transactional
    @Query(
            value = "SELECT d " +
                    "FROM Dates d " +
                    "JOIN room r ON d.room = r " +
                    "JOIN rooms rs ON r.rooms = rs " +
                    "WHERE rs.hotel.id = ?1 "
    )
    List<Dates> findByHotelIdWithLock(Integer hotelId);

    @Query(
            value = "SELECT d.id as id, d.room_id as room_id, d.start_date as start_date, d.end_date as end_date " +
                    "FROM dates d " +
                    "JOIN room r ON d.room_id = r.id " +
                    "JOIN rooms rs ON r.rooms_id = rs.id " +
                    "WHERE rs.hotel_id = ?1 AND d.start_date <= ?2 AND d.end_date >= ?3 " +
                    "FOR UPDATE"
            ,
            nativeQuery = true
    )
    Set<Dates> findByHotelIdAndDatesContainsWithLock(Integer hotelId, LocalDate startDate, LocalDate endDate);


    @Query(
            value = "SELECT d.id as id, d.room_id as room_id, d.start_date as start_date, d.end_date as end_date " +
                    "FROM dates d " +
                    "JOIN room r ON d.room_id = r.id " +
                    "JOIN rooms rs ON r.rooms_id = rs.id " +
                    "WHERE rs.hotel_id = ?1 AND (d.endDate > ?2 AND d.start_date < ?3) " +
                    "FOR UPDATE"
            ,
            nativeQuery = true
    )
    List<Dates> findByHotelIdAndDatesIncludes(Integer hotelId, LocalDate startDate, LocalDate endDate);

    @Query(
            value = "SELECT d.id as id, d.room_id as room_id, d.start_date as start_date, d.end_date as end_date " +
                    "FROM dates d " +
                    "JOIN room r ON d.room_id = r.id " +
                    "JOIN rooms rs ON r.rooms_id = rs.id " +
                    "WHERE rs.hotel_id = ?1 AND (d.end_date = ?2 OR d.start_date = ?3)" +
                    "FOR UPDATE"
            ,
            nativeQuery = true
    )
    Set<Dates> findByHotelIdAndMatchingDatesWithLock(Integer hotelId, LocalDate startDate, LocalDate endDate);

    @Query(
            value = "SELECT d.id as id, d.room_id as room_id, d.start_date as start_date, d.end_date as end_date " +
                    "FROM dates d " +
                    "JOIN room r ON d.room_id = r.id " +
                    "JOIN rooms rs ON r.rooms_id = rs.id " +
                    "WHERE rs.hotel_id = ?1 AND (d.end_date >= ?2 OR d.start_date <= ?3)" +
                    "FOR UPDATE"
            ,
            nativeQuery = true
    )
    Set<Dates> findByHotelIdAndIntersectDatesWithLock(Integer hotelId, LocalDate startDate, LocalDate endDate);

    @Transactional
    @Modifying
    @Query(
            value = "DELETE d FROM dates d " +
                    "JOIN room r ON d.room_id = r.id " +
                    "WHERE r.rooms_id = ?1",
            nativeQuery = true
    )
    void deleteByRoomsId(Integer roomsId);


    @Query(
            value = "SELECT d.id as id, d.room_id as room_id, d.start_date as start_date, d.end_date as end_date " +
                    "FROM dates d " +
                    "JOIN room r ON d.room_id = r.id " +
                    "JOIN rooms rs ON r.rooms_id = rs.id " +
                    "JOIN hotel h ON rs.hotel_id = h.id " +
                    "WHERE r.id = :roomId AND h.hotel_manager_id = :hotelManagerId " +
                    "FOR UPDATE"
            ,
            nativeQuery = true
    )
    Set<Dates> findByRoomIdAndHotelManagerIdWithLock(
            @Param("roomId") Long roomId,
            @Param("hotelManagerId") Integer hotelManagerId);


    @Query(
            value = "SELECT d.id as id, d.room_id as room_id, d.start_date as start_date, d.end_date as end_date " +
                    "FROM dates d " +
                    "JOIN room r ON d.room_id = r.id " +
                    "JOIN rooms rs ON r.rooms_id = rs.id " +
                    "JOIN hotel h ON rs.hotel_id = h.id " +
                    "WHERE r.id = :roomId AND h.user_id = :userId " +
                    "FOR UPDATE"
            ,
            nativeQuery = true
    )
    Set<Dates> findByRoomIdAndUserIdWithLock(
            @Param("roomId") Long roomId,
            @Param("userId") Integer userId);

    @Query(
            value = "SELECT d.id as id, d.room_id as room_id, d.start_date as start_date, d.end_date as end_date " +
                    "FROM dates d " +
                    "JOIN room r ON d.room_id = r.id " +
                    "JOIN rooms rs ON r.rooms_id = rs.id " +
                    "JOIN hotel h ON rs.hotel_id = h.id " +
                    "WHERE d.id = :datesId AND h.hotel_manager_id = :hotelManagerId " +
                    "FOR UPDATE"
            ,
            nativeQuery = true
    )
    Optional<Dates> findByIdAndHotelManagerIdWithLock(
            @Param("datesId") Long datesId,
            @Param("hotelManagerId") Integer hotelManagerId);

    @Modifying
    @Transactional
    @Query(
            value = "DELETE d FROM dates d " +
                    "JOIN room r ON d.room_id = r.id " +
                    "JOIN rooms rs ON r.rooms_id = rs.id " +
                    "JOIN hotel h ON rs.hotel_id = h.id " +
                    "WHERE d.id = :datesId AND h.hotel_manager_id = :hotelManagerId",
            nativeQuery = true
    )
    Integer deleteByIDAndHotelManagerId(
            @Param("datesId") Long datesId,
            @Param("hotelManagerId") Integer hotelManagerId);



    @Query(
            value = "SELECT d.id as id, d.room_id as room_id, d.start_date as start_date, d.end_date as end_date " +
                    "FROM dates d " +
                    "JOIN room r ON d.room_id = r.id " +
                    "JOIN rooms rs ON r.rooms_id = rs.id " +
                    "WHERE rs.hotel_id >= :i1 AND rs.hotel_id <= :i2 ",
            nativeQuery = true
    )
    List<Dates> findByHotelIdRange(int hotelStartId, int hotelEndId);

//    @Transactional
//    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query(
            value = "SELECT d.id as id, d.room_id as room_id, d.start_date as start_date, d.end_date as end_date " +
                    "FROM dates d " +
                    "JOIN room r ON d.room_id = r.id " +
                    "JOIN rooms rs ON r.rooms_id = rs.id " +
                    "WHERE rs.hotel_id >= :i1 AND rs.hotel_id < :i2 " +
                    "FOR UPDATE",
            nativeQuery = true
    )
    Set<Dates> findByHotelIdRangeWithLock(
            @Param("i1") int i1,
            @Param("i2") int i2
    );

    @Modifying
    @Transactional
    @Query(
            value = "DELETE d FROM dates d " +
                    "JOIN room r ON d.room_id = r.id " +
                    "JOIN rooms rs ON r.rooms_id = rs.id " +
                    "WHERE rs.hotel_id = :hotelId",
            nativeQuery = true
    )
    void deleteByHotelId(
            @Param("hotelId") Integer hotelId);

    @Query(
            value = "SELECT d " +
                    "FROM Dates d " +
                    "WHERE d.room.id in ?1 "
    )
    List<Dates> findByRoomIds(Set<Long> roomIds);



//    @Transactional
//    @Lock(LockModeType.PESSIMISTIC_WRITE)
//    @Query(
//            value = "SELECT h.id as hotelId, rs.id as roomsId, r.id as roomId, d.id as id, d.room.id as roomId, d.startDate as startDate, d.endDate as endDate " +
//                    "FROM Dates d " +
//                    "JOIN Room r ON d.room = r " +
//                    "JOIN Rooms rs ON r.rooms = rs " +
//                    "JOIN Hotel h ON rs.hotel = h " +
//                    "WHERE h.id >= :i1 AND h.id < :i2 AND (d.startDate <= :startDate OR d.endDate = :endDate)"
//    )
//    List<DatesWithIds> findByHotelIdRangeAndDatesCondition(
//            @Param("i1") int i1,
//            @Param("i2") int i2,
//            @Param("startDate") LocalDate startDate,
//            @Param("endDate") LocalDate endDate
//            );
}
