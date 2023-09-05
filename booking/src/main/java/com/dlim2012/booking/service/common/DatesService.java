package com.dlim2012.booking.service.common;

import com.dlim2012.clients.entity.UserRole;
import com.dlim2012.clients.exception.EntityAlreadyExistsException;
import com.dlim2012.clients.exception.ResourceNotFoundException;
import com.dlim2012.clients.kafka.dto.search.dates.DatesUpdateDetails;
import com.dlim2012.clients.mysql_booking.entity.BookingRoom;
import com.dlim2012.clients.mysql_booking.entity.Dates;
import com.dlim2012.clients.mysql_booking.entity.Room;
import com.dlim2012.clients.mysql_booking.entity.Rooms;
import com.dlim2012.clients.mysql_booking.repository.BookingRoomRepository;
import com.dlim2012.clients.mysql_booking.repository.DatesRepository;
import com.dlim2012.clients.mysql_booking.repository.RoomRepository;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class DatesService {

    private final GetEntityService getEntityService;
    private final MappingService mappingService;
    private final KafkaTemplate<String, DatesUpdateDetails> roomsSearchDatesUpdateKafkaTemplate;

    private final RoomRepository roomRepository;
    private final DatesRepository datesRepository;
    private final BookingRoomRepository bookingRoomRepository;


    public void saveNewDates(List<Room> modifiedRoomList, List<Dates> newDatesList) {
        newDatesList = datesRepository.saveAll(newDatesList);
        Map<Long, List<Dates>> newDatesMap = new HashMap<>();
        for (Dates dates : newDatesList) {
            Long roomId = dates.getRoom().getId();
            List<Dates> roomDatesList = newDatesMap.get(roomId);
            if (roomDatesList == null) {
                roomDatesList = new ArrayList<>(List.of(dates));
                newDatesMap.put(roomId, roomDatesList);
            } else {
                roomDatesList.add(dates);
            }
        }

        for (Room room : modifiedRoomList) {
            List<Dates> roomDatesList = newDatesMap.get(room.getId());
            if (roomDatesList != null) {
                room.getDatesSet().addAll(roomDatesList);
            }
        }
    }

    public List<Dates> removeDateRange(Room room, Dates dates, LocalDate startDate, LocalDate endDate) {
        List<Dates> newDatesList = new ArrayList<>();
        if (dates.getStartDate().equals(startDate)) {
            if (dates.getEndDate().equals(endDate)) {
                room.getDatesSet().remove(dates);
            } else {
                dates.setStartDate(endDate);
            }
        } else {
            if (dates.getEndDate().equals(endDate)) {
                dates.setEndDate(startDate);
            } else {
                newDatesList.add(
                    Dates.builder()
                            .room(room)
                            .startDate(dates.getEndDate())
                            .endDate(endDate)
                            .build()
                );
                dates.setEndDate(startDate);
            }
        }
        return newDatesList;
    }

    public List<Dates> removeDateRange(Room room, LocalDate startDate, LocalDate endDate) {
        for (Dates dates : room.getDatesSet()) {
            if (!dates.getStartDate().isAfter(startDate) && !dates.getEndDate().isBefore(endDate)) {
                return removeDateRange(room, dates, startDate, endDate);
            }
        }
        throw new RuntimeException("Date range not available.");
    }

    public List<Dates> addDateRange(Room room, LocalDate startDate, LocalDate endDate) {
        List<Dates> newDatesList = new ArrayList<>();
        if (room == null || !startDate.isBefore(endDate)) {
            return newDatesList;
        }

        Dates prevDates = null;
        Dates nextDates = null;
        for (Dates dates : room.getDatesSet()) {
            if (dates.getEndDate().equals(startDate)) {
                prevDates = dates;
            }
            if (dates.getStartDate().equals(endDate)) {
                nextDates = dates;
            }
            if (dates.getStartDate().isBefore(endDate) && dates.getEndDate().isAfter(startDate)) {
                throw new EntityAlreadyExistsException("Date range occupied.");
            }
        }

        if (prevDates != null) {
            if (nextDates != null) {
                prevDates.setEndDate(nextDates.getEndDate());
                room.getDatesSet().remove(nextDates);
            } else {
                prevDates.setEndDate(endDate);
            }
        } else {
            if (nextDates != null) {
                nextDates.setStartDate(startDate);
            } else {
                newDatesList.add(
                    Dates.builder()
                            .room(room)
                            .startDate(startDate)
                            .endDate(endDate)
                            .build()
                );
            }
        }
        return newDatesList;
    }

    public void removeDateBefore(Room room, LocalDate date) {
        List<Dates> datesToRemove = new ArrayList<>();
        boolean added = false;
        for (Dates dates : room.getDatesSet()) {
            if (dates.getStartDate().isBefore(date)) {
                if (dates.getEndDate().isBefore(date)) {
                    datesToRemove.add(dates);
                } else {
                    dates.setStartDate(date);
                }
            }
        }
        datesToRemove.forEach(room.getDatesSet()::remove);
    }

    /*
    Handle REST API requests
     */

    public void addAvailability(UserRole userRole, Integer userId, Integer hotelId, Long roomId, LocalDate startDate, LocalDate endDate) {
        Room room = getEntityService.getRoomByManagerWithLock(userRole, userId, hotelId, roomId);
        addDateRange(room, startDate, endDate);
        sendRoomToEs(hotelId, List.of(room));
    }

    public void editAvailability(UserRole userRole, Integer userId, Integer hotelId, Long roomId, Long datesId, LocalDate newStartDate, LocalDate newEndDate) {
        Room room = getEntityService.getRoomByManagerWithLock(userRole, userId, hotelId, roomId);
        Dates dates = null;
        for (Dates _dates : room.getDatesSet()) {
            if (_dates.getId().equals(datesId)) {
                dates = _dates;
                break;
            }
        }
        if (dates == null) {
            throw new ResourceNotFoundException("Dates not found.");
        }
        room.getDatesSet().remove(dates);
        try {
            addDateRange(room, newStartDate, newEndDate);
            room.setDatesVersion(room.getDatesVersion() + 1L);
        } catch (EntityAlreadyExistsException e) {
            room.getDatesSet().add(dates);
            roomRepository.save(room);
            throw e;
        }
        roomRepository.save(room);
    }

    public void deleteAvailability(UserRole userRole, Integer userId, Integer hotelId, Long datesId) {
        if (userRole.equals(UserRole.ADMIN)) {
            datesRepository.deleteById(datesId);
        } else if (userRole.equals(UserRole.HOTEL_MANAGER)) {
            datesRepository.deleteByIDAndHotelManagerId(datesId, userId);
        } else if (userRole.equals(UserRole.APP_USER)) {
            throw new RuntimeException("App user not allowed to manage hotel.");
        } else {
            throw new RuntimeException("Invalid user role " + userRole.toString() + " for find hotel.");
        }
    }

    /*
    Internal usage
     */

    public void newRoomList(Integer roomsId, List<Room> roomList, LocalDate startDate, LocalDate endDate) {
        // TODO: consider existing booking records

        List<Dates> datesList = new ArrayList<>();
        for (Room room : roomList) {
            Dates dates = Dates.builder()
                    .startDate(startDate)
                    .endDate(endDate)
                    .room(room)
                    .build();
            datesList.add(dates);
        }
        datesRepository.saveAll(datesList);
    }

    public void removeAllDates(Rooms rooms) {
        for (Room room : rooms.getRoomSet()) {
            if (room.getDatesSet() != null) {
                room.getDatesSet().clear();
            }
        }
    }

    public void adjust(
            Rooms rooms, List<Room> newRoomList, Boolean isActive, LocalDate startDate, LocalDate endDate) {

        if (!isActive) {
            removeAllDates(rooms);
            return;
        }

        // get existing booked/reserved dates
        List<BookingRoom> bookingRoomList = bookingRoomRepository.findByRoomsIdWithLock(rooms.getId());
        Map<Long, List<BookingRoom>> bookingRoomByRoomId = new HashMap<>();
        for (BookingRoom bookingRoom : bookingRoomList) {
            Long roomId = bookingRoom.getRoomId();
            List<BookingRoom> roomBookingRoomList = bookingRoomByRoomId.get(roomId);
            if (roomBookingRoomList == null) {
                roomBookingRoomList = new ArrayList<>(List.of(bookingRoom));
                bookingRoomByRoomId.put(roomId, roomBookingRoomList);
            } else {
                roomBookingRoomList.add(bookingRoom);
            }
        }

        // adjust dates for existing rooms
        Integer numDays = (int) ChronoUnit.DAYS.between(startDate, endDate);
        for (Room room : rooms.getRoomSet()) {
            // make and initialize boolean array
            Boolean[] boolArray = new Boolean[numDays];
            Arrays.fill(boolArray, Boolean.TRUE);
            for (LocalDate date = rooms.getAvailableFrom(); date.isBefore(rooms.getAvailableUntil()); date = date.plusDays(1)) {
                int dateDiff = (int) ChronoUnit.DAYS.between(startDate, date);
                if (dateDiff < 0) {
                    continue;
                }
                if (dateDiff >= numDays) {
                    break;
                }
                boolArray[dateDiff] = Boolean.FALSE;
            }

            // mark dates in the previous range
            for (Dates dates : room.getDatesSet()) {
                if (!dates.getEndDate().isAfter(startDate) || !dates.getStartDate().isBefore(endDate)) {
                    continue;
                }
                LocalDate start = dates.getStartDate().isAfter(startDate) ? dates.getStartDate() : startDate;
                LocalDate end = dates.getEndDate().isBefore(endDate) ? dates.getEndDate() : endDate;
                for (LocalDate date = start; date.isBefore(end); date = date.plusDays(1)) {
                    int dateDiff = (int) ChronoUnit.DAYS.between(startDate, date);
                    boolArray[dateDiff] = Boolean.TRUE;
                }
            }

            // mark dates in added date range
            List<BookingRoom> roomBookingRoomList = bookingRoomByRoomId.get(room.getId());
            if (roomBookingRoomList != null) {
                for (BookingRoom bookingRoom : roomBookingRoomList) {
                    LocalDate start = bookingRoom.getStartDateTime().toLocalDate().isAfter(startDate) ?
                            bookingRoom.getStartDateTime().toLocalDate() : startDate;
                    LocalDate end = bookingRoom.getEndDateTime().toLocalDate().isBefore(endDate) ?
                            bookingRoom.getEndDateTime().toLocalDate() : endDate;
                    for (LocalDate date = start; date.isBefore(end); date = date.plusDays(1)) {
                        int dateDiff = (int) ChronoUnit.DAYS.between(startDate, date);
                        boolArray[dateDiff] = Boolean.FALSE;
                    }
                }
            }

            // regenerate Dates entities
            room.getDatesSet().clear();
            int s = -1;
            for (int i = 0; i < numDays; i++) {
                if (boolArray[i]) {
                    if (s == -1) {
                        s = i;
                    }
                } else {
                    if (s != -1) {
                        room.getDatesSet().add(
                                Dates.builder()
                                        .room(room)
                                        .startDate(startDate.plusDays(s))
                                        .endDate(startDate.plusDays(i))
                                        .build()
                        );
                        s = -1;
                    }
                }
            }

            if (s != -1) {
                room.getDatesSet().add(
                        Dates.builder()
                                .room(room)
                                .startDate(startDate.plusDays(s))
                                .endDate(startDate.plusDays(numDays))
                                .build()
                );
            }
        }


        // Add dates to new rooms
        for (Room room : newRoomList) {
            room.getDatesSet().add(Dates.builder()
                    .room(room)
                    .startDate(startDate)
                    .endDate(endDate)
                    .build());
        }

    }


    public List<Room> removeDatesFromRooms(Rooms rooms, LocalDate startDate, LocalDate endDate, Integer quantity) {
        List<DatesScore> datesScoreList = new ArrayList<>();

        // Remove available date ranges with priority of start date or end date matches
        for (Room room : rooms.getRoomSet()) {
            for (Dates dates : room.getDatesSet()) {
                if (!dates.getStartDate().isAfter(startDate) && !dates.getEndDate().isBefore(endDate)) {
                    int score = 0;
                    if (dates.getStartDate().equals(startDate)) {
                        score += 2;
                    }
                    if (dates.getEndDate().equals(endDate)) {
                        score += 1;
                    }
                    datesScoreList.add(
                            DatesScore.builder()
                                    .room(room)
                                    .dates(dates)
                                    .score(score)
                                    .build()
                    );
                }
            }
        }

        if (datesScoreList.size() < quantity) {
            throw new RuntimeException("Dates not available.");
        }

        datesScoreList.sort(Comparator.comparing(o -> o.score));
        List<Room> modifiedRoomList = new ArrayList<>();
        List<Dates> newDatesList = new ArrayList<>();
        for (DatesScore datesScore : datesScoreList) {
            newDatesList.addAll(
                    removeDateRange(datesScore.room, datesScore.getDates(), startDate, endDate));
            datesScore.room.setDatesVersion(datesScore.room.getDatesVersion() + 1L);
            modifiedRoomList.add(datesScore.room);
            quantity -= 1;
            if (quantity == 0) {
                break;
            }
        }

        saveNewDates(modifiedRoomList, newDatesList);

        return modifiedRoomList;
    }


    public void sendRoomToEs(Integer hotelId, List<Room> modifiedRoomList) {
        DatesUpdateDetails details = mappingService.getDatesUpdateDetails(hotelId, modifiedRoomList);
        roomsSearchDatesUpdateKafkaTemplate.send("rooms-search-dates-update", details);
    }

    @Builder
    @Data
    public static class DatesScore {
        public Room room;
        public Dates dates;
        public Integer score;
    }

}
