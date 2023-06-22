package com.dlim2012.clients.mysql_booking.repository;

import com.dlim2012.clients.mysql_booking.entity.Room;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface RoomRepository extends JpaRepository<Room, Integer> {

    Optional<Room> findByHotelIdAndRoomId(Integer hotelId, Integer roomId);

    @Lock(LockModeType.PESSIMISTIC_READ)
    @Transactional
    @Query("SELECT ra FROM Room ra")
    List<Room> findAllWithLock();


}
