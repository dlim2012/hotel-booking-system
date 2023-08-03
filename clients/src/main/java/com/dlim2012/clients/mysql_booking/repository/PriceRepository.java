package com.dlim2012.clients.mysql_booking.repository;

import com.dlim2012.clients.mysql_booking.entity.Price;
import com.dlim2012.clients.mysql_booking.entity.Rooms;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface PriceRepository  extends JpaRepository<Price, Long> {
    Optional<Price> findByRoomsAndDate(Rooms rooms, LocalDate date);


    @Query(
            value = "SELECT p.id, p.rooms_id, p.date, p.price_in_cents FROM price p " +
                    "JOIN rooms rs ON p.rooms_id = rs.id " +
                    "WHERE rs.hotel_id = ?1 AND p.date >= ?2 AND p.date < ?3",
            nativeQuery = true
    )
    Set<Price> findByHotelIdAndDates(Integer hotelId, LocalDate startDate, LocalDate endDate);

    @Transactional
    @Modifying
    void deleteAllByRoomsId(Integer roomsId);

    @Query(
            value = "SELECT p FROM Price p "+
                    "WHERE p.rooms.id = ?1"
    )
    List<Price> findByRoomsId(Integer roomsId);

    @Transactional
    @Modifying
    @Query(
            value = "DELETE p FROM price p " +
                    "JOIN rooms rs ON p.rooms_id = rs.id " +
                    "WHERE rs.hotel_id >= ?1 AND rs.hotel_id < ?2",
            nativeQuery = true
    )
    void deleteAllByHotelIdRange(int hotelStartId, int hotelEndId);

    @Transactional
    @Modifying
    @Query(
            value = "DELETE p FROM price p " +
                    "WHERE p.date < ?1 OR p.date >= ?2",
            nativeQuery = true
    )
    void deleteByOutOfDateRange(LocalDate startDate, LocalDate endDate);


    @Transactional
    @Modifying
    @Query(
            value = "DELETE p FROM price p " +
                    "JOIN rooms rs ON p.rooms_id = rs.id " +
                    "WHERE rs.hotel_id >= ?1 AND rs.hotel_id < ?2 AND p.date < ?3",
            nativeQuery = true
    )
    void deleteByHotelIdRangeAndMaxDate(int hotelStartId, int hotelEndId, LocalDate minBookingDate);

    @Transactional
    @Modifying
    @Query(
            value = "DELETE p FROM price p " +
                    "JOIN rooms rs ON p.rooms_id = rs.id " +
                    "WHERE p.date < ?1",
            nativeQuery = true
    )
    void deleteByMaxDate(LocalDate minBookingDate);


    @Transactional
    @Modifying
    @Query(
            value = "DELETE p FROM price p " +
                    "JOIN rooms rs ON p.rooms_id = rs.id " +
                    "WHERE rs.hotel_id = ?1",
            nativeQuery = true
    )
    void deleteByHotelId(Integer hotelId);
}
