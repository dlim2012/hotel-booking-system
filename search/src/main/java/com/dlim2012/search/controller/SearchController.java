package com.dlim2012.search.controller;

import com.dlim2012.search.dto.count.NumberByCityRequest;
import com.dlim2012.search.dto.count.NumberByPropertyTypeRequest;
import com.dlim2012.search.dto.count.NumberResponse;
import com.dlim2012.search.dto.hotelSearch.HotelSearchRequest;
import com.dlim2012.search.dto.hotelSearch.HotelSearchResponse;
import com.dlim2012.search.dto.priceAgg.PriceAggRequest;
import com.dlim2012.search.dto.priceAgg.PriceAggResponse;
import com.dlim2012.search.dto.quantity.RoomsAvailabilityRequest;
import com.dlim2012.search.dto.quantity.RoomsAvailabilityResponse;
import com.dlim2012.search.service.CountHotelService;
import com.dlim2012.search.service.SearchService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/api/v1/search")
@RequiredArgsConstructor
@CrossOrigin
public class SearchController {

    private final SearchService searchService;
    private final CountHotelService countHotelService;

    @GetMapping("/test")
    public String test(){
        return "Test";
    }

    @PostMapping(path = "/hotel")
    HotelSearchResponse searchHotel(@RequestBody @Valid HotelSearchRequest hotelSearchRequest) throws IOException {
//        log.info("Search requested: {}", hotelSearchRequest);
        log.info("Search requested");
//        System.out.println(hotelSearchRequest);
        return searchService.search(hotelSearchRequest);
    }

    @PostMapping(path = "/price")
    List<PriceAggResponse> aggPrice(@RequestBody PriceAggRequest request) throws IOException {
        log.info("Price aggregation requested: {}", request);
        return searchService.aggPrice(request);
    }

    @PostMapping(path = "/hotel/{hotelId}/availability")
    RoomsAvailabilityResponse getRoomsAvailability(
            @PathVariable("hotelId") Integer hotelId,
            @RequestBody RoomsAvailabilityRequest request
            ) throws IOException {
        return searchService.getRoomsAvailability(hotelId, request);
    }

    @PostMapping(path = "/count/city")
    List<NumberResponse> numHotelByCity(
            @RequestBody List<NumberByCityRequest> request
            ) throws IOException {
        return countHotelService.numHotelByCity(request);
    }

    @PostMapping(path = "/count/property-type")
    List<NumberResponse> numHotelByPropertyType(
            @RequestBody List<NumberByPropertyTypeRequest> request
            ) throws IOException {
        return countHotelService.numHotelByPropertyType(request);
    }

//    @PostMapping(path = "/city")
//    public void searchHotelByCity(
//
//    ){
//    }

}
