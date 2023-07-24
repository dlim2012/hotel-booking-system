package com.dlim2012.searchconsumer.controller;

import com.dlim2012.clients.elasticsearch.document.Hotel;
import com.dlim2012.searchconsumer.repository.HotelRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/api/v1/search-consumer/test")
@RequiredArgsConstructor
public class SearchConsumerTestController {

    private final HotelRepository hotelRepository;

    @GetMapping("/hotel/{hotelId}")
    public Hotel getHotel(
            @PathVariable("hotelId") Integer hotelId
    ){
        return hotelRepository.findById(hotelId.toString()).get();
    }

    @DeleteMapping("")
    public void deleteAll(){
        hotelRepository.deleteAll();
    }
}
