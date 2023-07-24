package com.dlim2012.clients.mysql_booking.repository;

import com.dlim2012.clients.mysql_booking.entity.BookingRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;

public interface BookingRoomRepository extends JpaRepository<BookingRoom, Long> {

    @Query(
            value = "SELECT br FROM BookingRoom br " +
                    "JOIN BookingRooms brs ON br.bookingRooms = brs " +
                    "JOIN Booking b ON brs.booking = b " +
                    "WHERE br.id = ?1 AND b.id =?2 AND b.userId = ?3"
    )
    Optional<BookingRoom> findByIdAndBookingIdAndUserId(Long bookingRoomId, Long bookingId, Integer userId);

    @Query(
            value = "SELECT br.id, br.booking_rooms_id, br.room_id, br.guest_name, br.guest_email " +
                    "FROM booking_room br " +
                    "JOIN booking_rooms brs ON br.booking_rooms_id = brs.id " +
                    "WHERE brs.booking_id = ?1",
            nativeQuery = true
    )
    Set<BookingRoom> findRoomIdsByBookingId(Long bookingId);

    @Modifying
    @Transactional
    @Query(
            value = "DELETE br FROM booking_room br " +
                    "JOIN booking_rooms brs ON br.booking_rooms_id = brs.id " +
                    "JOIN booking b ON brs.booking_id = b.id " +
                    "WHERE br.id = :bookingRoomId AND b.hotel_manager_id = :hotelManagerId",
            nativeQuery = true
    )
    void deleteByIdAndHotelManagerId(
            @Param("bookingRoomId") Long bookingRoomId,
            @Param("hotelManagerId") Integer hotelManagerId);


    @Query(
            value = "SELECT br.* FROM booking_room br " +
                    "JOIN booking_rooms brs ON br.booking_rooms_id = brs.id " +
                    "JOIN booking b ON brs.booking_id = b.id " +
                    "WHERE br.id = :bookingRoomId AND b.hotel_manager_id = :hotelManagerId",
            nativeQuery = true
    )
    Optional<BookingRoom> findByIdAndHotelManagerId(
            @Param("bookingRoomId") Long bookingRoomId,
            @Param("hotelManagerId") Integer hotelManagerId);

    @Query(
            value = "SELECT br.* FROM booking_room br " +
                    "JOIN booking_rooms brs ON br.booking_rooms_id = brs.id " +
                    "JOIN booking b ON brs.booking_id = b.id " +
                    "WHERE br.id = :bookingRoomId AND b.hotel_manager_id = :hotelManagerId " +
                    "WITH LOCK"
                    ,
            nativeQuery = true
    )
    Optional<BookingRoom> findByIdAndHotelManagerIdWithLock(
            @Param("bookingRoomId") Long bookingRoomId,
            @Param("hotelManagerId") Integer hotelManagerId);

}
