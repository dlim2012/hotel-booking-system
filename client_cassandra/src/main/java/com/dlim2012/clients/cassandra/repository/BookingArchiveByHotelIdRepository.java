package com.dlim2012.clients.cassandra.repository;

import com.dlim2012.clients.cassandra.entity.BookingArchiveByHotelId;
import com.dlim2012.clients.cassandra.entity.BookingArchiveByUserId;
import com.dlim2012.clients.entity.BookingMainStatus;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

@Repository
public interface BookingArchiveByHotelIdRepository
        extends CassandraRepository<BookingArchiveByHotelId, Integer> {

    Stream<BookingArchiveByUserId> findByHotelIdAndMainStatusAndEndDateTimeGreaterThan(
            Integer hotelId, BookingMainStatus mainStatus, LocalDateTime endDateTime);

    List<BookingArchiveByHotelId> findByHotelIdAndMainStatusAndEndDateTimeGreaterThanEqual(Integer hotelId, BookingMainStatus status, LocalDateTime startDateTime);

    List<BookingArchiveByHotelId> findByHotelIdAndMainStatusAndEndDateTimeGreaterThanEqualAndEndDateTimeLessThan(Integer hotelId, BookingMainStatus status, LocalDateTime startDateTime, LocalDateTime endDateTime);
}
