package com.dlim2012.hotel.controller.restapi;

import com.dlim2012.hotel.dto.hotel.registration.HotelRoomsInfoResponse;
import com.dlim2012.hotel.service.HotelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/api/v1/hotel/public")
@RequiredArgsConstructor
@CrossOrigin
public class PublicController {


    private final HotelService hotelService;

    @GetMapping(path = "/hotel/{hotelId}/rooms")
    public HotelRoomsInfoResponse getHotelRooms(
            @PathVariable("hotelId") Integer hotelId
    ){
        log.info("Get hotel {} requested for rooms.", hotelId);
        return hotelService.getHotelRooms(hotelId);
    }

}
