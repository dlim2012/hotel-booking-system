package com.dlim2012.searchconsumer.repository;

import com.dlim2012.clients.elasticsearch.document.Rooms;
import org.springframework.data.repository.CrudRepository;

public interface RoomsRepository extends CrudRepository<Rooms, String> {
    boolean existsById(String s);
}
