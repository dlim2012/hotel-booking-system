package com.dlim2012.hotel;

import com.dlim2012.hotel.entity.Hotel;
import com.dlim2012.hotel.exception.EntityAlreadyExistsException;
import com.dlim2012.hotel.repository.HotelRepository;
import com.dlim2012.hotel.repository.RoomRepository;
import com.dlim2012.hotel.repository.facility.FacilityRepository;
import com.dlim2012.hotel.repository.facility.HotelFacilityRepository;
import com.dlim2012.hotel.service.HotelService;
import com.dlim2012.dto.HotelFullAddressItem;
import com.dlim2012.dto.RoomItem;
import com.dlim2012.dto.facility.FacilityItem;
import com.dlim2012.dto.locality.CountryItem;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class AddToDbRunner implements CommandLineRunner {

    private final HotelService hotelService;
    private final HotelRepository hotelRepository;
    private final RoomRepository roomRepository;
    private final FacilityRepository facilityRepository;
    private final HotelFacilityRepository hotelFacilityRepository;

    @Autowired
    public AddToDbRunner(HotelService hotelService, HotelRepository hotelRepository, RoomRepository roomRepository, FacilityRepository facilityRepository, HotelFacilityRepository hotelFacilityRepository) {
        this.hotelService = hotelService;
        this.hotelRepository = hotelRepository;
        this.roomRepository = roomRepository;
        this.facilityRepository = facilityRepository;
        this.hotelFacilityRepository = hotelFacilityRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        String entityDirectory = System.getProperty("user.dir") + "/hotel/src/main/java/com/dlim2012/hotel/entity";
        try {
            log.info("Adding countries through a CommandLineRunner.");
            addCountries(entityDirectory + "/locality/countries.csv");
        } catch (Exception e) {
            System.out.println(e);
        }
        try {
            log.info("Adding facilities through a CommandLineRunner.");
            addFacilities(entityDirectory + "/facility/facilities.csv");
        } catch (Exception e) {
            System.out.println(e);
        }
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
                        hotelService.postCountry(new CountryItem(-1, strings[0], strings[1]));
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
                    hotelService.postFacility(new FacilityItem(-1, strings[0]));
                    } catch (EntityAlreadyExistsException ignored){

                    }
                }
            }
        } catch (IOException e) {
            log.info("Error while reading {}: {}", file, e);
        }
    }

    public void addHotelExample(){
        HotelFullAddressItem _hotelFullAddressItem = new HotelFullAddressItem(
                -1,
                "Example",
                "Exmaple Hotel",
                true,
                "example address line 1",
                "example address line 2",
                "00000",
                "example city",
                "example state",
                "United States"
        );
        System.out.println("postHotel");
        try {
            Hotel hotel = hotelService.postHotel(_hotelFullAddressItem);
        } catch (Exception e){
            System.out.println(e);
        }

        RoomItem _roomPostRequest = new RoomItem(
                -1,
                "exampleRoomDisplayName",
                true,
                "exampleDescription",
                2,
                1,
                10,
                10.0,
                20.0
        );
        List<RoomItem> roomPostRequestList = new ArrayList<>();
        roomPostRequestList.add(_roomPostRequest);
        System.out.println("postRoom");
        hotelService.postRoom(1, roomPostRequestList);

//        Optional<Hotel> optionalHotel = hotelRepository.findById(1);
//        if (optionalHotel.isEmpty()){
//            System.out.println("hotel not found.");
//            return;
//        }
//        Hotel hotel = optionalHotel.get();
//        System.out.println(hotel);
//        System.out.println(hotel.getRooms());
//
//        IdItem idItem1 = new IdItem(1);
//        IdItem idItem2 = new IdItem(2);
//        List<IdItem> idItemList = new ArrayList<>();
//        idItemList.add(idItem1);
//        idItemList.add(idItem2);
//
//        hotelService.putHotelFacilities(1, idItemList);
//        System.out.println(hotelFacilityRepository.findByHotelId(hotel.getId()));
    }

}
