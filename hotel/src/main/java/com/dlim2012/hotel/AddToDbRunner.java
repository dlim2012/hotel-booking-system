package com.dlim2012.hotel;

import com.dlim2012.clients.entity.SharedIds;
import com.dlim2012.clients.exception.EntityAlreadyExistsException;
import com.dlim2012.hotel.dto.hotel.registration.HotelRegisterRequest;
import com.dlim2012.hotel.dto.rooms.registration.BedInfo;
import com.dlim2012.hotel.dto.rooms.registration.RoomsRegisterRequest;
import com.dlim2012.hotel.entity.Hotel;
import com.dlim2012.hotel.repository.HotelRepository;
import com.dlim2012.hotel.repository.RoomsRepository;
import com.dlim2012.hotel.repository.facility.FacilityRepository;
import com.dlim2012.hotel.repository.facility.HotelFacilityRepository;
import com.dlim2012.hotel.service.FacilityService;
import com.dlim2012.hotel.service.HotelService;
import com.dlim2012.hotel.service.LocalityService;
import com.dlim2012.hotel.service.RoomsService;
import com.dlim2012.hotel.util.WeightedRandom;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class AddToDbRunner implements CommandLineRunner {

    private final HotelService hotelService;
    private final RoomsService roomsService;
    private final LocalityService localityService;
    private final FacilityService facilityService;

    private final HotelRepository hotelRepository;
    private final RoomsRepository roomsRepository;
    private final FacilityRepository facilityRepository;
    private final HotelFacilityRepository hotelFacilityRepository;

    private final ModelMapper modelMapper = new ModelMapper();
    private final SharedIds sharedIds = new SharedIds();
    private final Random rand = new Random();

    private final String LoremIpsum = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.";


    @Override
    public void run(String... args) throws Exception {
        facilityService.saveFacilities(sharedIds.getFacilities());
//        addHotelRoomsExample("name");
//        addHotelRoomsExample("name2");
//        addHotelsFromCSV();
    }


    public void addHotelRoomsExample(String hotelName){

        HotelRegisterRequest hotelregisterRequest = HotelRegisterRequest.builder()
                .name(hotelName)
                .description(LoremIpsum)
                .propertyType("Guesthouse")
                .addressLine1("addressLine1")
                .addressLine2("addressLine2")
                .neighborhood("neighborhood")
                .zipcode("zipcode")
                .city("Amherst")
                .state("Massachusetts")
                .country("United States")
                .latitude(42.364748)
                .longitude(-72.539618)
                .phone("1234567890")
                .fax("12345-67890")
                .website("website@website.com")
                .email("email@email.com")
                .propertyRating(3)
                .facilityDisplayNameList(Arrays.asList(
                        "Conference Hall",
                        "Dry cleaning",
                        "24-hour front desk"
//                        "Fitness center"
                ))
                .build();
        Hotel hotel = hotelService.postHotel(1, hotelregisterRequest, true);

//        RoomsRegisterRequest roomsRegisterRequest = RoomsRegisterRequest.builder()
//                .displayName("displayName")
//                .description("description")
//                .bedInfoDtoList(List.of(
//                        BedInfo.builder().size("KING").quantity(1).build(),
//                        BedInfo.builder().size("SOFA_BED").quantity(1).build()
//                        ))
//                .maxAdult(1)
//                .maxChild(0)
//                .quantity(2)
//                .priceMin(1000L)
//                .priceMax(10000L)
//                .checkOutTime(660)
//                .checkInTime(1080)
//                .isActive(true)
//                .availableFrom(LocalDate.now())
//                .availableUntil(LocalDate.now().plusDays(3))
//                .freeCancellationDays(5)
//                .noPrepaymentDays(10)
//                .facilityDisplayNameList(Arrays.asList(
//                        "Kitchen", "Coffee machine",
//                        "Breakfast"))
//                .build();
//        roomsService.postRoom(1, hotel.getId(), roomsRegisterRequest);

        RoomsRegisterRequest roomsRegisterRequest2 = RoomsRegisterRequest.builder()
                .displayName("displayName2")
                .description("description2")
                .bedInfoDtoList(List.of(
                        BedInfo.builder().size("QUEEN").quantity(1).build()
                ))
                .maxAdult(1)
                .maxChild(1)
                .quantity(5)
                .priceMin(1100L)
                .priceMax(11000L)
                .checkOutTime(600)
                .checkInTime(1280)
                .isActive(true)
                .availableFrom(LocalDate.now())
                .availableUntil(LocalDate.now().plusDays(10))
                .freeCancellationDays(2)
                .noPrepaymentDays(4)
                .facilityDisplayNameList(Arrays.asList("Kitchen",
                        "Breakfast"))
                .build();
        roomsService.postRoom(1, hotel.getId(), roomsRegisterRequest2);

//        hotelregisterRequest.setAddressLine1("modifiedAddressLine!");
//        hotel = hotelService.postHotel(1, hotelregisterRequest);
        System.out.println("Runner Finished!");
    }





    public List<List<String>> readUsZips() throws IOException {

        String entityDirectory = System.getProperty("user.dir") + "/hotel/src/main/java/com/dlim2012/hotel/entity";
        File zipsFile = new ClassPathResource("assets/uszips.csv").getFile();
        System.out.println(zipsFile);
        return readCsv(zipsFile);
    }

    public List<List<String>> readCsv(File file){
        List<List<String>> data = new ArrayList<>();
        try(BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line = "";
            while ((line = br.readLine()) != null) {
                data.add(Arrays.stream(line.split(",")).toList());
            }
        } catch (IOException e) {
            log.info("Error while reading {}: {}", file, e);
        }
        return data;
    }

    public void addHotelsFromCSV() throws IOException {
        File csvFile = new ClassPathResource("assets/hotel_data.csv").getFile();
        System.out.println(readCsv(csvFile).get(0));
        List<List<String>> csvHotelData = readCsv(csvFile);

        WeightedRandom<Integer> weightedPropertyRatingRandom = new WeightedRandom<>();
        weightedPropertyRatingRandom.addEntry(0, 0.2);
        weightedPropertyRatingRandom.addEntry(1, 0.05);
        weightedPropertyRatingRandom.addEntry(2, 0.15);
        weightedPropertyRatingRandom.addEntry(3, 0.25);
        weightedPropertyRatingRandom.addEntry(4, 0.25);
        weightedPropertyRatingRandom.addEntry(5, 0.1);

        WeightedRandom<String> weightedBedInfoRandom = new WeightedRandom<>();
        weightedBedInfoRandom.addEntry("KING", 2);
        weightedBedInfoRandom.addEntry("QUEEN", 2);
        weightedBedInfoRandom.addEntry("SOFA_BED", 2);
        weightedBedInfoRandom.addEntry("FULL", 1);
        weightedBedInfoRandom.addEntry("TWIN", 1);
        weightedBedInfoRandom.addEntry("SINGLE", 1);

        System.out.println("TOTAL: " + csvHotelData.size());

        List<String> roomNames = new ArrayList<>(
                List.of("Villa Suite", "Master Suite", "Presidential Suite", "City View Room", "Lodge Room", "Luxury Room",
                        "Deluxe Room", "Standard Suite", "Honeymoon Suite", "Bridal Suite", "Top View Room")
        );

        List<Hotel> hotelsList = new ArrayList<>();
//        for (int i=1; i<10; i++){
        int count = 1;
        int countAdded = 1;
        long start = System.currentTimeMillis();
        for (int i=1; i<csvHotelData.size(); i++){
            List<String> row = csvHotelData.get(i);
            if (!Objects.equals(row.get(3), "New York")){
                continue;
            }
            if (!Objects.equals(row.get(4), "New York")){
                continue;
            }
            countAdded++;

            List<String> hotelFacilities = new ArrayList<>();
            for (String facility: sharedIds.getHotelFacilityList()){
                if (rand.nextDouble() < 0.8){
                    hotelFacilities.add(facility);
                }
            }
            long time = System.currentTimeMillis();
            log.info("==========    " + i + "/" + csvHotelData.size() + " , (count: " + count + "/" + countAdded + ", " + String.format("%.2f", ((double) (time - start)) / 1000 / count) + ", total time: " + String.format("%.2f", ((double) time - start) / 1000) + ")    ===================");

            double latitudeDiff = (Math.random() - 0.5) * 0.05;
            double longitudeDiff = (Math.random() - 0.5) * 0.05;

            HotelRegisterRequest hotelRegisterRequest = HotelRegisterRequest.builder()
                    .name(row.get(0))
                    .description(LoremIpsum)
                    .propertyType(Objects.equals(row.get(6), "Others") ? "Hotel" : row.get(6))
                    .addressLine1(row.get(1))
                    .addressLine2("")
                    .neighborhood("")
                    .zipcode(row.get(2))
                    .city(row.get(3))
                    .state(row.get(4))
                    .country("United States")
                    .latitude(Double.valueOf(row.get(9)) + latitudeDiff)
                    .longitude(Double.valueOf(row.get(10)) + longitudeDiff)
                    .phone(row.get(7))
                    .fax("")
                    .website(row.get(8))
                    .email("")
                    .propertyRating(weightedPropertyRatingRandom.getRandom())
                    .facilityDisplayNameList(hotelFacilities)
                    .build();

            Hotel hotel;
            try {
                hotel = hotelService.postHotel(1, hotelRegisterRequest, true);
            } catch(EntityAlreadyExistsException e){
                continue;
            } catch(Exception e){
                System.out.println(e.getMessage());
                System.out.println(hotelRegisterRequest);
                throw e;
            }

            if (hotel.getId() == null){
                System.out.println("hotel id is null.");
                continue;
            }


            int numRooms= (int) (Math.random() * 3) + 2;
            for (int j=0; j<numRooms; j++){
                int numBeds = (int) Math.pow(5, Math.random());
                int maxPeople = (int) (Math.random() * numBeds) + numBeds;
                int maxAdult =  (1 + ((int) ((maxPeople - 1) * (0.5 + (Math.random() / 2)))));
                int quantity = (int) (Math.random() * 9) + 1;
                long minPrice = (long) Math.pow(10.0, 3.3 + ( 0.8 * (Math.random())));
                long maxPrice = minPrice * 2;
                int checkOutTime = 540 + 10 * ((int) (Math.random() * 15));
                int checkInTime = 900 + 10 * ((int) ( Math.random() * 24));
                int noPrepaymentDays = (int) (Math.random() * 10);
                int roomNameIndex = (int) Math.floor((Math.random() * roomNames.size() * 0.9999));
                String roomName = roomNames.get(roomNameIndex) + String.format( " with %d bed" + (numBeds > 1 ? "s" : ""), numBeds);


                List<String> roomFacilities = new ArrayList<>();
                for (String facility: sharedIds.getRoomFacilityList()){
                    if (facility == "Breakfast"){
                        if (rand.nextDouble() < 0.8){
                            roomFacilities.add(facility);
                        }
                    }  else {
                        if (rand.nextDouble() < 0.5){
                            roomFacilities.add(facility);
                        }
                    }
                }

                Map<String, Integer> beds = new HashMap<>();

                for (int k=0; k<numBeds; k++){
                    String bed = weightedBedInfoRandom.getRandom();
                    if (beds.containsKey(bed)){
                        beds.put(bed, beds.get(bed) + 1);
                    } else {
                        beds.put(bed, 1);
                    }
                }

                List<BedInfo> bedInfoList = new ArrayList<>();
                for (String bed: beds.keySet()){
                    bedInfoList.add(BedInfo.builder().size(bed).quantity(beds.get(bed)).build());
                }

                RoomsRegisterRequest roomsRegisterRequest = RoomsRegisterRequest.builder()
                        .displayName(roomName)
                        .shortName("shortName")
                        .description(LoremIpsum)
                        .maxAdult(maxAdult)
                        .maxChild(maxPeople - maxAdult)
                        .quantity(quantity)
                        .priceMin(minPrice)
                        .priceMax(maxPrice)
                        .checkOutTime(checkOutTime)
                        .checkInTime(checkInTime)
                        .isActive(true)
                        .availableFrom(LocalDate.now())
                        .freeCancellationDays(noPrepaymentDays*2)
                        .noPrepaymentDays(noPrepaymentDays)
                        .facilityDisplayNameList(roomFacilities)
                        .bedInfoDtoList(bedInfoList)
                        .build();
                roomsService.postRoom(1, hotel.getId(),roomsRegisterRequest);
            }

            count++;
//            if (count >= 2){
//                break;
//            }
        }



    }


}
