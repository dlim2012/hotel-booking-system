package com.dlim2012.hotel.repository.facility;

import com.dlim2012.hotel.entity.facility.Facility;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface FacilityRepository extends JpaRepository<Facility, Integer> {

    boolean existsByIdAndDisplayName(Integer id, String displayName);
    boolean existsByDisplayName(String displayName);

    @Transactional
    void deleteByDisplayName(String displayName);
}
