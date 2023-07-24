package com.dlim2012.hotel.repository.saved;

import com.dlim2012.hotel.entity.saved.SavedUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

public interface SavedUserRepository extends JpaRepository<SavedUser, Integer> {
    @Transactional
    void deleteByUserIdAndHotelId(Integer userId, Integer hotelId);

    boolean existsByHotelIdAndUserId(Integer hotelId, Integer userId);
}
