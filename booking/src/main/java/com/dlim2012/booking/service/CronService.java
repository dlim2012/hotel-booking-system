package com.dlim2012.booking.service;

import com.dlim2012.booking.service.booking.BookingStatusService;
import com.dlim2012.booking.service.common.*;
import com.dlim2012.clients.entity.BookingStatus;
import com.dlim2012.clients.kafka.dto.archive.BookingIdArchiveRequest;
import com.dlim2012.clients.kafka.dto.search.dates.DatesUpdateDetails;
import com.dlim2012.clients.kafka.dto.search.hotel.HotelsNewDayDetails;
import com.dlim2012.clients.kafka.dto.search.price.PriceUpdateDetails;
import com.dlim2012.clients.mysql_booking.entity.*;
import com.dlim2012.clients.mysql_booking.repository.BookingRepository;
import com.dlim2012.clients.mysql_booking.repository.HotelRepository;
import com.dlim2012.clients.mysql_booking.repository.PriceRepository;
import com.dlim2012.clients.mysql_booking.repository.RoomsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class CronService {

    private final MappingService mappingService;
    private final DatesService datesService;
    private final PriceService priceService;
    private final BookingStatusService bookingStatusService;

    private final BookingRepository bookingRepository;
    private final HotelRepository hotelRepository;
    private final RoomsRepository roomsRepository;
    private final PriceRepository priceRepository;

    private final KafkaTemplate<String, HotelsNewDayDetails> hotelNewDayKafkaTemplate;
    private final KafkaTemplate<String, BookingIdArchiveRequest> bookingIdArchiveKafkaTemplate;

    private final Integer MAX_BOOKING_DAYS = 90;
    private final Integer CRON_MAX_HOTELS_FETCH = 100;

    @Scheduled(cron = "0 0 0 * * ?")
    public void newDayCron() {
        /*
        Updates price and dates every day
         */

        LocalDate minBookingDate = LocalDate.now();
        LocalDate maxBookingDate = minBookingDate.plusDays(MAX_BOOKING_DAYS);

        Integer maxId = hotelRepository.findMaxId();
        for (int hotelStartId = 0; hotelStartId <= maxId; hotelStartId += CRON_MAX_HOTELS_FETCH) {

            int hotelEndId = hotelStartId + CRON_MAX_HOTELS_FETCH;
            List<Hotel> hotelList = hotelRepository.findByIdGreaterThanEqualAndIdLessThanWithLock(hotelStartId, hotelEndId);


            List<Price> newPriceList = new ArrayList<>();
            List<Dates> newDatesList = new ArrayList<>();
            List<Room> modifiedRoomList = new ArrayList<>();
            for (Hotel hotel : hotelList) {
                for (Rooms rooms : hotel.getRoomsSet()) {
                    // update dates
                    for (Room room : rooms.getRoomSet()) {
                        datesService.removeDateBefore(room, minBookingDate);
                        newDatesList.addAll(datesService.addDateRange(room, rooms.getDatesAddedUntil(), maxBookingDate));
                        room.setDatesVersion(room.getDatesVersion() + 1L);
                        modifiedRoomList.add(room);
                    }

                    // Delete previous price
                    priceRepository.deleteByHotelIdRangeAndMaxDate(hotelStartId, hotelEndId, minBookingDate);

                    // Add new price
                    for (LocalDate date = rooms.getDatesAddedUntil(); date.isBefore(maxBookingDate); date = date.plusDays(1)) {
                        Price price = Price.builder()
                                .rooms(rooms)
                                .date(date)
                                .priceInCents(priceService.getDefaultPriceInCents(rooms.getPriceMin(), rooms.getPriceMax()))
                                .version(0)
                                .build();
                        newPriceList.add(price);
                    }
                    rooms.setDatesAddedUntil(maxBookingDate);
                }
            }

            datesService.saveNewDates(modifiedRoomList, newDatesList);
            priceRepository.saveAll(newPriceList);

            Map<Integer, DatesUpdateDetails> datesUpdateDetailsMap = new HashMap<>();
            Map<Integer, List<PriceUpdateDetails>> priceUpdateDetailsMap = new HashMap<>();
            Map<Integer, List<Price>> priceRoomsMap = mappingService.mapPriceByRoomsId(
                    priceRepository.findByHotelIdRange(hotelStartId, hotelEndId)
            );

            for (Hotel hotel : hotelList) {
                datesUpdateDetailsMap.put(hotel.getId(), mappingService.getDatesUpdateDetails(hotel));
                priceUpdateDetailsMap.put(hotel.getId(), mappingService.getPriceUpdateDetails(hotel, priceRoomsMap));
            }

            HotelsNewDayDetails hotelsNewDayDetails = HotelsNewDayDetails.builder()
                    .startId(hotelStartId)
                    .endId(hotelEndId)
                    .datesUpdateDetailsMap(datesUpdateDetailsMap)
                    .priceUpdateDetailsMap(priceUpdateDetailsMap)
                    .build();


            hotelRepository.saveAll(hotelList);
            hotelNewDayKafkaTemplate.send("hotel-new-day", hotelsNewDayDetails);
        }
    }

    @Scheduled(cron = "0 0 2 * * ?")
    public void noPrepaymentCron() {

        LocalDate today = LocalDate.now();

        Integer maxId = hotelRepository.findMaxId();
        List<Long> bookingIdsToCancel = new ArrayList<>();
        for (int hotelStartId = 0; hotelStartId <= maxId; hotelStartId += CRON_MAX_HOTELS_FETCH) {
            int hotelEndId = hotelStartId + CRON_MAX_HOTELS_FETCH;

            List<Booking> bookingList = bookingRepository
                    .findByNoPrepaymentAndHotelRange(hotelStartId, hotelEndId, today, BookingStatus.RESERVED);

            log.info("Cron job - processing no prepayments. found {} bookings with hotel ids in {} ~ {}",
                    bookingList.size(), hotelStartId, hotelEndId - 1);

            List<Rooms> roomsList = roomsRepository.findByHotelIdRangeWithLock(hotelStartId, hotelEndId);
            Map<Integer, Rooms> roomsMap = mappingService.mapRooms(roomsList);
            Map<Integer, List<Booking>> bookingMapByHotel = mappingService.mapBookingByHotelId(bookingList);

            List<Dates> newDates = new ArrayList<>();
            Set<Room> modifiedRoomSet = new HashSet<>();
            Map<Integer, Set<Room>> modifiedRoomByHotel = new HashMap<>();
            for (Map.Entry<Integer, List<Booking>> entry: bookingMapByHotel.entrySet()){
                Integer hotelId = entry.getKey();
                Set<Room> modifiedRoomHotel = new HashSet<>();
                for (Booking booking: entry.getValue()){
                    for (BookingRooms bookingRooms: booking.getBookingRooms()){
                        if (bookingRooms.getPrepayUntil() != null && bookingRooms.getPrepayUntil().isBefore(today)){
                            Rooms rooms = roomsMap.get(bookingRooms.getRoomsId());
                            for (BookingRoom bookingRoom: bookingRooms.getBookingRoomSet()){
                                if (bookingStatusService.isReservedStatus(bookingRoom.getStatus())){
                                    log.info("Cancelling booking {} - booking room {} (hotel {}, room {}) due to no prepayment.",
                                            booking.getId(), bookingRoom.getId(), booking.getHotelId(), bookingRooms.getRoomsId());
                                    bookingRoom.setStatus(BookingStatus.CANCELLED_NO_PREPAYMENT);
                                    booking.setPriceInCents(booking.getPriceInCents() - bookingRooms.getPricePerRoomInCents());

                                    if (rooms != null){
                                        rooms.setDatesReserved(rooms.getDatesReserved() -
                                                (int) ChronoUnit.DAYS.between(
                                                        bookingRoom.getStartDateTime().toLocalDate(),
                                                        booking.getEndDateTime().toLocalDate()));
                                        Long roomId = bookingRoom.getRoomId();
                                        for (Room room: rooms.getRoomSet()){
                                            if (room.getId().equals(roomId)){
                                                newDates.addAll(datesService.addDateRange(room, bookingRoom.getStartDateTime().toLocalDate(), bookingRoom.getEndDateTime().toLocalDate()));
                                                room.setDatesVersion(room.getDatesVersion()+1);
                                                modifiedRoomHotel.add(room);
                                                break;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                modifiedRoomSet.addAll(modifiedRoomHotel);
                modifiedRoomByHotel.put(hotelId, modifiedRoomHotel);
            }
            datesService.saveNewDates(modifiedRoomSet.stream().toList(), newDates);
            for (Map.Entry<Integer, Set<Room>> entry: modifiedRoomByHotel.entrySet()){
                datesService.sendRoomToEs(entry.getKey(), entry.getValue().stream().toList());
            }


            bookingRepository.saveAll(bookingList);
            roomsRepository.saveAll(roomsList);
            bookingIdArchiveKafkaTemplate.send("booking-archive", BookingIdArchiveRequest.builder()
                    .bookingIds(bookingIdsToCancel).build());

        }
    }

}
