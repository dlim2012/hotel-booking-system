package com.dlim2012.clients.cassandra.repository;

import com.dlim2012.clients.cassandra.entity.BookingArchiveByRoomId;
import com.dlim2012.clients.cassandra.entity.BookingArchiveByUserId;
import com.dlim2012.clients.entity.BookingMainStatus;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.stream.Stream;

@Repository
public interface BookingArchiveByRoomIdRepository
        extends CassandraRepository<BookingArchiveByRoomId, Integer> {

    Stream<BookingArchiveByUserId> findByRoomIdAndMainStatusAndEndDateTimeGreaterThan(
            Integer RoomId, BookingMainStatus mainStatus, LocalDateTime endDateTime);

}
