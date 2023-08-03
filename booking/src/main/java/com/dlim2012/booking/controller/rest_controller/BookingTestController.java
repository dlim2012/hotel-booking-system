//package com.dlim2012.booking.controller.rest_controller;
//
//import com.dlim2012.booking.dto.reserve.BookingRequest;
//import com.dlim2012.booking.dto.reserve.BookingResponse;
//import com.dlim2012.booking.dto.reserve.ReserveResponse;
//import com.dlim2012.booking.service.CacheService;
//import com.dlim2012.booking.service.UserService;
//import com.dlim2012.booking.service.booking_entity.BookingService;
//import com.dlim2012.clients.entity.BookingMainStatus;
//import com.dlim2012.clients.entity.BookingStatus;
//import com.dlim2012.clients.exception.ResourceNotFoundException;
//import com.dlim2012.clients.mysql_booking.entity.*;
//import com.dlim2012.clients.mysql_booking.repository.BookingRepository;
//import com.dlim2012.clients.mysql_booking.repository.HotelRepository;
//import com.dlim2012.clients.mysql_booking.repository.RoomsRepository;
//import com.dlim2012.security.service.JwtService;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.security.oauth2.jwt.Jwt;
//import org.springframework.security.oauth2.jwt.JwtDecoder;
//import org.springframework.web.bind.annotation.*;
//
//import java.time.LocalDateTime;
//import java.time.temporal.ChronoUnit;
//import java.util.*;
//
//@RestController
//@Slf4j
//@RequestMapping("/api/v1/booking/test")
//@RequiredArgsConstructor
//public class BookingTestController {
//    private final BookingService bookingService;
//    private final UserService userService;
//    private final JwtService jwtService;
//    private final JwtDecoder jwtDecoder;
//    private final CacheService cacheService;
//
//    private final HotelRepository hotelRepository;
//    private final BookingRepository bookingRepository;
//    private final RoomsRepository roomsRepository;
//
//
//    @GetMapping("")
//    public String test(){
//        return "Test";
//    }
//
//    // for testing
//    @GetMapping("/hotel/{hotelId}")
//    public Hotel getHotel(
//            @PathVariable("hotelId") Integer hotelId
//    ){
//        System.out.println(hotelRepository.findAll());
//        System.out.println(roomsRepository.findAll());
//        Hotel hotel = hotelRepository.findById(hotelId)
//                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found"));
//
//        for (Rooms rooms: hotel.getRoomsSet()) {
//            for (Room room : rooms.getRoomSet()) {
//                for (Dates dates : room.getDatesSet()) {
//                    dates.setRoom(null);
//                }
//                room.setRooms(null);
//            }
//            for (Price price : rooms.getPriceList()) {
//                price.setRooms(null);
//            }
//            rooms.setHotel(null);
//        }
//        return hotel;
//    }
//
//    @GetMapping("/hotel/{hotelId}/rooms")
//    public Set<Rooms> getRooms(
//            @PathVariable("hotelId") Integer hotelId
//    ){
//        Set<Rooms> roomsSet = roomsRepository.findByHotelId(hotelId);
//        for (Rooms rooms: roomsSet) {
//            for (Room room : rooms.getRoomSet()) {
//                for (Dates dates : room.getDatesSet()) {
//                    dates.setRoom(null);
//                }
//                room.setRooms(null);
//            }
//            for (Price price : rooms.getPriceList()) {
//                price.setRooms(null);
//            }
//        }
//        return roomsSet;
//    }
//
//    @GetMapping("/booking/{bookingId}")
//    public Booking getBooking(
//            @PathVariable("bookingId") Long bookingId
//    ){
//        Booking booking = bookingRepository.findById(bookingId)
//                .orElseThrow(() -> new ResourceNotFoundException("Booking not found."));
//        for (BookingRooms bookingRooms: booking.getBookingRooms()){
//            for (BookingRoom bookingRoom: bookingRooms.getBookingRoomList()){
//                bookingRoom.setBookingRooms(null);
//            }
//            bookingRooms.setBooking(null);
//        }
//        return bookingRepository.findById(bookingId)
//                .orElseThrow(() -> new ResourceNotFoundException("Booking not found."));
//    }
//
//    @DeleteMapping("/booking/{bookingId}/timeout")
//    public void bookingTimeout(
//            @PathVariable("bookingId") Long bookingId
//    ){
//        bookingService.processPaymentCancelledIfStatusReservedForTimeOut(bookingId, BookingStatus.CANCELLED_PAYMENT_TIME_EXPIRED);
//    }
//
//    @DeleteMapping("/delete")
//    public void deleteAll(){
//        Integer userId = jwtService.getId();
//        bookingRepository.deleteByHotelManagerId(userId);
//        hotelRepository.deleteByHotelManagerId(userId);
//    }
//
//    @PostMapping(path = "/hotel/{hotelId}/reserve")
//    public ReserveResponse reserveHotel(
//            @PathVariable("hotelId") Integer hotelId,
//            @RequestBody BookingRequest bookingRequest,
//            @RequestHeader (name="Authorization") String token
//    ){
//
//        Jwt jwt = jwtDecoder.decode(token);
//        Integer userId = jwtService.getId(jwt);
//        log.info("Reserve requested for hotel {} by user {}", hotelId, userId);
//        Booking booking = bookingService.reserveHotel(hotelId, userId, bookingRequest);
//        if (booking == null){
//            return new ReserveResponse(-1L, false);
//        }
//        return new ReserveResponse(
//                booking.getId(),
//                booking.getStatus().equals(BookingStatus.RESERVED)
//        );
//    }
//
//    @PostMapping(path = "/hotel/{hotelId}/book")
//    public BookingResponse bookHotel(
//            @PathVariable("hotelId") Integer hotelId,
//            @RequestBody BookingRequest bookingRequest,
//            @RequestHeader (name="Authorization") String token
//    ){
//        Jwt jwt = jwtDecoder.decode(token);
//        Integer userId = jwtService.getId(jwt);
//        log.info("Booking requested for hotel {} by user {}", hotelId, userId);
//        return bookingService.bookHotel(hotelId, userId, bookingRequest);
//    }
//
//    @PutMapping(path = "/booking/{bookingId}/user")
//    public void cancelBookingByUser(
//            @PathVariable("bookingId") Long bookingId,
//            @RequestHeader (name="Authorization") String token
//    ){
//        Jwt jwt = jwtDecoder.decode(token);
//        Integer userId = jwtService.getId(jwt);
//        log.info("Booking cancellation requested by app user {}: booking {}", userId, bookingId);
//        userService.cancelBookingByUser(bookingId, userId);
//    }
//
//    @PutMapping(path = "/booking/{bookingId}/process")
//    public void processBooking(
//            @PathVariable("bookingId") Long bookingId,
//            @RequestHeader (name="Authorization") String token
//    ){
//        cacheService.cacheBookingIdEvict(bookingId);
//        Optional<Booking> optionalBooking = bookingRepository.findByIdWithLock(bookingId);
//        Booking booking = optionalBooking.get();
//
//
//        booking.setMainStatus(BookingMainStatus.BOOKED);
//        booking.setStatus(BookingStatus.BOOKED);
//        booking.setInvoiceConfirmTime(LocalDateTime.now());
//        bookingRepository.save(booking);
//        System.out.println(bookingRepository.findById(bookingId));
//
//
//        // adjust bookingDates
//        Map<Integer, Integer> bookingNumRoom = new HashMap<>();
//        for (BookingRooms bookingRooms: booking.getBookingRooms()){
//            bookingNumRoom.put(bookingRooms.getRoomsId(), bookingRooms.getBookingRoomList().size());
//        }
//
//        List<Rooms> roomsList = roomsRepository.findByHotelIdWithLock(booking.getHotelId());
//        int numDates = 0;
//        for (BookingRooms bookingRooms: booking.getBookingRooms()){
//            for (BookingRoom bookingRoom: bookingRooms.getBookingRoomList()){
//                numDates += Math.toIntExact(
//                        ChronoUnit.DAYS.between(
//                                bookingRoom.getStartDateTime().toLocalDate(),
//                                bookingRoom.getEndDateTime().toLocalDate()
//                        )
//                );
//            }
//        }
//        for (Rooms rooms: roomsList){
//            Integer numRoom = bookingNumRoom.getOrDefault(rooms.getId(), 0);
//            if (numRoom > 0) {
//                rooms.setDatesReserved(rooms.getDatesReserved() - numRoom * numDates);
//                rooms.setDatesBooked(rooms.getDatesBooked() + numRoom * numDates);
//            }
//        }
//        roomsRepository.saveAll(roomsList);
//
//    }
//
//}
