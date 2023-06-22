package com.dlim2012.clients.mysql_booking.repository;

import com.dlim2012.clients.mysql_booking.entity.AvailableRoom;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AvailableRoomRepository extends JpaRepository<AvailableRoom, Long> {
    Optional<AvailableRoom> findByRoomIdAndDate(Integer roomId, LocalDate date);

    List<AvailableRoom> findByRoomIdAndDateBetween(Integer roomId, LocalDate startDate, LocalDate endDate);

    @Lock(LockModeType.PESSIMISTIC_READ)
    @Transactional
    @Query(value = "SELECT ar FROM AvailableRoom ar" +
            " WHERE ar.roomId = ?1" +
            " AND ar.date BETWEEN ?2 AND ?3" +
            " AND ar.availableQuantity >= ?4")
    List<AvailableRoom> findByRoomIdAndDateBetweenAndAvailableQuantity(
            Integer roomId, LocalDate startDate, LocalDate endDate, Long quantity);

    @Lock(LockModeType.PESSIMISTIC_READ)
    @Transactional
    @Query(value = "SELECT ar FROM AvailableRoom ar" +
            " WHERE ar.roomId = ?1")
    List<AvailableRoom> findByRoomId(
            Integer roomId
    );

    @Modifying
    @Transactional
    @Query(value = "" +
            " WITH RECURSIVE cte AS (SELECT * FROM available_room ar" +
            " WHERE ar.room_id = :roomId" +
            " AND ar.date BETWEEN :startDate AND :endDate" +
            " AND ar.available_quantity >= :quantity)" +
            " UPDATE available_room ar" +
            " INNER JOIN cte ON ar.id = cte.id" +
            " SET ar.available_quantity = IF(" +
            "   (SELECT count(*) FROM cte) = DATEDIFF(:endDate, :startDate) + 1," +
            "   ar.available_quantity - :quantity," +
            "   ar.available_quantity" +
            " )"
            , nativeQuery = true
    )
    Integer conditionalDecreaseQuantityByRoomIdAndDateBetween(
            @Param("roomId") Integer roomId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("quantity") Integer quantity
    );

    @Modifying
    @Transactional
    @Query(value = "" +
            " UPDATE available_room ar" +
            " SET ar.available_quantity = ar.available_quantity + :quantity" +
            " WHERE ar.room_id = :roomId" +
            "   AND ar.date BETWEEN :startDate AND :endDate" +
            "   AND ar.available_quantity >= :quantity"
            , nativeQuery = true
    )
    Integer increaseQuantityByRoomIdAndDateBetween(
            @Param("roomId") Integer roomId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("quantity") Integer quantity
    );

    @Modifying
    @Transactional
    @Query(value = "" +
            " INSERT INTO available_room ar (room_id, date, initial_quantity, available_quantity)" +
            " SELECT r.room_id, :date, r.quantity, r.quantity" +
            " FROM room r"
            , nativeQuery = true
    )
    Integer insertRoomAvailabilitiesByDate(LocalDate date);

//    @Modifying
//    @Query(value = "WITH cte AS (" +
//            "   SELECT ar.id, count(*) AS count FROM available_room ar" +
//            "   WHERE ar.room_id = :roomId" +
//            "     AND ar.date BETWEEN :startDate AND :endDate" +
//            "     AND ar.available_quantity > :quantity" +
//            " )" +
//            " UPDATE available_room ar" +
//            " SET ar.quantity = IF (" +
//            "    cte.count == DATEDIFF(:endDate, :startDate) + 1," +
//            "    ar.quantity - :quantity" +
//            "    ar.quantity" +
//            " )" +
//            " FROM ar INNER JOIN cte ON ar.id = cte.id")
//    Integer decreaseQuantityByRoomIdAndDateBetween(
//            Integer roomId, LocalDate startDate, LocalDate endDate, Integer quantity
//    );


    @Query(value = "WITH cte AS (SELECT * FROM available_room ar" +
            " WHERE ar.room_id = :roomId" +
            " AND ar.date BETWEEN :startDate AND :endDate" +
            " AND ar.available_quantity >= :quantity)" +
            " SELECT IF ((SELECT count(*) FROM cte) = DATEDIFF(:endDate, :startDate) + 1," +
            " room_id, -10) FROM cte", nativeQuery = true)
    Long findByRoomIdAndDateBetweenAndAvailableQuantityIfValid(
            Long roomId, LocalDate startDate, LocalDate endDate, Integer quantity);
}
