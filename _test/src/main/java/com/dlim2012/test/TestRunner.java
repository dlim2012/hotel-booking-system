package com.dlim2012.test;

import com.dlim2012.clients.elasticsearch.document.Hotel;
import com.dlim2012.test.dto.booking.dto.BookingResponse;
import com.dlim2012.test.dto.booking.dto.ReserveResponse;
import com.dlim2012.test.dto.hotel.dto.hotel.registration.HotelRegisterRequest;
import com.dlim2012.test.dto.hotel.dto.rooms.registration.BedInfo;
import com.dlim2012.test.dto.hotel.dto.rooms.registration.RoomsRegisterRequest;
import com.dlim2012.test.utils.APICalls;
import com.dlim2012.test.utils.AddToDb;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class TestRunner implements CommandLineRunner{

    APICalls apiCalls;
    AddToDb addToDb;

    public TestRunner() throws Exception {
//        this.apiCalls = new APICalls();
        this.addToDb = new AddToDb();
    }

    private final RestTemplate restTemplate = new RestTemplate();
    @Override
    public void run(String... args) throws Exception {
        addToDb.run();

//        apiCalls.reset();
//        System.out.println("Test Runner start.");

//        apiCalls.hotelAddHotel();
//        apiCalls.hotelAddRooms();
//        tests();
//
//        addData();

//        System.out.println("Test Runner end.");
    }

    // paypal test account
    // sb-gg0wr26334198@business.example.com
    // aP4VNrx6ZH*T

    public void addData() throws Exception{
        log.info("=========================== addData ===========================");
        apiCalls.hotelAddHotel();
        apiCalls.hotelAddRooms();
        TimeUnit.MILLISECONDS.sleep(2000);
        apiCalls.reserve();
        apiCalls.reserve();
        apiCalls.reserve(LocalDate.now().plusDays(1), LocalDate.now().plusDays(3));
        apiCalls.bookingGetHotel(false, false, true, false);
        apiCalls.book();
        apiCalls.book();
        apiCalls.bookingGetHotel(false, false, true, false);
        apiCalls.search();
        apiCalls.searchConsumer(true, false, false, false, false);
        apiCalls.bookingResult();
    }

    public void tests() throws  Exception{
        bookingTest();
        apiCalls.reset();
        putTest();
        apiCalls.reset();
        modifyRoomsTest();
        apiCalls.reset();
        deleteTest();
        apiCalls.reset();
    }

    public void modifyRoomsTest() throws Exception{
        log.info("=========================== modifyRoomsTest ===========================");

        // add hotel and rooms
        try {
            apiCalls.hotelAddHotel();
            apiCalls.hotelAddRooms();
        } catch(Exception e){
            // Error if hotel is already registered
            log.error(e.getMessage());
        }

        apiCalls.modifyRooms();
    }

    public void bookingTest() throws Exception{
        log.info("=========================== bookingTest ===========================");
        Hotel esHotel = null;
        com.dlim2012.clients.mysql_booking.entity.Hotel bookingHotel = null;

        HotelRegisterRequest hotelregisterRequest = HotelRegisterRequest.builder()
                .name("hotelName")
                .description("hotelDescription")
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
                        "24-hour front desk",
                        "Fitness center"
                ))
                .build();

        RoomsRegisterRequest roomsRegisterRequest1 = RoomsRegisterRequest.builder()
                .displayName("displayName")
                .shortName("shortName")
                .description("description")
                .bedInfoDtoList(List.of(
                        BedInfo.builder().size("KING").quantity(1).build(),
                        BedInfo.builder().size("SOFA_BED").quantity(1).build()
                ))
                .maxAdult(2)
                .maxChild(1)
                .quantity(5)
                .priceMin(1000L)
                .priceMax(10000L)
                .checkOutTime(660)
                .checkInTime(1080)
                .isActive(true)
                .availableFrom(LocalDate.now())
                .availableUntil(LocalDate.now().plusDays(10))
                .freeCancellationDays(5)
                .noPrepaymentDays(10)
                .facilityDisplayNameList(Arrays.asList(
                        "Kitchen", "Coffee machine",
                        "Breakfast"))
                .build();

        // add hotel and rooms
        try {
            apiCalls.hotelAddHotel(hotelregisterRequest, true);
            apiCalls.hotelAddRooms(roomsRegisterRequest1, true);
        } catch(Exception e){
            // Error if hotel is already registered
            log.error(e.getMessage());
        }

        /* check booking */
        bookingHotel = apiCalls.bookingGetHotel(false, false, true, false);

        /* check elasticsearch */
        esHotel = apiCalls.searchConsumer(false, true, false, false, false);
        apiCalls.search();

        /* reserve */
        ReserveResponse response1 =  apiCalls.reserve();
        assert (response1.getSuccess());
        ReserveResponse response2 =  apiCalls.reserve();
        assert (response2.getSuccess());

        /* check booking */
        bookingHotel = apiCalls.bookingGetHotel(false, false, true, false);

        /* check elasticsearch */
        esHotel = apiCalls.searchConsumer(false, true, false, false, false);
        apiCalls.search();

        /* cancel reserve */
        apiCalls.cancelBooking(true, apiCalls.reserveId);

        /* check booking */
        apiCalls.bookingGetHotel(false, false, true, false);
//
//        /* check elasticsearch */
        esHotel = apiCalls.searchConsumer(false, true, false, false, false);

        /* book */
        BookingResponse response3 = apiCalls.book();
        assert (response3.getReserveSuccess());
        BookingResponse response4 = apiCalls.book();
        assert (response4.getReserveSuccess());


        /* cancel booking */
        apiCalls.cancelBooking(true, response3.getBookingId());

        apiCalls.bookingTimeout(response4.getBookingId());

//
//
//
//        /* check booking */
        apiCalls.bookingGetHotel(false, false, true, false);
//
//        /* check elasticsearch */
        esHotel = apiCalls.searchConsumer(false, true, false, false, false);
//        apiCalls.search();


//        /* get booking results */
        apiCalls.bookingResult();

//        apiCalls.bookingTimeout(response3.getBookingId());
//        /* check booking */
//        apiCalls.bookingGetHotel(false, false, true, false);
//
//        /* check elasticsearch */
//        esHotel = apiCalls.searchConsumer(false, true, false, false, false);
//        apiCalls.search();
        /* modify hotel */
//        modifyHotelInfo();


        // check elastic search

        // check booking
    }

    public void putTest() throws Exception {
        log.info("=========================== putTest ===========================");
//        apiCalls.modifyHotelInfo();
        // add hotel and rooms
        try {
            apiCalls.hotelAddHotel();
            apiCalls.hotelAddRooms();
        } catch(Exception e){
            // Error if hotel is already registered
            log.info(e.getMessage());
        }

        /* modify hotel info *.
        apiCalls.modifyHotelInfo();

        /* check booking */
        apiCalls.bookingGetHotel(false, false, false, true);

        /* put price test */
        apiCalls.modifyPrice();

        /* check booking */
        apiCalls.bookingGetHotel(false, false, false, true);

        /* check elasticsearch */
        Hotel hotel = apiCalls.searchConsumer(false, true, false, false, true);

    }

    public void deleteTest() throws Exception{
        log.info("=========================== deleteTest ===========================");

        // add hotel and rooms
        try {
            apiCalls.hotelAddHotel();
            apiCalls.hotelAddRooms();
        } catch(Exception e){
            // Error if hotel is already registered
            log.info(e.getMessage());
        }

        /* check booking */
        apiCalls.bookingGetHotel(true, false, false, false);

        /* check elasticsearch */
        Hotel hotel = apiCalls.searchConsumer(false, true, false, false, false);

        /* Delete hotel */
        apiCalls.hotelDeleteHotel();

        /* check booking */
        try {
            apiCalls.bookingGetHotel(true, false, false, false);
            log.error("Fail: deleted hotel found in BOOKING.");
        } catch (HttpClientErrorException e){
            log.info("Success: deleted hotel not found in BOOKING.");
        }

        /* check elasticsearch */
        try {
            hotel = apiCalls.searchConsumer(false, true, false, false, false);
            log.error("Fail: deleted hotel found in elastic search.");
        } catch (HttpClientErrorException e){
            log.info("Success: deleted hotel not found in elastic search.");
        }

        // add hotel and rooms
        try {
            apiCalls.hotelAddHotel();
            apiCalls.hotelAddRooms();
        } catch(Exception e){
            // Error if hotel is already registered
            log.info(e.getMessage());
        }

        apiCalls.hotelDeleteRooms();

        /* check booking */
        apiCalls.bookingGetHotel(true, false, false, false);

        /* check elasticsearch */
        hotel = apiCalls.searchConsumer(false, true, false, false, false);

    }


}
