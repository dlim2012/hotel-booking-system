package com.dlim2012.searchconsumer.service;

import com.dlim2012.clients.elasticsearch.config.ElasticSearchUtils;
import com.dlim2012.clients.elasticsearch.document.*;
import com.dlim2012.clients.exception.ResourceNotFoundException;
import com.dlim2012.clients.kafka.dto.search.dates.DatesUpdateDetails;
import com.dlim2012.clients.kafka.dto.search.rooms.RoomsSearchDeleteRequest;
import com.dlim2012.clients.kafka.dto.search.rooms.RoomsSearchDetails;
import com.dlim2012.clients.kafka.dto.search.rooms.RoomsSearchVersion;
import com.dlim2012.clients.utils.PriceService;
import com.dlim2012.searchconsumer.repository.DateRepository;
import com.dlim2012.searchconsumer.repository.HotelRepository;
import com.dlim2012.searchconsumer.repository.RoomsRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DateService {

    private final RoomsRepository roomsRepository;
    private final DateRepository dateRepository;
    private final HotelRepository hotelRepository;
    private final PriceService priceService;
    private final ElasticSearchUtils elasticSearchUtils;

    private final ModelMapper modelMapper = new ModelMapper();
    private final ObjectMapper objectMapper;
    private final Random random = new Random();
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final Integer MAX_ROOMS_QUANTITY = 100;
    private final Long DAYS = 30L;

    private final Integer NUM_RETRY_UPDATE = 2;

    public DateService(RoomsRepository roomsRepository, DateRepository dateRepository, HotelRepository hotelRepository, PriceService priceService, ElasticSearchUtils elasticSearchUtils, ObjectMapper objectMapper) {
        this.roomsRepository = roomsRepository;
        this.dateRepository = dateRepository;
        this.hotelRepository = hotelRepository;
        this.priceService = priceService;
        this.elasticSearchUtils = elasticSearchUtils;
        this.objectMapper = objectMapper;

        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
    }


    public Rooms roomsSearchDetailsToRooms(RoomsSearchDetails details){

        String roomsId = details.getRoomsId().toString();

        boolean breakfast = false;
        for (RoomsSearchDetails.FacilityDto facilityDto: details.getFacilityDto()){
            if (facilityDto.getDisplayName().equals("Breakfast")){
                breakfast = true;
            }
        }


        Rooms rooms = modelMapper.map(details, Rooms.class);
        rooms.setId(roomsId);
        rooms.setPriceRange(Rooms.PriceRange.builder()
                .gte(details.getPriceMin())
                .lte(details.getPriceMax())
                .build()
        );
        rooms.setAvailableFromInteger(elasticSearchUtils.toInteger(details.getAvailableFrom()));
        rooms.setAvailableUntilInteger(details.getAvailableUntil() == null ? null :
                elasticSearchUtils.toInteger(details.getAvailableUntil()));
        rooms.setRoom(details.getRoomDto().stream()
                .map(room -> Room.builder()
                        .roomId(room.getRoomId().toString())
                        .datesVersion(room.getDatesVersion())
                        .dates(room.getDatesDtoList().stream()
                                .map(datesDto -> Dates.builder()
                                        .id(datesDto.getDatesId().toString())
                                        .hotelId(details.getHotelId())
                                        .roomsId(details.getRoomsId())
                                        .roomId(room.getRoomId())
                                        .maxAdult(details.getMaxAdult())
                                        .maxChild(details.getMaxChild())
                                        .numBed(details.getBedDto().stream().map(RoomsSearchDetails.BedInfoDto::getQuantity).reduce(0, Integer::sum))
                                        .dateRange(Dates.DateRange.builder()
                                                .gte(elasticSearchUtils.toInteger(datesDto.getStartDate()))
                                                .lte(elasticSearchUtils.toInteger(datesDto.getEndDate()))
                                                .build())
                                        .build())
                                .collect(Collectors.toSet())
                        )
                        .build()
                )
                .toList());
        rooms.setPrice(details.getPriceDto().stream()
                .map(priceDto -> Price.builder()
                        .id(priceDto.getPriceId().toString())
                        .date(elasticSearchUtils.toInteger(priceDto.getDate()))
                        .roomsId(details.getRoomsId())
                        .priceInCents(priceDto.getPriceInCents())
                        .build()
                ).toList()
        );
        rooms.setFacility(details.getFacilityDto().stream()
                .map(facilityDto -> Facility.builder().id(facilityDto.getId()).build()).toList()
        );
        rooms.setBed(details.getBedDto().stream()
                .map(bedInfoDto -> modelMapper.map(bedInfoDto, RoomsBed.class)).toList());
        rooms.setNumBeds(details.getBedDto().stream()
                .map(RoomsSearchDetails.BedInfoDto::getQuantity).reduce(0, Integer::sum));
        rooms.setBreakfast(breakfast);
        return rooms;
    }



    public void updateRooms(RoomsSearchDetails details) throws IOException, InterruptedException {

        for (int i=0; i<NUM_RETRY_UPDATE; i++){
            try {
                // fetch hotel
                Hotel hotel = hotelRepository.findById(details.getHotelId().toString())
                        .orElseThrow(() -> new ResourceNotFoundException("Hotel {} not found while updating."));



                if (hotel.getSeqNoPrimaryTerm() == null){
                    throw new ResourceNotFoundException("Hotel {} found without SeqNoPrimaryTerm");
                }

                // update rooms
                Rooms newRooms = roomsSearchDetailsToRooms(details);
                List<Rooms> newRoomsList = new ArrayList<>();
                if (hotel.getRooms() != null){
                    List<Rooms> roomsList = hotel.getRooms();
                    for (Rooms rooms: roomsList){
                        if (rooms.getRoomsId().equals(details.getRoomsId())){
                            newRooms.setPrice(rooms.getPrice());
                        }
                        newRoomsList.add(rooms);
                    }
                }
                newRoomsList.add(newRooms);
                hotel.setRooms(newRoomsList);

                hotelRepository.save(hotel);
                return;
            } catch (OptimisticLockingFailureException | ResourceNotFoundException e){
                log.error(e.getMessage());
                TimeUnit.MILLISECONDS.sleep((long) (random.nextDouble() * 10));
            } catch (Exception e){
                log.error(e.getMessage());
                return;
            }
        }
        log.error("Add Rooms for hotel {} failed after {} retries.", details.getHotelId(), NUM_RETRY_UPDATE);
    }


    public void updateRoomsVersion(RoomsSearchVersion newVersionDetails) throws IOException, InterruptedException {

//        GetRequest getRequest = new GetRequest("hotel", details.getHotelId().toString());
//        GetResponse getResponse = client.get(getRequest, RequestOptions.DEFAULT);

        for (int i=0; i<NUM_RETRY_UPDATE; i++){
            try {
                // fetch hotel
                Hotel hotel = hotelRepository.findById(newVersionDetails.getHotelId().toString())
                        .orElseThrow(() -> new ResourceNotFoundException("Hotel {} not found while updating."));

                if (hotel.getSeqNoPrimaryTerm() == null){
                    throw new ResourceNotFoundException("Hotel {} found without SeqNoPrimaryTerm");
                }

                // update rooms
                if (hotel.getRooms() != null){
                    List<Rooms> roomsList = hotel.getRooms();
                    for (Rooms rooms: roomsList){
                        if (rooms.getRoomsId().equals(newVersionDetails.getRoomsId())){
                            rooms.setFreeCancellationDays(newVersionDetails.getFreeCancellationDays());
                            rooms.setNoPrepaymentDays(rooms.getNoPrepaymentDays());

                            Map<Long, Room> roomMap = new HashMap<>();
                            for (Room room: rooms.getRoom()){
                                roomMap.put(Long.valueOf(room.getRoomId()), room);
                            }

                            List<Room> roomList = new ArrayList<>();
                            for (RoomsSearchVersion.RoomDto roomDto: newVersionDetails.getRoomDto()){
                                Room prevRoom = roomMap.getOrDefault(roomDto.getRoomId(), null);
                                if (prevRoom != null && prevRoom.getDatesVersion() > roomDto.getDatesVersion()){
                                    roomList.add(prevRoom);
                                } else {
                                    roomList.add(
                                            Room.builder()
                                                    .roomId(roomDto.getRoomId().toString())
                                                    .datesVersion(roomDto.getDatesVersion())
                                                    .dates(roomDto.getDatesDtoList().stream()
                                                            .map(datesDto -> Dates.builder()
                                                                    .id(datesDto.getDatesId().toString())
                                                                    .hotelId(newVersionDetails.getHotelId())
                                                                    .roomsId(newVersionDetails.getRoomsId())
                                                                    .roomId(roomDto.getRoomId())
                                                                    .maxAdult(rooms.getMaxAdult())
                                                                    .maxChild(rooms.getMaxChild())
                                                                    .numBed(rooms.getNumBeds())
                                                                    .dateRange(Dates.DateRange.builder()
                                                                            .gte(elasticSearchUtils.toInteger(datesDto.getStartDate()))
                                                                            .lte(elasticSearchUtils.toInteger(datesDto.getEndDate()))
                                                                            .build())
                                                                    .build())
                                                            .collect(Collectors.toSet())
                                                    )
                                                    .build()
                                    );
                                }
                            }
                            rooms.setRoom(roomList);

                            rooms.setPrice(newVersionDetails.getPriceDto().stream()
                                    .map(priceDto -> Price.builder()
                                            .id(priceDto.getPriceId().toString())
                                            .date(elasticSearchUtils.toInteger(priceDto.getDate()))
                                            .roomsId(newVersionDetails.getRoomsId())
                                            .priceInCents(priceDto.getPriceInCents())
                                            .build()
                                    ).toList());
                        }
                    }
                }

                hotelRepository.save(hotel);
                return;
            } catch (OptimisticLockingFailureException | ResourceNotFoundException e){
                log.error(e.getMessage());
                TimeUnit.MILLISECONDS.sleep((long) (random.nextDouble() * 10));
            } catch (Exception e){
                log.error(e.getMessage());
                return;
            }
        }
        log.error("Add Rooms for hotel {} failed after {} retries.", newVersionDetails.getHotelId(), NUM_RETRY_UPDATE);
    }


    public void deleteRooms(RoomsSearchDeleteRequest request) throws InterruptedException {

        for (int i=0; i<NUM_RETRY_UPDATE; i++){
            try {
                // fetch hotel
                Hotel hotel = hotelRepository.findById(request.getHotelId().toString())
                        .orElseThrow(() -> new ResourceNotFoundException("Hotel {} not found while updating."));

                if (hotel.getSeqNoPrimaryTerm() == null){
                    throw new ResourceNotFoundException("Hotel {} found without SeqNoPrimaryTerm");
                }

                // update rooms
                List<Rooms> newRoomsList = new ArrayList<>();
                if (hotel.getRooms() != null){
                    List<Rooms> roomsList = hotel.getRooms();
                    for (Rooms rooms: roomsList){
                        if (rooms.getRoomsId().equals(request.getRoomsId())){
                            continue;
                        }
                        newRoomsList.add(rooms);
                    }
                }
                hotel.setRooms(newRoomsList);

                hotelRepository.save(hotel);
                return;
            } catch (OptimisticLockingFailureException | ResourceNotFoundException e){
                log.error(e.getMessage());
                TimeUnit.MILLISECONDS.sleep((long) (random.nextDouble() * 10));
            } catch (Exception e){
                log.error(e.getMessage());
                return;
            }
        }
        log.error("Delete Rooms {} for hotel {} failed after {} retries.", request.getRoomsId(), request.getHotelId(), NUM_RETRY_UPDATE);
    }

    public void updateHotelDates(Hotel hotel, DatesUpdateDetails details){
        if (details == null){
            return;
        }


        if (hotel.getSeqNoPrimaryTerm() == null){
            log.error("Hotel {} found without SeqNoPrimaryTerm", hotel.getId());
        }

        // update rooms dates availabilities
        for (Rooms rooms: hotel.getRooms()){
            for (Room room: rooms.getRoom()){
                Long roomId = Long.valueOf(room.getRoomId());
                Long datesVersion = details.getDatesVersions().getOrDefault(Long.valueOf(room.getRoomId()), null);
                if (datesVersion == null || datesVersion < room.getDatesVersion()){
                    continue;
                }
                room.setDatesVersion(datesVersion);

                List<DatesUpdateDetails.DatesDto> datesList = details.getDatesMap().getOrDefault(roomId, null);
                if (datesList == null){
                    continue;
                }

                room.setDates(datesList.stream().map(
                        datesDto -> Dates.builder()
                                .id(datesDto.getId().toString())
                                .hotelId(details.getHotelId())
                                .roomId(roomId)
                                .maxAdult(rooms.getMaxAdult())
                                .maxChild(rooms.getMaxChild())
                                .numBed(rooms.getNumBeds())
                                .dateRange(
                                        Dates.DateRange.builder()
                                                .gte(elasticSearchUtils.toInteger(datesDto.getStartDate()))
                                                .lte(elasticSearchUtils.toInteger(datesDto.getEndDate()))
                                                .build()
                                )
                                .build()
                ).collect(Collectors.toSet()));
            }
        }

        hotelRepository.save(hotel);
    }

    public void updateDates(DatesUpdateDetails details) throws InterruptedException {

        for (int i=0; i<NUM_RETRY_UPDATE; i++){
            try {
                // fetch hotel
                Hotel hotel = hotelRepository.findById(details.getHotelId().toString())
                        .orElseThrow(() -> new ResourceNotFoundException("Hotel {} not found while updating."));

                updateHotelDates(hotel, details);

                hotelRepository.save(hotel);
                return;
            } catch (OptimisticLockingFailureException | ResourceNotFoundException e){
                log.error(e.getMessage());
                TimeUnit.MILLISECONDS.sleep((long) (random.nextDouble() * 10));
            } catch (Exception e){
                log.error(e.getMessage());
                return;
            }
        }
        log.error("Updating dates for hotel {} failed after {} retries.", details.getHotelId(), NUM_RETRY_UPDATE);
    }

    @Scheduled(cron = "0 3 4 * * ?") // 4:30 am every day
    public void newDay(){

    }
}
