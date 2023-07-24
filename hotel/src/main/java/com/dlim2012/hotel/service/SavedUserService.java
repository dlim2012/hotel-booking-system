package com.dlim2012.hotel.service;

import com.dlim2012.hotel.dto.hotel.saved.SavedHotel;
import com.dlim2012.hotel.dto.saved.HotelIdItem;
import com.dlim2012.hotel.entity.Hotel;
import com.dlim2012.hotel.entity.saved.SavedUser;
import com.dlim2012.hotel.repository.HotelRepository;
import com.dlim2012.hotel.repository.saved.SavedUserRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class SavedUserService {

    private final LocalityService localityService;

    private final SavedUserRepository savedUserRepository;
    private final HotelRepository hotelRepository;

    private final EntityManager entityManager;

    public void save(Integer userId, HotelIdItem item) {
        SavedUser savedUser = SavedUser.builder()
                .userId(userId)
                .hotel(entityManager.getReference(Hotel.class, item.getHotelId()))
                .build();
        savedUserRepository.save(savedUser);
        System.out.println(savedUserRepository.findAll());
    }


    public void deleteSaved(Integer userId, HotelIdItem item) {
        savedUserRepository.deleteByUserIdAndHotelId(userId, item.getHotelId());
    }

    public List<SavedHotel> getSavedHotels(Integer userId) {
        return hotelRepository.findBySavedUserId(userId).stream()
                .map(hotel -> SavedHotel.builder()
                        .id(hotel.getId())
                        .name(hotel.getName())
                        .address(localityService.getFullAddress(hotel))
                        .build())
                .toList();
    }
}
