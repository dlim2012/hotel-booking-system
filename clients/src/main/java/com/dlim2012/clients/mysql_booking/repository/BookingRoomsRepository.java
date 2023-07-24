package com.dlim2012.clients.mysql_booking.repository;

import com.dlim2012.clients.mysql_booking.entity.BookingRooms;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BookingRoomsRepository extends JpaRepository<BookingRooms, Long> {
    List<BookingRooms> findByRoomsId(Integer roomsId);

//    @Query(
//            value = "SELECT EXISTS(" +
//                    "SELECT brs.id FROM booking_rooms brs " +
//                    "JOIN booking b ON brs.booking_id = b.id " +
//                    "WHERE brs.id = :bookingRoomsId AND b.hotel_id = :hotelId AND b.hotel_manager_id = :hotelManagerId)",
//            nativeQuery = true
//    )
//    Long existsByIdAndHotelManagerId(
//            @Param("bookingRoomsId") Long bookingRoomsId,
//            @Param("hotelId") Integer hotelId,
//            @Param("hotelManagerId") Integer hotelManagerId);

    @Query(
            value = "SELECT brs.id as id, brs.booking_id as booking_id, brs.rooms_id as rooms_id, brs.rooms_display_name as rooms_display_name, brs.rooms_short_name as rooms_short_name FROM booking_rooms brs " +
                    "JOIN booking b ON brs.booking_id = b.id " +
                    "WHERE brs.id = :bookingRoomsId AND b.hotel_id = :hotelId AND b.hotel_manager_id = :hotelManagerId",
            nativeQuery = true
    )
    Optional<BookingRooms> findByIdAndHotelManagerId(
            @Param("bookingRoomsId") Long bookingRoomsId,
            @Param("hotelId") Integer hotelId,
            @Param("hotelManagerId") Integer hotelManagerId);

    @Query(
            value = "SELECT count(*) FROM booking_rooms br " +
                    "JOIN booking b ON b.id = br.booking_id " +
                    "WHERE br.id = :booking_rooms_id AND b.hotel_manager_id = :hotel_manager_id",
            nativeQuery = true
    )
    Long existsByIdAndHotelManagerId(
            @Param("booking_rooms_id") Long bookingRoomsId,
            @Param("hotel_manager_id") Integer hotelManagerId);
}

