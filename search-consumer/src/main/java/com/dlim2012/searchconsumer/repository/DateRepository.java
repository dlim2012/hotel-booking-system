package com.dlim2012.searchconsumer.repository;

import com.dlim2012.clients.elasticsearch.document.Dates;
import org.springframework.data.repository.CrudRepository;

public interface DateRepository extends CrudRepository<Dates, String> {
}
