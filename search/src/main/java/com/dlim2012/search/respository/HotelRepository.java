package com.dlim2012.search.respository;

import com.dlim2012.clients.elasticsearch.document.Hotel;
import org.springframework.data.repository.CrudRepository;

public interface HotelRepository extends CrudRepository<Hotel, String> {
}
