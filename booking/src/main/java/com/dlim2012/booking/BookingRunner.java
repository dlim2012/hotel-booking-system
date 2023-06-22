package com.dlim2012.booking;

import com.dlim2012.booking.controller.PaypalController;
import com.dlim2012.clients.mysql_booking.entity.Booking;
import com.dlim2012.clients.mysql_booking.repository.AvailableRoomRepository;
import com.dlim2012.booking.service.AvailableRoomService;
import com.dlim2012.booking.service.BookingKafkaProducerService;
import com.dlim2012.booking.service.BookingService;
import com.dlim2012.clients.dto.booking.BookingItem;
import com.dlim2012.clients.dto.hotel.RoomItem;
import com.dlim2012.clients.entity.BookingStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

@RequiredArgsConstructor
@Component
public class BookingRunner implements CommandLineRunner {

//    private final RedisTemplate<String, Object> redisTemplate;
    // docker exec -it hotel-booking-redis-server redis-cli
    private final AvailableRoomRepository availableRoomRepository;
    private final AvailableRoomService availableRoomService;
    private final BookingService bookingService;
//    private final Receiver receiver;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;
    private final PaypalController paypalController;
    private final BookingKafkaProducerService bookingKafkaProducerService;

    @Override
    public void run(String... args) throws Exception {

        availableRoomService.updateRoom(
                RoomItem.builder()
                        .id(-1)
                        .hotelId(-1)
                        .displayName("displayName")
                        .description("description")
                        .isActive(true)
                        .maxAdult(2)
                        .maxChild(1)
                        .quantity(10)
                        .priceMin(10.0)
                        .priceMax(15.0)
                        .availableFrom(LocalDate.now())
                        .availableUntil(LocalDate.now().plusDays(30))
                        .build()
        );
        BookingItem bookingItem1 = BookingItem.builder()
                .userId(-1)
                .hotelId(-1)
                .roomId(-1)
                .startDateTime(LocalDateTime.now())
                .endDateTime(LocalDateTime.now().plusDays(3))
                .quantity(1)
                .priceInCents(10000L)
                .build();
        Booking booking = bookingService.reserveBook(-1, -1, -1, bookingItem1);

        bookingService.saveInvoiceId(booking, "paymentId");;
        bookingService.saveBookingStatus(booking, BookingStatus.BOOKED);
        bookingKafkaProducerService.sendBookingItem(booking);



//        Integer res = availableRoomRepository.conditionalDecreaseQuantityByRoomIdAndDateBetween(
//                -1, LocalDate.now(), LocalDate.now().plus(1, ChronoUnit.DAYS), 1
//        );
//        System.out.println(res);



//        BookingItem bookingItem2 = new BookingItem(
//                2, 2, LocalDate.now().minus(1, ChronoUnit.DAYS), LocalDate.now().plus(1, ChronoUnit.DAYS),
//                2
//        );
//        bookingService.book(1, bookingItem2);

//        String json = objectMapper.writeValueAsString(bookingItem2);
//        System.out.println(json);
//        BookingItem _bookingItem2 = objectMapper.readValue(json, BookingItem.class);
//        System.out.println(_bookingItem2);

//		StringRedisTemplate template = ctx.getBean(StringRedisTemplate.class);
//        Receiver receiver = ctx.getBean(Receiver.class);

//        redisTemplate.expire("chat", 5, TimeUnit.SECONDS);
//        while (receiver.getCount() < 100) {
//
//            System.out.println("Sending message...");
//
//            redisTemplate.convertAndSend("chat", "Hello from Redis!");
//            Thread.sleep(500L);
//        }
        System.out.println("run finished");
    }
}
