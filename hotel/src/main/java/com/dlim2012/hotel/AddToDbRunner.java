package com.dlim2012.hotel;

import com.dlim2012.clients.dto.IdItem;
import com.dlim2012.clients.dto.hotel.HotelItem;
import com.dlim2012.clients.dto.hotel.RoomItem;
import com.dlim2012.clients.dto.hotel.facility.FacilityItem;
import com.dlim2012.clients.dto.hotel.facility.HotelFacilityItem;
import com.dlim2012.clients.dto.hotel.facility.RoomFacilityItem;
import com.dlim2012.hotel.dto.locality.CountryItem;
import com.dlim2012.clients.exception.EntityAlreadyExistsException;
import com.dlim2012.hotel.entity.Hotel;
import com.dlim2012.hotel.entity.Room;
import com.dlim2012.hotel.repository.HotelRepository;
import com.dlim2012.hotel.repository.RoomRepository;
import com.dlim2012.hotel.repository.facility.FacilityRepository;
import com.dlim2012.hotel.repository.facility.HotelFacilityRepository;
import com.dlim2012.hotel.service.FacilityService;
import com.dlim2012.hotel.service.HotelService;
import com.dlim2012.hotel.service.LocalityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class AddToDbRunner implements CommandLineRunner {

    private final HotelService hotelService;
    private final LocalityService localityService;
    private final FacilityService facilityService;

    private final HotelRepository hotelRepository;
    private final RoomRepository roomRepository;
    private final FacilityRepository facilityRepository;
    private final HotelFacilityRepository hotelFacilityRepository;

    private final ModelMapper modelMapper = new ModelMapper();

    @Override
    public void run(String... args) throws Exception {
        String entityDirectory = System.getProperty("user.dir") + "/hotel/src/main/java/com/dlim2012/hotel/entity";
//        try {
//            log.info("Adding countries through a CommandLineRunner.");
//            addCountries(entityDirectory + "/locality/countries.csv");
//        } catch (Exception e) {
//            System.out.println(e);
//        }
//        try {
//            log.info("Adding facilities through a CommandLineRunner.");
//            addFacilities(entityDirectory + "/facility/facilities.csv");
//        } catch (Exception e) {
//            System.out.println(e);
//        }
        try {
            log.info("Adding example hotel through a CommandLineRunner");
            addHotelExample();
        } catch (Exception e) {
            System.out.println(e);
        }
    }
    public void addCountries(String file){
        try(BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line = "";
            while ((line = br.readLine()) != null) {
                String[] strings = line.split(",");
                if (strings.length == 2) {
                    try {
                        localityService.postCountry(
                                new CountryItem(null, strings[0], strings[1]));
                    } catch (EntityAlreadyExistsException ignored){

                    }
                }
            }
        } catch (IOException e) {
            log.info("Error while reading {}: {}", file, e);
        }
    }

    public void addFacilities(String file){
        try(BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line = "";
            while ((line = br.readLine()) != null) {
                String[] strings = line.split(",");
                if (strings.length == 1) {
                    try {
                        facilityService.postFacility(new FacilityItem(-1, strings[0]));
                    } catch (EntityAlreadyExistsException ignored){

                    }
                }
            }
        } catch (IOException e) {
            log.info("Error while reading {}: {}", file, e);
        }
    }

    public void addHotelExample(){
        try {
            localityService.postCountry(CountryItem.builder().id(-1).name("United States").build());
        } catch (Exception exception){
            localityService.putCountry(CountryItem.builder().id(-1).name("United States").build());
        }
        try {
            facilityService.postFacility(FacilityItem.builder().id(-1).displayName("facility1").build());
        } catch (Exception exception){
            facilityService.putFacility(FacilityItem.builder().id(-1).displayName("facility1").build());
        }
        try {
            facilityService.postFacility(FacilityItem.builder().id(-1).displayName("facility2").build());
        } catch (Exception exception){
            facilityService.putFacility(FacilityItem.builder().id(-1).displayName("facility2").build());
        }

        HotelItem hotelItem = HotelItem.builder()
                .id(null)
                .displayName("displayName")
                .description("description")
                .isActive(true)
                .addressLine1("addressLine1")
                .addressLine2("addressLine2")
                .zipcode("zipcode")
                .city("city")
                .state("state")
                .country("United States")
                .build();
        System.out.println(modelMapper.map(hotelItem, Hotel.class));
        System.out.println("postHotel");
        Hotel hotel = hotelService.postHotel(1, hotelItem, "junghoonlim12@gmail.com");

        RoomItem roomItem = RoomItem.builder()
                .id(null)
                .hotelId(1)
                .displayName("displayName")
                .description("description")
                .isActive(true)
                .maxAdult(2)
                .maxChild(1)
                .quantity(10)
                .priceMin(1.0)
                .priceMax(2.0)
                .checkOutTime(600)
                .checkInTime(1080)
                .availableFrom(LocalDate.now())
                .availableUntil(LocalDate.now().plusDays(100))
                .build();
        System.out.println(modelMapper.map(roomItem, Room.class).toString());
        System.out.println("postRoom");
        hotelService.postRoom(1, roomItem, "junghoonlim12@gmail.com");

//        hotelService.putHotel(1, hotelItem, "junghoonlim12@gmail.com");
//        hotelService.putRoom(1, 1, roomItem, "junghoonlim12@gmail.com");

        // add facilites
        List<HotelFacilityItem> hotelFacilityItemList = new ArrayList<>();
        hotelFacilityItemList.add(HotelFacilityItem.builder().facilityId(1).isActive(true).build());
        hotelFacilityItemList.add(HotelFacilityItem.builder().facilityId(2).isActive(true).build());
        facilityService.setHotelFacilities(1, hotelFacilityItemList);

        List<RoomFacilityItem> roomFacilityItemList = new ArrayList<>();
        roomFacilityItemList.add(RoomFacilityItem.builder().facilityId(1).isActive(true).build());
        roomFacilityItemList.add(RoomFacilityItem.builder().facilityId(2).isActive(true).build());
        facilityService.setRoomFacilities(1, 1, roomFacilityItemList);

        hotelService.deleteHotel(1);
        hotelService.deleteRoom(1, 1);
        facilityService.deleteFacilities(List.of(new IdItem(1), new IdItem(2)));
    }

}
