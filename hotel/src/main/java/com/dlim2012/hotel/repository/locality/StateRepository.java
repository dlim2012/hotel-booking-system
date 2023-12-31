package com.dlim2012.hotel.repository.locality;

import com.dlim2012.hotel.entity.locality.State;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StateRepository extends JpaRepository<State, Integer> {
    boolean existsByCountryId(Integer countryId);
    boolean existsByCountryIdAndId(Integer countryId, Integer id);
    boolean existsByNameAndCountryId(String name, Integer countryId);
    boolean existsByIdAndName(Integer id, String name);

    Optional<State> findByNameAndCountryId(String name, Integer countryId);
    List<State> findByCountryId(Integer countryId);
}
