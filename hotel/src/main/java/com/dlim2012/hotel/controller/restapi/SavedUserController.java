package com.dlim2012.hotel.controller.restapi;


import com.dlim2012.hotel.dto.hotel.saved.SavedHotel;
import com.dlim2012.hotel.dto.saved.HotelIdItem;
import com.dlim2012.hotel.service.SavedUserService;
import com.dlim2012.security.service.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/api/v1/hotel")
@RequiredArgsConstructor
@CrossOrigin
public class SavedUserController {
    private final JwtService jwtService;
    private final SavedUserService savedUserService;

    @PostMapping("/saved")
    public void postSaved(
            @RequestBody HotelIdItem item
    ){
        Integer userId = jwtService.getId();
        log.info("Save hotel {} requested from user {}", item.getHotelId(), userId);
        savedUserService.save(userId, item);
    }

    @GetMapping("/saved")
    List<SavedHotel> getSavedHotels(){
        Integer userId = jwtService.getId();
        return savedUserService.getSavedHotels(userId);
    }

    @DeleteMapping("/saved")
    public void deleteSaved(
            @RequestBody HotelIdItem item
    ){
        Integer userId = jwtService.getId();
        log.info("Delete saved hotel {} requested from user {}", item.getHotelId(), userId);
        savedUserService.deleteSaved(userId, item);
    }
}
