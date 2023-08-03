package com.dlim2012.test.utils;

import com.dlim2012.test.dto.booking.dto.BookingRequest;
import com.dlim2012.test.dto.booking_management.dto.ListByUserRequest;
import com.dlim2012.test.dto.hotel.dto.hotel.registration.HotelRegisterRequest;
import com.dlim2012.test.dto.hotel.dto.rooms.registration.BedInfo;
import com.dlim2012.test.dto.hotel.dto.rooms.registration.RoomsRegisterRequest;
import com.dlim2012.test.dto.search.dto.hotelSearch.HotelSearchRequest;
import com.dlim2012.test.dto.user.AuthenticationRequest;
import com.dlim2012.test.dto.user.UserRegisterRequest;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TestRequests {

    private final String userEmail = "admin@hb.com";
    private final String userPassword = "admin_user_password";

    public UserRegisterRequest userRegisterRequest = UserRegisterRequest.builder()
            .firstName("firstname")
            .lastName("lastname")
            .email(userEmail)
            .password(userPassword)
            .build();

    public AuthenticationRequest userLoginRequest = AuthenticationRequest.builder()
            .email(userEmail)
            .password(userPassword)
            .build();

    public HotelRegisterRequest hotelregisterRequest = HotelRegisterRequest.builder()
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

    public RoomsRegisterRequest roomsRegisterRequest1 = RoomsRegisterRequest.builder()
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

    public RoomsRegisterRequest roomsRegisterRequest2 = RoomsRegisterRequest.builder()
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
            .availableUntil(LocalDate.now().plusDays(5))
            .freeCancellationDays(2)
            .noPrepaymentDays(4)
            .facilityDisplayNameList(Arrays.asList("Kitchen",
                    "Breakfast"))
            .build();

    public BookingRequest getBookingRequest(Integer roomsId, LocalDate startDate, LocalDate endDate) {
        List<BookingRequest.BookingRequestRooms> rooms = new ArrayList<>();
        int quantity = 2;
        for (int i=0; i<quantity; i++){
            rooms.add(BookingRequest.BookingRequestRooms.builder()
                    .roomsId(roomsId)
                    .roomsName("roomsName")
                    .guestName("guestName")
                    .guestEmail("guestEmail")
                    .build());
        }
        BookingRequest bookingRequest = BookingRequest.builder()
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
                .priceInCents(5500L * ChronoUnit.DAYS.between(startDate, endDate) * quantity)
                .rooms(rooms)
                .build();
        return bookingRequest;
    }

    public ListByUserRequest listByUserRequest =  ListByUserRequest.builder()
            .status(List.of("RESERVED", "BOOKED", "CANCELLED", "COMPLETED"))
            .startDate(LocalDate.now().minusDays(1000))
            .build();

    public HotelSearchRequest hotelSearchRequest = HotelSearchRequest.builder()
            .city("Amherst")
            .state("Massachusetts")
            .country("United States")
            .latitude(0.0)
            .longitude(0.0)
            .startDate(LocalDate.now().plusDays(1))
            .endDate(LocalDate.now().plusDays(2))
            .numAdult(4)
            .numChild(2)
            .numRoom(2)
            .priceMax(100000000L)
            .priceMin(0L)
            .propertyTypes(List.of())
            .propertyRating(List.of())
            .hotelFacility(List.of())
            .roomsFacility(List.of())
            .build();
}
