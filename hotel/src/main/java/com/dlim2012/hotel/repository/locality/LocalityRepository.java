package com.dlim2012.hotel.repository.locality;

import com.dlim2012.hotel.entity.locality.Locality;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LocalityRepository extends JpaRepository<Locality, Integer> {
    boolean existsByCityId(Integer cityId);
    boolean existsByCityIdAndId(Integer cityId, Integer id);
    boolean existsByCityIdAndZipcode(Integer cityId, String zipcode);
    boolean existsByIdAndZipcode(Integer id, String zipcode);

    boolean existsByZipcode(String zipcode);

    Optional<Locality> findByZipcode(String zipcode);
    Optional<Locality> findByZipcodeAndCityId(String zipcode, Integer cityId);

    List<Locality> findByCityId(Integer cityId);
}