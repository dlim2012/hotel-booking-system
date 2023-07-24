package com.dlim2012.bookingmanagement.dto.hotelInfo;

import com.dlim2012.clients.entity.BookingMainStatus;
import com.dlim2012.clients.mysql_booking.entity.Dates;
import com.dlim2012.clients.mysql_booking.entity.Room;
import com.dlim2012.clients.mysql_booking.entity.Rooms;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
public class HotelMainInfoResponse {
    LocalDate recordStartDate;
    int recordNumBooking;
    long recordTotalPrice;

    int numRooms;
    int numRoom;
    int availableDates;

    int numReserved;
    int numReservedDates;
    int numBooked;
    int numBookedDates;
    int numReservedOutOfRange;
    int numReservedDatesOutOfRange;
    int numBookedOutOfRange;
    int numBookedDatesOutOfRange;

    public HotelMainInfoResponse() {
        this.recordStartDate = null;
        this.recordNumBooking = 0;
        this.recordTotalPrice = 0L;
        this.numRooms = 0;
        this.numRoom = 0;
        this.availableDates = 0;
        this.numReserved = 0;
        this.numReservedDates = 0;
        this.numBooked = 0;
        this.numBookedDates = 0;
        this.numReservedOutOfRange = 0;
        this.numReservedDatesOutOfRange = 0;
        this.numBookedOutOfRange = 0;
        this.numBookedDatesOutOfRange = 0;
    }

    public void addRooms(Set<Rooms> roomsSet){
        this.numRooms += roomsSet.size();
        for (Rooms rooms: roomsSet){
            this.numRoom += rooms.getRoomSet().size();
            for (Room room: rooms.getRoomSet()){
                for (Dates dates: room.getDatesSet()){
                    this.availableDates += (int) ChronoUnit.DAYS.between(
                            dates.getStartDate(), dates.getEndDate());
                }
            }
        }
    }

    public void addDateRange(int dates, BookingMainStatus status, boolean outOfRange){
        if (status.equals(BookingMainStatus.RESERVED)){
            if (outOfRange){
                this.numReservedOutOfRange += 1;
                this.numReservedDatesOutOfRange += dates;

            } else {
                this.numReserved += 1;
                this.numReservedDates += dates;
            }
        } else if (status.equals(BookingMainStatus.BOOKED)){
            if (outOfRange){
                this.numBookedOutOfRange += 1;
                this.numBookedDatesOutOfRange += dates;

            } else {
                this.numBooked += 1;
                this.numBookedDates += dates;
            }
        } else {
            throw new IllegalArgumentException("Illegal argument.");
        }
    }

    public void addTotalPrice(long priceInCents){
        this.recordTotalPrice += priceInCents;
    }
}
