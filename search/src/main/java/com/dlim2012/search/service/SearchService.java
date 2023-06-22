package com.dlim2012.search.service;

import com.dlim2012.clients.dto.IdItem;
import com.dlim2012.clients.dto.hotel.HotelItem;
import com.dlim2012.clients.dto.hotel.RoomItem;
import com.dlim2012.search.dto.HotelSearchRequest;
import com.dlim2012.search.dto.RoomSearchRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SearchService {

    public List<HotelItem> searchHotel(HotelSearchRequest hotelSearchRequest) {
        // todo
        return new ArrayList<>();
    }

    public List<RoomItem> searchRoom(RoomSearchRequest roomSearchRequest) {
        // todo
        return new ArrayList<>();
    }

    public List<RoomItem> searchHotelRooms(Integer hotelId, IdItem idItem) {
        // todo
        return new ArrayList<>();
    }
}
