package com.dlim2012.clients.mysql_booking.repository;

import com.dlim2012.clients.entity.BookingMainStatus;
import com.dlim2012.clients.entity.BookingStatus;
import com.dlim2012.clients.mysql_booking.entity.Booking;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {


    List<Booking> findByHotelId(Integer hotelId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Transactional
    @Query("SELECT b FROM Booking b WHERE b.id = ?1")
    Optional<Booking> findByIdWithLock(Long bookingId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Transactional
    @Query("SELECT b FROM Booking b WHERE b.id = ?1 AND b.hotelManagerId = ?2")
    Optional<Booking> findByIdAndHotelManagerIdWithLock(Long bookingId, Integer hotelManagerId);

//    @Query("SELECT b FROM Booking b" +
//            " WHERE b.roomId = :roomId " +
//            "   AND DATE(b.startDateTime) <= :date AND :date <= DATE(b.endDateTime)" +
//            "   AND b.status = :bookingStatus" +
//            " ORDER BY b.invoiceConfirmTime desc")
//    List<Booking> findByRoomIdAndDateAndStatusOrderByInvoiceConfirmationTime(
//            @Param("roomId") Integer roomId,
//            @Param("date") LocalDate date,
//            @Param("bookingStatus") BookingStatus bookingStatus);

    @Query("SELECT b FROM Booking b" +
            " WHERE b.status = :status" +
            " AND DATE(b.endDateTime) <= :endDate")
    List<Booking> findByStatusAndBeforeEndDate(
            @Param("status") BookingStatus status,
            @Param("endDate") LocalDate endDate
    );

    /* BOOKING  */
    Optional<Booking> findByIdAndUserId(Long bookingId, Integer userId);


    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Transactional
    @Query("SELECT b FROM Booking b WHERE b.id = ?1 AND b.userId = ?2")
    Optional<Booking> findByIdAndUserIdWithLock(Long bookingId, Integer userId);

//    @Lock(LockModeType.PESSIMISTIC_WRITE)
//    @Transactional
//    Optional<Booking> findByIdAndUserIdWithLock(Long bookingId, Integer userId);

    Optional<Booking> findByIdAndUserIdAndHotelId(Long bookingId, Integer userId, Integer hotelId);

    Optional<Booking> findByIdAndHotelManagerId(Long bookingId, Integer hotelManagaerId);

    Optional<Booking> findByIdAndHotelManagerIdAndHotelId(Long bookingId, Integer hotelManagerId, Integer hotelId);

    Optional<Booking> findByIdAndUserIdAndMainStatus(Long bookingId, Integer userId, BookingMainStatus reserved);

    boolean existsByIdAndHotelManagerId(BookingRoomsRepository bookingRoomsRepository, Integer userId);

    @Query(
            value = "SELECT b FROM Booking b " +
                    "JOIN BookingRooms brs on b = brs.booking " +
                    "JOIN BookingRoom br on brs = br.bookingRooms " +
                    "WHERE b.hotelId >= :hotelStartId AND b.hotelId < :hotelEndId " +
                    "   AND brs.prepayUntil < :date AND br.status = :status"
    )
    List<Booking> findByNoPrepaymentAndHotelRange(
            @Param("hotelStartId") int hotelStartId,
            @Param("hotelEndId") int hotelEndId,
            @Param("date") LocalDate date,
            @Param("status") BookingStatus reserved
    );

    /* BOOKING-MANAGEMENT - find by user ID */

    @Query(
            value = "SELECT b FROM Booking b " +
                    "WHERE b.userId = :userId " +
                    "AND b.mainStatus = :status " +
                    "AND DATE(b.endDateTime) >= :date"
    )
    List<Booking> findByUserIdAndMainStatusAndEndDate(
            @Param("userId") Integer userId,
            @Param("status") BookingMainStatus status,
            @Param("date") LocalDate date);

    @Query(
            value = "SELECT b FROM Booking b " +
                    "WHERE b.userId = :userId " +
                    "AND b.mainStatus = :status " +
                    "AND DATE(b.endDateTime) >= :startDate " +
                    "AND DATE(b.endDateTime) < :endDate"
    )
    List<Booking> findByUserIdAndMainStatusAndEndDateRange(
            @Param("userId") Integer userId,
            @Param("status") BookingMainStatus status,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);


    @Query(
            value = "SELECT b FROM Booking b " +
                    "WHERE b.userId = :userId " +
                    "AND DATE(b.endDateTime) >= :date"
    )
    List<Booking> findByUserIdAndDate(
            @Param("userId") Integer userId,
            @Param("date") LocalDate date);

    @Query(
            value = "SELECT b FROM Booking b " +
                    "WHERE b.userId = :userId " +
                    "AND DATE(b.endDateTime) >= :startDate " +
                    "AND DATE(b.endDateTime) < :endDate"
    )
    List<Booking> findByUserIdAndDateRange(
            @Param("userId") Integer userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);


    /* BOOKING-MANAGEMENT - find by hotel ID */

    @Query(
            value = "SELECT b FROM Booking b " +
                    "WHERE b.hotelId = ?1 " +
                    "AND b.mainStatus = ?2 " +
                    "AND DATE(b.endDateTime) >= ?3"
    )
    List<Booking> findByHotelIdAndMainStatusAndDate(
            @Param("hotelId") Integer hotelId,
            @Param("status") BookingMainStatus status,
            @Param("date") LocalDate date);

    @Query(
            value = "SELECT b FROM Booking b " +
                    "WHERE b.hotelId = :hotelId " +
                    "AND b.mainStatus = :status " +
                    "AND DATE(b.endDateTime) >= :startDate " +
                    "AND DATE(b.endDateTime) < :endDate"
    )
    List<Booking> findByHotelIdAndMainStatusAndDateRange(
            @Param("hotelId") Integer hotelId,
            @Param("status") BookingMainStatus status,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);


    @Query(
            value = "SELECT b FROM Booking b " +
                    "WHERE b.hotelId = :hotelId " +
                    "AND (b.mainStatus = :status1 OR b.mainStatus = :status2)"
    )
    List<Booking> findByHotelIdAndTwoMainStatus(
            @Param("hotelId") Integer hotelId,
            @Param("status1") BookingMainStatus status1,
            @Param("status2") BookingMainStatus status2);




    /* DELETE */

    @Transactional
    void deleteByHotelManagerId(Integer hotelManagerId);

    /* ARCHIVAL */

    @Query(
            value = "SELECT b FROM Booking b WHERE b.id in :bookingIds"
    )
    List<Booking> findByIds(@Param("bookingIds") List<Long> bookingIds);



//    Optional<Booking> findByIdAndStatus(
//            Long id, BookingStatus bookingStatus
//    );
//
//    List<Booking> findByInvoiceId(
//            String invoiceId
//    );

//    @Modifying
//    @Query("UPDATE Booking b SET b.status = ?2 WHERE b.id = ?1")
//    void setBookingStatusById(Long id, BookingStatus bookingStatus);
}
