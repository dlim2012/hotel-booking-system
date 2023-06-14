package com.dlim2012.hotel.repository.locality;

import com.dlim2012.hotel.entity.locality.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface CountryRepository extends JpaRepository<Country, Integer> {

    boolean existsByIdAndName(Integer Id, String name);

    Optional<Country> findByName(String name);

    boolean existsByName(String name);

    @Transactional
    void deleteByName(String name);

//    @Modifying
//    @Query("DELETE FROM country c WHERE c.name=:name")
//    void deleteByName(@Param("name") String name);
}
