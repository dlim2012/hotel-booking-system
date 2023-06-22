package com.dlim2012.booking.controller;


import com.dlim2012.booking.service.AvailableRoomService;
import com.dlim2012.booking.service.BookingService;
import com.dlim2012.booking.service.PaypalService;
import com.dlim2012.clients.dto.hotel.RoomItem;
import com.dlim2012.clients.etc.Pair;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.io.IOException;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class BookingKafkaListeners {

    private final AvailableRoomService availableRoomService;
    private final BookingService bookingService;
    private final PaypalService paypalService;

    @KafkaListener(topics="room", containerFactory = "roomItemKafkaListenerContainerFactory", groupId = "room-booking")
    void roomListener(@Validated({RoomItem.SearchConsumer.class}) RoomItem roomItem) throws IOException {
        // manage default availabilities in this function
        // for micro adjustment, hotel-manager should be connected to booking service directly
        log.info("Listener received: {} with id {} (quantity: {})", "room",
                roomItem.getId(), roomItem.getQuantity());
        List<Pair<String, Long>> invoiceToCancel = availableRoomService.updateRoom(roomItem);
        for (Pair<String, Long> p: invoiceToCancel){
            paypalService.cancelPayment(p.getL(), p.getR());
        }
    }

}
