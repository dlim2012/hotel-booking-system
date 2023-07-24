package com.dlim2012.user.test_user;

import com.dlim2012.clients.dto.IdItem;
import com.dlim2012.clients.exception.ResourceNotFoundException;
import com.dlim2012.user.entity.User;
import com.dlim2012.user.repository.UserRepository;
import com.dlim2012.user.service.TokenService;
import com.dlim2012.user.test_user.dto.booking.BookingRequest;
import com.dlim2012.user.test_user.dto.booking.BookingResponse;
import com.dlim2012.user.test_user.dto.booking.ReserveResponse;
import com.dlim2012.user.test_user.dto.hotel.BedInfo;
import com.dlim2012.user.test_user.dto.hotel.HotelRegisterRequest;
import com.dlim2012.user.test_user.dto.hotel.RoomsRegisterRequest;
import com.dlim2012.user.test_user.dto.search.PriceAggRequest;
import com.dlim2012.user.test_user.dto.search.PriceAggResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class TestUserService {

    private final String gatewayAddress = "http://10.0.0.110:9000";

    private final UserRepository userRepository;
    private final TokenService tokenService;
    private final PasswordEncoder passwordEncoder;

    private String appUserJwt = null;

    private final String LoremIpsum = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.";

    @Autowired
    public TestUserService(UserRepository userRepository, TokenService tokenService, PasswordEncoder passwordEncoder) throws InterruptedException {
        this.userRepository = userRepository;
        this.tokenService = tokenService;
        this.passwordEncoder = passwordEncoder;


//        TimeUnit.MILLISECONDS.sleep(1000);
//        User appUser = userRepository.findByEmail("appUser@hotel-booking.com")
//                .orElseThrow(() -> new ResourceNotFoundException("User not found."));
//        this.appUserJwt = tokenService.generateToken(appUser);
    }

    @Async
    public void asyncMethod() throws InterruptedException{
        Thread.sleep(5000);
        System.out.println("Calling other service..");
        System.out.println("Thread: " +
                Thread.currentThread().getName());
    }

    public String getAppUserJwt(){
        if (appUserJwt == null){
            User appUser = userRepository.findByEmail("appUser@hotel-booking.com")
                    .orElseThrow(() -> new ResourceNotFoundException("User not found."));
            appUserJwt = tokenService.generateToken(appUser);
        }
        return appUserJwt;
    }

    public <T> Object postWithJwt(String path, T t, Class<?> s, String jwt){
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", jwt);
        HttpEntity<T> request = new HttpEntity<T>(t, headers);
        return restTemplate.postForObject(gatewayAddress + path, request, s);
    }


    public <T> Object putWithJwt(String path, T t, Class<?> s, String jwt){
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", jwt);
        HttpEntity<T> request = new HttpEntity<T>(t, headers);
        return restTemplate.exchange(gatewayAddress + path, HttpMethod.PUT, request, s).getBody();
    }


    public HotelRegisterRequest getHotelRegisterRequest(){
        return HotelRegisterRequest.builder()
                .name("Test Hotel")
                .description(LoremIpsum)
                .propertyType("Hotel")
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
                        "24-hour front desk",
                        "Fitness center"
                ))
                .build();
    }

    public BookingRequest.BookingRequestRooms getBookingRequestRooms(Integer roomsId, String roomsName){
        return BookingRequest.BookingRequestRooms.builder()
                .roomsId(roomsId)
                .roomsName(roomsName)
                .guestName("guestName")
                .guestEmail("guestEmail")
                .build();
    }

    public BookingRequest getBookingRequest(
            List<BookingRequest.BookingRequestRooms> rooms,
            LocalDate startDate,
            LocalDate endDate,
            Long price
    ){
        return BookingRequest.builder()
                .firstName("firstName")
                .lastName("lastName")
                .email("booking@email.com")
                .hotelName("hotelName")
                .neighborhood("neighborhood")
                .city("city")
                .state("state")
                .country("country")
                .startDate(startDate)
                .endDate(endDate)
                .checkOutTime(600)
                .checkInTime(1080)
                .specialRequests("specialRequests")
                .estimatedArrivalHour(18)
                .priceInCents(price)
                .rooms(rooms)
                .build();
    }

    @Async
    public void function(String jwt) throws InterruptedException {
        RestTemplate restTemplate = new RestTemplate();

        String roomsName1 = "Suite Room for Honeymoon";
        String roomsNameShort1 = "Suite";
        String roomsName2 = "Deluxe Room with Two Beds";
        String roomsNameShort2 = "Double";


        Integer hotelId = ((IdItem) postWithJwt(
                "/api/v1/hotel/test/hotel/register",
                getHotelRegisterRequest(),
                IdItem.class,
                jwt
        )).getId();
        log.info("Hotel {} registered", hotelId);


        TimeUnit.MILLISECONDS.sleep(500);
        System.out.println("-----------------------------------------------------------");

        RoomsRegisterRequest roomsRegisterRequest1 = RoomsRegisterRequest.builder()
                .displayName(roomsName1)
                .shortName(roomsNameShort1)
                .description("description")
                .bedInfoDtoList(List.of(
                        BedInfo.builder().size("KING").quantity(1).build(),
                        BedInfo.builder().size("SOFA_BED").quantity(1).build()
                ))
                .maxAdult(2)
                .maxChild(1)
                .quantity(5)
                .priceMin(10000L)
                .priceMax(12000L)
                .checkOutTime(660)
                .checkInTime(1080)
                .isActive(true)
                .availableFrom(LocalDate.now())
                .availableUntil(LocalDate.now().plusDays(60))
                .freeCancellationDays(5)
                .noPrepaymentDays(10)
                .facilityDisplayNameList(Arrays.asList(
                        "Kitchen", "Coffee machine",
                        "Breakfast"))
                .build();
        Integer roomsId1 = ((IdItem) postWithJwt(
                String.format("/api/v1/hotel/test/hotel/%d/room", hotelId),
                roomsRegisterRequest1,
                IdItem.class,
                jwt
        )).getId();
        log.info("Rooms {} registered", roomsId1);


        System.out.println("-----------------------------------------------------------");

        RoomsRegisterRequest roomsRegisterRequest2 = RoomsRegisterRequest.builder()
                .displayName(roomsName2)
                .shortName(roomsNameShort2)
                .description("description2")
                .bedInfoDtoList(List.of(
                        BedInfo.builder().size("QUEEN").quantity(1).build(),
                        BedInfo.builder().size("TWIN").quantity(1).build()
                ))
                .maxAdult(1)
                .maxChild(1)
                .quantity(5)
                .priceMin(12500L)
                .priceMax(15000L)
                .checkOutTime(600)
                .checkInTime(900)
                .isActive(true)
                .availableFrom(LocalDate.now())
                .availableUntil(null)
                .freeCancellationDays(2)
                .noPrepaymentDays(4)
                .facilityDisplayNameList(Arrays.asList("Kitchen",
                        "Breakfast"))
                .build();
        Integer roomsId2 = ((IdItem) postWithJwt(
                String.format("/api/v1/hotel/test/hotel/%d/room", hotelId),
                roomsRegisterRequest2,
                IdItem.class,
                jwt
        )).getId();
        log.info("Rooms {} registered", roomsId2);

        TimeUnit.MILLISECONDS.sleep(2000);
        System.out.println("-----------------------------------------------------------");

        // reserve1
        LocalDate startDate = LocalDate.now().plusDays(10);
        LocalDate endDate = LocalDate.now().plusDays(20);
        PriceAggRequest priceAggRequest = PriceAggRequest.builder()
                .hotelId(hotelId)
                .startDate(startDate)
                .endDate(endDate)
                .build();
        PriceAggResponse[] prices = (PriceAggResponse[]) postWithJwt(
                "/api/v1/search/price",
                priceAggRequest,
                PriceAggResponse[].class,
                getAppUserJwt()
        );
        Map<Integer, Long> priceMap = new HashMap<>();
        for (PriceAggResponse priceAggResponse: prices){
            priceMap.put(priceAggResponse.getRoomsId(), priceAggResponse.getSumPrice());
        }

        List<BookingRequest.BookingRequestRooms> rooms = new ArrayList<>();
        rooms.add(getBookingRequestRooms(roomsId1, roomsName1));
        rooms.add(getBookingRequestRooms(roomsId1, roomsName1));
        rooms.add(getBookingRequestRooms(roomsId2, roomsName2));
        BookingRequest reserveRequest = getBookingRequest(rooms, startDate, endDate,
                priceMap.get(roomsId1) * 2 + priceMap.get(roomsId2)
                );
        ReserveResponse reserveResponse = (ReserveResponse) postWithJwt(
                String.format("/api/v1/booking/test/hotel/%d/reserve", hotelId),
                reserveRequest,
                ReserveResponse.class,
                appUserJwt
        );
        System.out.println(reserveResponse);

        TimeUnit.MILLISECONDS.sleep(500);
        System.out.println("-----------------------------------------------------------");

        // reserve 2
        startDate = LocalDate.now().plusDays(2);
        endDate = LocalDate.now().plusDays(5);
        priceAggRequest = PriceAggRequest.builder()
                .hotelId(hotelId)
                .startDate(startDate)
                .endDate(endDate)
                .build();
        prices = (PriceAggResponse[]) postWithJwt(
                "/api/v1/search/price",
                priceAggRequest,
                PriceAggResponse[].class,
                getAppUserJwt()
        );
        priceMap = new HashMap<>();
        for (PriceAggResponse priceAggResponse: prices){
            priceMap.put(priceAggResponse.getRoomsId(), priceAggResponse.getSumPrice());
        }

        rooms = new ArrayList<>();
        rooms.add(getBookingRequestRooms(roomsId2, roomsName1));
        rooms.add(getBookingRequestRooms(roomsId2, roomsName2));
        reserveRequest = getBookingRequest(rooms, startDate, endDate,
                priceMap.get(roomsId2) * 2
        );
        reserveResponse = (ReserveResponse) postWithJwt(
                String.format("/api/v1/booking/test/hotel/%d/reserve", hotelId),
                reserveRequest,
                ReserveResponse.class,
                appUserJwt
        );
        System.out.println(reserveResponse);


        TimeUnit.MILLISECONDS.sleep(500);
        System.out.println("-----------------------------------------------------------");

        // cancel reserve 2
        putWithJwt(String.format("/api/v1/booking/test/booking/%d/user", reserveResponse.getBookingId()),
                null,
                Void.class,
                appUserJwt
                );
        System.out.println("Cancelling reservation " + reserveResponse.getBookingId());

        TimeUnit.MILLISECONDS.sleep(500);
        System.out.println("-----------------------------------------------------------");

        // book 1
        startDate = LocalDate.now().plusDays(0);
        endDate = LocalDate.now().plusDays(15);
        priceAggRequest = PriceAggRequest.builder()
                .hotelId(hotelId)
                .startDate(startDate)
                .endDate(endDate)
                .build();
        prices = (PriceAggResponse[]) postWithJwt(
                "/api/v1/search/price",
                priceAggRequest,
                PriceAggResponse[].class,
                getAppUserJwt()
        );
        priceMap = new HashMap<>();
        for (PriceAggResponse priceAggResponse: prices){
            priceMap.put(priceAggResponse.getRoomsId(), priceAggResponse.getSumPrice());
        }

        rooms = new ArrayList<>();
        rooms.add(getBookingRequestRooms(roomsId1, roomsName1));
        rooms.add(getBookingRequestRooms(roomsId1, roomsName2));
        rooms.add(getBookingRequestRooms(roomsId1, roomsName2));
        BookingRequest bookingRequest = getBookingRequest(rooms, startDate, endDate,
                priceMap.get(roomsId1) * 3
        );
        BookingResponse bookingResponse = (BookingResponse) postWithJwt(
                String.format("/api/v1/booking/test/hotel/%d/book", hotelId),
                bookingRequest,
                BookingResponse.class,
                appUserJwt
        );
        System.out.println(bookingResponse);

        TimeUnit.MILLISECONDS.sleep(500);
        System.out.println("-----------------------------------------------------------");
        // todo: book and proceed

        startDate = LocalDate.now().plusDays(3);
        endDate = LocalDate.now().plusDays(6);
        priceAggRequest = PriceAggRequest.builder()
                .hotelId(hotelId)
                .startDate(startDate)
                .endDate(endDate)
                .build();
        prices = (PriceAggResponse[]) postWithJwt(
                "/api/v1/search/price",
                priceAggRequest,
                PriceAggResponse[].class,
                getAppUserJwt()
        );
        priceMap = new HashMap<>();
        for (PriceAggResponse priceAggResponse: prices){
            priceMap.put(priceAggResponse.getRoomsId(), priceAggResponse.getSumPrice());
        }

        rooms = new ArrayList<>();
        rooms.add(getBookingRequestRooms(roomsId2, roomsName1));
        rooms.add(getBookingRequestRooms(roomsId2, roomsName2));
        rooms.add(getBookingRequestRooms(roomsId2, roomsName2));
        bookingRequest = getBookingRequest(rooms, startDate, endDate,
                priceMap.get(roomsId2) * 3
        );
        bookingResponse = (BookingResponse) postWithJwt(
                String.format("/api/v1/booking/test/hotel/%d/book", hotelId),
                bookingRequest,
                BookingResponse.class,
                appUserJwt
        );
        System.out.println(bookingResponse);

        System.out.println("-----------------------------------------------------------");
        // process booking
        putWithJwt(
                String.format("/api/v1/booking/test/booking/%d/process", bookingResponse.getBookingId()),
                null,
                Void.class,
                appUserJwt
                );
        log.info("Processing booking {}", bookingResponse.getBookingId());

    }

    @Data
    @Builder
    @AllArgsConstructor
    public static class EmptyClass{

    }
}
