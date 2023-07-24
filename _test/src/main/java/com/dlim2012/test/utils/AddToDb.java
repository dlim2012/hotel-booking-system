package com.dlim2012.test.utils;

import com.dlim2012.test.dto.hotel.dto.hotel.registration.HotelRegisterRequest;
import com.dlim2012.test.dto.hotel.dto.rooms.registration.BedInfo;
import com.dlim2012.test.dto.hotel.dto.rooms.registration.RoomsRegisterRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
public class AddToDb {

    private final APICalls apiCalls = new APICalls();
    private final Random rand = new Random();
    private final SharedIds sharedIds = new SharedIds();

    private final String LoremIpsum = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.";

    public void run() throws InterruptedException, IOException {
        apiCalls.init();
        addHotelsFromCSV();
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

    public void addHotelsFromCSV() throws IOException, InterruptedException {
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

        Map<Integer, String> numbers = new HashMap<>();
        numbers.put(1, "one");
        numbers.put(2, "two");
        numbers.put(3, "three");
        numbers.put(4, "four");
        numbers.put(5, "five");

        System.out.println("TOTAL: " + csvHotelData.size());

        List<String> roomNames = new ArrayList<>(
                List.of("Villa Suite", "Master Suite", "Presidential Suite", "City View Room", "Lodge Room", "Luxury Room",
                        "Deluxe Room", "Standard Suite", "Honeymoon Suite", "Bridal Suite", "Top View Room")
        );

//        for (int i=1; i<10; i++){
        int count = 1;
        int countAdded = 1;
        int roomsCount = 0;
        int roomCount = 0;
        long start = System.currentTimeMillis();
        for (int i = 1; i < csvHotelData.size(); i++) {
            List<String> row = csvHotelData.get(i);
//            if (!Objects.equals(row.get(3), "New York")) {
//                continue;
//            }
//            if (!Objects.equals(row.get(4), "New York")) {
//                continue;
//            }

            List<String> hotelFacilities = new ArrayList<>();
            for (String facility : sharedIds.getHotelFacilityList()) {
                if (rand.nextDouble() < 0.8) {
                    hotelFacilities.add(facility);
                }
            }
            long time = System.currentTimeMillis();
            System.out.println("==========    " + i + "/" + csvHotelData.size() + " , (count: " + count + "/" + countAdded + ", " + String.format("%.2f", ((double) (time - start)) / 1000 / count) + ", total time: " + String.format("%.2f", ((double) time - start) / 1000) + ")    ===================");

            countAdded++;

            double latitudeDiff = (Math.random() - 0.5) * 0.05;
            double longitudeDiff = (Math.random() - 0.5) * 0.05;
            HotelRegisterRequest hotelRegisterRequest = null;
            try {
                hotelRegisterRequest = HotelRegisterRequest.builder()
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
            } catch (Exception e){
                continue;
            }

            Integer hotelId;

            try {
                hotelId = apiCalls.hotelAddHotel(hotelRegisterRequest, false);
            } catch (Exception e) {
                System.out.println(e.getMessage());
                System.out.println(hotelRegisterRequest);
                continue;
            }


            int numRooms = (int) (Math.random() * 3) + 2;
            for (int j = 0; j < numRooms; j++) {
                String shortName = "rooms_" + numbers.get(j);
                int numBeds = (int) Math.pow(5, Math.random());
                int maxPeople = (int) (Math.random() * numBeds) + numBeds;
                int maxAdult = (1 + ((int) ((maxPeople - 1) * (0.5 + (Math.random() / 2)))));
                int quantity = (int) (Math.random() * 9) + 1;
                long minPrice = (long) Math.pow(10.0, 3.3 + (0.8 * (Math.random())));
                long maxPrice = minPrice * 2;
                int checkOutTime = 540 + 10 * ((int) (Math.random() * 15));
                int checkInTime = 900 + 10 * ((int) (Math.random() * 24));
                int noPrepaymentDays = (int) (Math.random() * 10);
                int freeCancellationDays = (int) (Math.random() * noPrepaymentDays);
                int roomNameIndex = (int) Math.floor((Math.random() * roomNames.size() * 0.9999));
                String roomName = roomNames.get(roomNameIndex) + String.format(" with %d bed" + (numBeds > 1 ? "s" : ""), numBeds);


                List<String> roomFacilities = new ArrayList<>();
                for (String facility : sharedIds.getRoomFacilityList()) {
                    if (facility == "Breakfast") {
                        if (rand.nextDouble() < 0.8) {
                            roomFacilities.add(facility);
                        }
                    } else {
                        if (rand.nextDouble() < 0.5) {
                            roomFacilities.add(facility);
                        }
                    }
                }

                Map<String, Integer> beds = new HashMap<>();

                for (int k = 0; k < numBeds; k++) {
                    String bed = weightedBedInfoRandom.getRandom();
                    if (beds.containsKey(bed)) {
                        beds.put(bed, beds.get(bed) + 1);
                    } else {
                        beds.put(bed, 1);
                    }
                }

                List<BedInfo> bedInfoList = new ArrayList<>();
                for (String bed : beds.keySet()) {
                    bedInfoList.add(BedInfo.builder().size(bed).quantity(beds.get(bed)).build());
                }

                RoomsRegisterRequest roomsRegisterRequest = RoomsRegisterRequest.builder()
                        .displayName(roomName)
                        .shortName(shortName)
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
                        .freeCancellationDays(noPrepaymentDays / 2)
                        .noPrepaymentDays(freeCancellationDays)
                        .facilityDisplayNameList(roomFacilities)
                        .bedInfoDtoList(bedInfoList)
                        .build();
                apiCalls.hotelAddRooms(roomsRegisterRequest, hotelId, false);


                roomsCount += 1;
                roomCount += quantity;
            }

            count++;
//            if (count >= 2){
//                break;
//            }
        }
    }

}
