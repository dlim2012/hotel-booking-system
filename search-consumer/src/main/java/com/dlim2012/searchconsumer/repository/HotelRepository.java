package com.dlim2012.searchconsumer.repository;

import com.dlim2012.clients.elasticsearch.document.Hotel;
import org.springframework.data.repository.CrudRepository;

import java.util.Set;

public interface HotelRepository extends CrudRepository<Hotel, String> {
    Set<Hotel> findByIdGreaterThan(int i);

    Set<Object> findByIdGreaterThanEqualAndIdLessThan(int i1, int i2);

    Set<Object> findByIdLessThan(int i);

    // Between: inclusive
    Set<Hotel> findByIdBetween(int i, int i1);
}
