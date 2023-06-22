package com.dlim2012.search.controller;

import com.dlim2012.clients.dto.IdItem;
import com.dlim2012.clients.dto.hotel.HotelItem;
import com.dlim2012.clients.dto.hotel.RoomItem;
import com.dlim2012.clients.elasticsearch.document.Hotel;
import com.dlim2012.search.dto.HotelSearchRequest;
import com.dlim2012.search.dto.RoomSearchRequest;
import com.dlim2012.search.service.SearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/api/v1/search")
@RequiredArgsConstructor
@CrossOrigin
public class SearchController {

    private final SearchService searchService;

    @PostMapping(path = "/hotel")
    List<HotelItem> searchHotel(@RequestBody HotelSearchRequest hotelSearchRequest){
        return searchService.searchHotel(hotelSearchRequest);
    }

    @PostMapping(path = "/room")
    List<RoomItem> searchRoom(@RequestBody RoomSearchRequest roomSearchRequest){
        return searchService.searchRoom(roomSearchRequest);
    }

    @GetMapping(path = "/hotel/{hotelId}/room")
    List <RoomItem> searchHotelRooms(
            @PathVariable("hotelId") Integer hotelId,
            @RequestBody IdItem idItem
            ){
        return searchService.searchHotelRooms(hotelId, idItem);
    }
}
