package com.dlim2012.hotel.repository.locality;

import com.dlim2012.hotel.entity.locality.City;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CityRepository extends JpaRepository<City, Integer> {
    boolean existsByStateId(Integer stateId);
    boolean existsByStateIdAndId(Integer stateId, Integer Id);
    boolean existsByIdAndName(Integer id, String name);

    Optional<City> findByNameAndStateId(String name, Integer stateId);
    List<City> findByStateId(Integer stateId);
}
