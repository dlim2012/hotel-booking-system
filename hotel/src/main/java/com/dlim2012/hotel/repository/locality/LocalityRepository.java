package com.dlim2012.hotel.repository.locality;

import com.dlim2012.hotel.entity.locality.Locality;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LocalityRepository extends JpaRepository<Locality, Integer> {

    boolean existsByZipcode(String zipcode);

    Optional<Locality> findByZipcode(String zipcode);
}