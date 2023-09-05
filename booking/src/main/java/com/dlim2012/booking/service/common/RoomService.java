package com.dlim2012.booking.service.common;

import com.dlim2012.clients.mysql_booking.entity.Room;
import com.dlim2012.clients.mysql_booking.entity.Rooms;
import com.dlim2012.clients.mysql_booking.repository.RoomRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final DatesService datesService;

    private final RoomRepository roomRepository;

    private final EntityManager entityManager;

    public void adjust(Rooms rooms, Integer quantity, Boolean isActive, LocalDate startDate, LocalDate endDate) {

        List<Room> newRoomList = new ArrayList<>();
        if (rooms.getRoomSet() == null || rooms.getRoomSet().size() <= quantity) {
            for (int i = (rooms.getRoomSet() == null ? 1 : rooms.getRoomSet().size()); i <= quantity; i++) {
                Room room = Room.builder()
                        .rooms(entityManager.getReference(Rooms.class, rooms.getId()))
                        .roomNumber(i)
                        .datesVersion(0L)
                        .build();
                newRoomList.add(room);
            }
        } else {
            List<Room> roomToRemoveList = new ArrayList<>();
            for (Room room : rooms.getRoomSet()) {
                if (room.getRoomNumber() > quantity) {
                    roomToRemoveList.add(room);
                }
            }
            roomToRemoveList.forEach(rooms.getRoomSet()::remove);
        }

        datesService.adjust(rooms, newRoomList, isActive, startDate, endDate);
        rooms.getRoomSet().addAll(newRoomList);
    }


    public Map<Long, Room> getRoomMapWithLock(Integer hotelId) {
        List<Room> hotelRoomList = roomRepository.findByHotelIdWithLock(hotelId);
        Map<Long, Room> roomMap = new HashMap<>();
        for (Room room : hotelRoomList) {
            roomMap.put(room.getId(), room);
        }
        return roomMap;
    }


}
