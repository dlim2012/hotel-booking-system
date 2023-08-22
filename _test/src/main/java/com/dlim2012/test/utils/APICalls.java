package com.dlim2012.test.utils;

import com.dlim2012.clients.elasticsearch.document.Hotel;
import com.dlim2012.clients.elasticsearch.document.Room;
import com.dlim2012.clients.mysql_booking.entity.Price;
import com.dlim2012.clients.mysql_booking.entity.Rooms;
import com.dlim2012.test.dto.EmptyRequest;
import com.dlim2012.test.dto.IdItem;
import com.dlim2012.test.dto.booking.dto.BookingResponse;
import com.dlim2012.test.dto.booking.dto.ReserveResponse;
import com.dlim2012.test.dto.booking.dto.profile.RoomsPriceItem;
import com.dlim2012.test.dto.booking_management.dto.BookingArchiveItem;
import com.dlim2012.test.dto.hotel.dto.hotel.profile.HotelGeneralInfoItem;
import com.dlim2012.test.dto.hotel.dto.hotel.registration.HotelRegisterRequest;
import com.dlim2012.test.dto.hotel.dto.rooms.profile.RoomsGeneralInfoItem;
import com.dlim2012.test.dto.hotel.dto.rooms.registration.RoomsRegisterRequest;
import com.dlim2012.test.dto.search.dto.hotelSearch.HotelSearchResponse;
import com.dlim2012.test.dto.user.JwtResponse;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpServerErrorException;

import java.time.LocalDate;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
@Getter
public class APICalls {

    private final RestClient restClient = new RestClient();
    private final TestRequests testRequests = new TestRequests();

    private String jwt = "Bearer ";
    private Integer hotelId = 1;
    private Integer roomsId1 = 1;
    private Integer roomsId2 = 2;
    public Long reserveId = -1L;
    private Long bookingId = -1L;

    public void init() throws InterruptedException {

        try{
            this.userLogin();
        } catch(HttpServerErrorException | InterruptedException e){
            log.info(e.getMessage());
            this.userRegister();
        }
    }

    public void reset() throws InterruptedException{

        try{
            this.userLogin();
        } catch(HttpServerErrorException e){
            log.info(e.getMessage());
            this.userRegister();
        }

        this.deleteAllByUser();
        this.deleteAllHotelES();
    }

    public void userRegister() throws InterruptedException {
        log.info("USER: registering user.");
        JwtResponse result = (JwtResponse) restClient.post("/api/v1/user/register", testRequests.userRegisterRequest, JwtResponse.class);
        jwt = result.getJwt();
        restClient.setJwt("Bearer " + jwt);
        log.info("USER: Received jwt");
        interval();
    }


    public void userLogin() throws InterruptedException {
//        RestTemplate restTemplate = new RestTemplate();
        log.info("USER: Login.");
        JwtResponse result = (JwtResponse) restClient.post("/api/v1/user/login", testRequests.userLoginRequest, JwtResponse.class);
        jwt = result.getJwt();
        restClient.setJwt("Bearer " + jwt);
        log.info("USER: Received jwt");
        interval();
    }

    public Integer hotelAddHotel(HotelRegisterRequest request, Boolean useInterval) throws InterruptedException {
        IdItem idItem = (IdItem) restClient.postWithJwt(
                "/api/v1/hotel/hotel/register", request, IdItem.class);
        hotelId = idItem.getId();
        log.info("HOTEL: hotel {} added.", hotelId);
        if (useInterval) {
            interval();
        }
        return hotelId;
    }

    public void hotelAddHotel() throws InterruptedException {
        hotelAddHotel(testRequests.hotelregisterRequest, true);
    }

    public void hotelDeleteHotel() throws InterruptedException {
        log.info("HOTEL: deleting hotel {}", hotelId);
        ResponseEntity<?> responseEntity = restClient.deleteWithJwt(
                String.format("/api/v1/hotel/hotel/%d", hotelId),
                void.class
        );
        System.out.println(responseEntity.getStatusCode());
        interval();
    }

    public void hotelAddRooms(RoomsRegisterRequest request, Boolean useInterval) throws InterruptedException {
        IdItem idItem = (IdItem) restClient.postWithJwt(String.format("/api/v1/hotel/hotel/%d/room", hotelId), request, IdItem.class);
        roomsId1 = idItem.getId();
        log.info("HOTEL: rooms {} added.", roomsId1);
        if (useInterval){
            interval();
        }
    }

    public void hotelAddRooms(RoomsRegisterRequest request, Integer hotelId, Boolean useInterval) throws InterruptedException {
        IdItem idItem = (IdItem) restClient.postWithJwt(String.format("/api/v1/hotel/hotel/%d/room", hotelId), request, IdItem.class);
        roomsId1 = idItem.getId();
        log.info("HOTEL: rooms {} added.", roomsId1);
        if (useInterval){
            interval();
        }
    }

    public void hotelAddRooms() throws InterruptedException {
        hotelAddRooms(testRequests.roomsRegisterRequest1, true);
    }

    public void hotelDeleteRooms() throws InterruptedException {
        log.info("HOTEL: deleting rooms {} of hotel {}", roomsId1, hotelId);
        ResponseEntity<?> responseEntity = restClient.deleteWithJwt(
                String.format("/api/v1/hotel/hotel/%d/rooms/%d", hotelId, roomsId1),
                void.class
        );
        System.out.println(responseEntity.getStatusCode());
        interval();
    }

    public com.dlim2012.clients.mysql_booking.entity.Hotel bookingGetHotel(Boolean printAll, Boolean printRoom, Boolean printDates, Boolean printPrice) throws InterruptedException {
        log.info("BOOKING: Getting Rooms.");
        com.dlim2012.clients.mysql_booking.entity.Hotel hotel = (com.dlim2012.clients.mysql_booking.entity.Hotel) restClient.getWithJwt(
                String.format("/api/v1/booking/test/hotel/%d", hotelId), com.dlim2012.clients.mysql_booking.entity.Hotel.class);

        if (printAll) {
            System.out.println(hotel);
        }

        if (printRoom){
            for (Rooms rooms : hotel.getRoomsSet()) {
                System.out.println("roomsId: " + rooms.getId());
                for (com.dlim2012.clients.mysql_booking.entity.Room room: rooms.getRoomSet()){
                    System.out.println(room);
                }
            }

        }

        if (printDates){
            for (Rooms rooms : hotel.getRoomsSet()) {
                System.out.println("roomsId: " + rooms.getId());
                for (com.dlim2012.clients.mysql_booking.entity.Room room: rooms.getRoomSet()){
                    System.out.println(String.join(", ", room.getDatesSet().stream().map(dates->dates.getStartDate() + "-" + dates.getEndDate()).toList()));
                }
            }

        }

        if (printPrice){
            for (Rooms rooms: hotel.getRoomsSet()){
                System.out.println("roomsId: " + rooms.getId());
                for (Price price: rooms.getPriceList()){
                    System.out.println(price);
                }
            }
        }

        interval();
        return hotel;
    }


    public ReserveResponse reserve(LocalDate startDate, LocalDate endDate) throws InterruptedException {
        log.info("BOOKING: Reserving Rooms.");
        ReserveResponse reserveResponse = (ReserveResponse) restClient.postWithJwt(
                String.format("/api/v1/booking/hotel/%d/reserve", hotelId),
                testRequests.getBookingRequest(roomsId1, startDate, endDate),
                ReserveResponse.class
        );
        reserveId = reserveResponse.getBookingId();
        System.out.println(reserveResponse);
        interval();
        return reserveResponse;
    }

    public ReserveResponse reserve() throws InterruptedException {
        return reserve(LocalDate.now().plusDays(3), LocalDate.now().plusDays(4));
    }



    public BookingResponse book() throws InterruptedException {
        log.info("BOOKING: Booking Rooms.");
        BookingResponse bookingResponse = (BookingResponse) restClient.postWithJwt(
                String.format("/api/v1/booking/hotel/%d/book", hotelId),
                testRequests.getBookingRequest(roomsId1, LocalDate.now().plusDays(5), LocalDate.now().plusDays(6)),
                BookingResponse.class
        );
        bookingId = bookingResponse.getBookingId();
        System.out.println(bookingResponse);
        interval();
        return bookingResponse;
    }


    public void cancelBooking(Boolean byUser, Long bookingId) throws InterruptedException {
        if (byUser){
            log.info("BOOKING: cancelling booking {} by user id", bookingId);
            restClient.putWithJwt(
                    String.format("/api/v1/booking/booking/%d/user", bookingId),
                    new EmptyRequest(1),
                    void.class
            );
            interval();
        } else {
            log.info("BOOKING: cancelling booking {} by hotel manager id", bookingId);
            restClient.putWithJwt(
                    String.format("/api/v1/hotel/hotel/%d", bookingId),
                    new EmptyRequest(1),
                    void.class
            );
            interval();
        }
    }

    public void bookingTimeout(Long bookingId) throws InterruptedException {
        log.info("BOOKING: timing out booking {} by user id", bookingId);
        ResponseEntity<?> responseEntity = restClient.deleteWithJwt(
                String.format("/api/v1/booking/test/booking/%d/timeout", bookingId),
                void.class
        );
        System.out.println(responseEntity.getStatusCode());
        interval();

    }

    public void bookingResult() throws InterruptedException {
        log.info("BOOKING-MANAGEMENT: Fetching booking results.");
        BookingArchiveItem[] bookingArchiveItemList = (BookingArchiveItem[]) restClient.postWithJwt(
                "/api/v1/booking-management/user/booking",
                testRequests.listByUserRequest,
                BookingArchiveItem[].class
        );
        System.out.println(bookingArchiveItemList.length);
        for (BookingArchiveItem bookingArchiveItem: bookingArchiveItemList){
            System.out.println(bookingArchiveItem);
        }
        interval();
    }

    public Hotel searchConsumer(Boolean printAll, Boolean printRooms, Boolean printFacilities, Boolean printBeds, Boolean printPrice) throws InterruptedException {
        log.info("SEARCH-CONSUMER: Fetching hotel.");
        Hotel hotel = (Hotel) restClient.get(
                String.format("/api/v1/search-consumer/test/hotel/%d", hotelId),
                Hotel.class
        );
        log.info("Fetched hotel {}.", hotel.getId());
        if (printAll) {
            System.out.println(hotel);
        }
        if (printRooms) {
            System.out.println("--- Rooms ---");
            for (com.dlim2012.clients.elasticsearch.document.Rooms rooms : hotel.getRooms()) {
                for (Room room : rooms.getRoom()) {
                    System.out.println(String.join(", ", room.getDates().stream()
                            .map(i -> i.getDateRange().toString()).toList()));
                }
            }
        }
        if (printFacilities){
            System.out.println("--- Facilities ---");
            System.out.println(hotel.getFacility());
            for (com.dlim2012.clients.elasticsearch.document.Rooms rooms : hotel.getRooms()) {
                System.out.println(rooms.getRoomsId() + " " + rooms.getFacility());
            }
        }
        if (printBeds){
            System.out.println("--- Beds ---");
            for (com.dlim2012.clients.elasticsearch.document.Rooms rooms : hotel.getRooms()) {
                System.out.println(rooms.getRoomsId() + " " + rooms.getBed());
            }
        }
        if (printPrice){
            System.out.println("---price---");
            for (com.dlim2012.clients.elasticsearch.document.Rooms rooms : hotel.getRooms()) {
                System.out.println(rooms.getRoomsId() + " " + rooms.getPrice());
            }

        }
        interval();
        return hotel;
    }

    public void search() throws InterruptedException {
        log.info("SEARCH: Searching hotel.");
        HotelSearchResponse[] hotelSearchResponseList = (HotelSearchResponse[]) restClient.postWithJwt(
                "/api/v1/search/hotel",
                testRequests.hotelSearchRequest,
                HotelSearchResponse[].class
        );
        System.out.println(hotelSearchResponseList.length);
        for (HotelSearchResponse hotelSearchResponse: hotelSearchResponseList){
            System.out.println(hotelSearchResponse);
        }
        interval();
    }

    public void modifyHotelInfo() throws InterruptedException {
        log.info("HOTEL: Testing put hotel general info.");
        String newHotelName = "newHotelName";
        HotelGeneralInfoItem hotelGeneralInfoItem = (HotelGeneralInfoItem) restClient.getWithJwt(
                String.format("/api/v1/hotel/hotel/%d/info", hotelId),
                HotelGeneralInfoItem.class);
        System.out.println(hotelGeneralInfoItem);
        hotelGeneralInfoItem.setName(newHotelName);
        restClient.putWithJwt(
                String.format("/api/v1/hotel/hotel/%d/info", hotelId),
                hotelGeneralInfoItem,
                Void.class
        );
        hotelGeneralInfoItem = (HotelGeneralInfoItem) restClient.getWithJwt(
                String.format("/api/v1/hotel/hotel/%d/info", hotelId),
                HotelGeneralInfoItem.class);
        assert (hotelGeneralInfoItem.getName().equals(newHotelName));
        interval();
    }

    public void modifyRooms() throws InterruptedException {
        log.info("HOTEL, BOOKING: modify rooms test.");
        com.dlim2012.clients.mysql_booking.entity.Hotel hotel;
        hotel = bookingGetHotel(false, true, false, false);

        // decrease quantity
        Integer quantity1 = 2;
        LocalDate startDate = LocalDate.now().plusDays(1);
        LocalDate endDate = LocalDate.now().plusDays(15);


        reserve();
        reserve();
        reserve();
        reserve();
        reserve();

        TimeUnit.MILLISECONDS.sleep(1000);
        TimeUnit.MILLISECONDS.sleep(1000);
        RoomsGeneralInfoItem roomsGeneralInfoItem = (RoomsGeneralInfoItem) restClient.getWithJwt(
                String.format("/api/v1/hotel/hotel/%d/rooms/%d/info", hotelId, roomsId1),
                RoomsGeneralInfoItem.class
        );
        hotel = bookingGetHotel(false, true, false, false);

        roomsGeneralInfoItem.setQuantity(quantity1);
        roomsGeneralInfoItem.setAvailableFrom(startDate);
        roomsGeneralInfoItem.setAvailableUntil(endDate);
        restClient.putWithJwt(
                String.format("/api/v1/hotel/hotel/%d/rooms/%d/info", hotelId, roomsId1),
                roomsGeneralInfoItem,
                RoomsGeneralInfoItem.class
        );
        interval();
        hotel = bookingGetHotel(false, true, false, false);

        roomsGeneralInfoItem = (RoomsGeneralInfoItem) restClient.getWithJwt(
                String.format("/api/v1/hotel/hotel/%d/rooms/%d/info", hotelId, roomsId1),
                RoomsGeneralInfoItem.class
        );
        assert(roomsGeneralInfoItem.getQuantity().equals(quantity1));
        interval();

        /* check booking */
        hotel = bookingGetHotel(false, true, false, false);

    }

    public void modifyPrice() throws InterruptedException {
        log.info("BOOKING: put price.");
        Long newPrice = 10000L;
        RoomsPriceItem roomsPriceItem = (RoomsPriceItem) restClient.getWithJwt(
                String.format("/api/v1/booking/hotel/%d/rooms/%d/price", hotelId, roomsId1),
                RoomsPriceItem.class
        );
        for (RoomsPriceItem.PriceDto priceDto :roomsPriceItem.getPriceDtoList()){
            priceDto.setPriceInCents(newPrice);
        }
        restClient.putWithJwt(
                String.format("/api/v1/booking/hotel/%d/rooms/%d/price", hotelId, roomsId1),
                roomsPriceItem,
                Void.class
        );
        roomsPriceItem = (RoomsPriceItem) restClient.getWithJwt(
                String.format("/api/v1/booking/hotel/%d/rooms/%d/price", hotelId, roomsId1),
                RoomsPriceItem.class
        );
        System.out.println(roomsPriceItem);
        for (RoomsPriceItem.PriceDto priceDto :roomsPriceItem.getPriceDtoList()){
            assert (priceDto.getPriceInCents().equals(newPrice));
        }
        Hotel hotel = searchConsumer(false, true, false, false, true);
        for (com.dlim2012.clients.elasticsearch.document.Rooms rooms: hotel.getRooms()){
            for (com.dlim2012.clients.elasticsearch.document.Price price: rooms.getPrice()){
                assert(price.getPriceInCents().equals(newPrice));
            }
        }
        log.info("BOOKING, SEARCH-CONSUMER: put price test success.");



    }

    public void interval() throws InterruptedException {
        TimeUnit.MILLISECONDS.sleep(200);
        System.out.println("-----------------------------------------------------------");
    }

    public void deleteAllByUser() throws InterruptedException {
        log.info("HOTEL TEST: deleting hotel by user");
        ResponseEntity<?> responseEntity = restClient.deleteWithJwt(
                String.format("/api/v1/hotel/test/hotel"),
                void.class
        );
        System.out.println(responseEntity.getStatusCode());

        log.info("BOOKING TEST: deleting booking, hotel by user");
        responseEntity = restClient.deleteWithJwt(
                String.format("/api/v1/booking/test/delete"),
                void.class
        );
        System.out.println(responseEntity.getStatusCode());
    }

    public void deleteAllHotelES() throws InterruptedException{
        log.info("SEARCH-CONSUMER-TEST: deleting hotel by user");
        ResponseEntity<?> responseEntity = restClient.delete(
                String.format("/api/v1/search-consumer/test"),
                void.class
        );
    }


}
