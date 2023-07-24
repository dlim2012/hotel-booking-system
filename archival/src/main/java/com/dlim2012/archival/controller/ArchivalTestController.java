package com.dlim2012.archival.controller;

import com.dlim2012.archival.service.ArchivalService;
import com.dlim2012.clients.mysql_booking.entity.Booking;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/api/v1/archival")
@RequiredArgsConstructor
public class ArchivalTestController {
    private final ArchivalService archivalService;

    @PostMapping("/booking")
    public void postBooking(
            @RequestBody Booking booking
    ){
        archivalService.archiveBookingBatch(List.of(booking));
    }
}
