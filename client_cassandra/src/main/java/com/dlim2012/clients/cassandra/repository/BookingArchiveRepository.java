package com.dlim2012.clients.cassandra.repository;

import com.dlim2012.clients.cassandra.entity.BookingArchive;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingArchiveRepository
        extends CassandraRepository<BookingArchive, Long> {

}
