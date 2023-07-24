package com.dlim2012.clients.mysql_booking.dto;

import java.time.LocalDate;

public interface DatesWithIds {
    Integer getHotelId();
    Integer getRoomsId();
    Integer getRoomId();
    Long getId();
    LocalDate getStartDate();
    LocalDate getEndDate();
}
