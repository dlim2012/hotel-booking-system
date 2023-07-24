package com.dlim2012.clients.mysql_booking.dto;

import java.time.LocalDate;

public interface RoomDatesUpdateInfo {
    Integer getHotelId();
    Integer getRoomsId();
    Long getRoomId();
    LocalDate getDatesAddedUntil();
}
