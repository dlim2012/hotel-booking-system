package com.dlim2012.clients.mysql_booking.repository;

import com.dlim2012.clients.entity.BookingStatus;
import com.dlim2012.clients.mysql_booking.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("SELECT b FROM Booking b" +
            " WHERE b.roomId = :roomId " +
            "   AND DATE(b.startDateTime) <= :date AND :date <= DATE(b.endDateTime)" +
            "   AND b.status = :bookingStatus" +
            " ORDER BY b.invoiceConfirmTime desc")
    List<Booking> findByRoomIdAndDateAndStatusOrderByInvoiceConfirmationTime(
            @Param("roomId") Integer roomId,
            @Param("date") LocalDate date,
            @Param("bookingStatus") BookingStatus bookingStatus);

    @Query("SELECT b FROM Booking b" +
            " WHERE b.status = :status" +
            " AND DATE(b.endDateTime) <= :endDate")
    List<Booking> findByStatusAndBeforeEndDate(
            @Param("status") BookingStatus status,
            @Param("endDate") LocalDate endDate
    );

    Optional<Booking> findByIdAndStatus(
            Long id, BookingStatus bookingStatus
    );

    List<Booking> findByInvoiceId(
            String invoiceId
    );

    @Modifying
    @Query("UPDATE Booking b SET b.status = ?2 WHERE b.id = ?1")
    void setBookingStatusById(Long id, BookingStatus bookingStatus);
}
