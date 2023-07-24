package com.dlim2012.bookingmanagement.dto.booking;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookingArchiveItem {
    Long id;
    Integer hotelId;
    Integer userId;
    String hotelName;
    String address;
    String neighborhood;
    String city;
    String state;
    String country;
    LocalDateTime startDateTime;
    LocalDateTime endDateTime;
    Long priceInCents;
    String invoiceId;
    LocalDateTime invoiceConfirmTime;
    String mainStatus;
    String status;
    List<Rooms> rooms;

//    private String firstName;
//    private String lastName;
//    private String email;
//    private String specialRequests;
//    private Integer estimatedArrivalHour;


    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Rooms {
        Integer roomsId;
        String roomsName;
//        Integer quantity;
    }

    public void addAddress(){
        String address = "";
        if (this.getNeighborhood() != null && !this.getNeighborhood().isEmpty()){
            address += this.getNeighborhood() + ", ";
        }
        address += this.getCity() + ", ";
        address += this.getState() + ", ";
        address += this.getCountry();
        this.setAddress(address);
    }

//    public void addQuantity(){
//        this.quantity = 0;
//        rooms.forEach(rooms1 -> {this.quantity += rooms1.quantity;});
//    }

    public void addAggs(){
        addAddress();
//        addQuantity();
    }

}
