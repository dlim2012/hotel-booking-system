package com.dlim2012.hotel.controller;

import com.dlim2012.hotel.DeleteRequest;
import com.dlim2012.hotel.entity.file.ImageType;
import com.dlim2012.hotel.service.HotelService;
import com.dlim2012.dto.HotelFullAddressItem;
import com.dlim2012.dto.HotelItem;
import com.dlim2012.dto.IdItem;
import com.dlim2012.dto.RoomItem;
import com.dlim2012.dto.facility.FacilityItem;
import com.dlim2012.dto.file.HotelImageUrlItem;
import com.dlim2012.dto.file.RoomImageUrlItem;
import com.dlim2012.dto.locality.CityItem;
import com.dlim2012.dto.locality.CountryItem;
import com.dlim2012.dto.locality.StateItem;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/api/v1/hotel")
@RequiredArgsConstructor
@CrossOrigin
public class HotelController {
    // v: add a hotel service and tables
    // v: add REST API for countries and add countries manually
    // v: add REST API for hotel, rooms, facility, imageUrl
    // v: add Kafka
    // todo: add rest apis for locality

    private final HotelService hotelService;

    // Country (internal), Facility, Hotel, Room, Hotel Image, Room Image, Hotel Facility, Room Facility

    // Post
    @PostMapping(path = "/country")
    void postCountry(@RequestBody CountryItem CountryItem) {
        hotelService.postCountry(CountryItem);
    }

    @PostMapping(path = "/state")
    void postState(@RequestBody StateItem postStateRequest){
        hotelService.postState(postStateRequest);
    }

    @PostMapping(path = "/city")
    void postCity(@RequestBody CityItem CityItem){
        hotelService.postCity(CityItem);
    }


    @PostMapping(path = "/facility")
    void postFacility(@RequestBody FacilityItem FacilityItem) {
        hotelService.postFacility(FacilityItem);
    }

    @PostMapping(path = "")
    void postHotel(@RequestBody @Validated({HotelFullAddressItem.HotelValidation.class}) HotelFullAddressItem hotelFullAddressItem) {
        hotelService.postHotel(hotelFullAddressItem);
    }

    @PostMapping(path = "/{hotelId}/rooms")
    void postRooms(@PathVariable("hotelId") Integer hotelId, @RequestBody List<RoomItem> RoomItemList){
        hotelService.postRoom(hotelId, RoomItemList);
    }

    @PostMapping(path = "/{hotelId}/image")
    void postHotelImage(@PathVariable("hotelId") Integer hotelId, @RequestParam("image")MultipartFile file) throws IOException {
        hotelService.postHotelImage(hotelId, file);
    }

    @PostMapping(path = "/{hotelId}/room/{roomId}/image")
    void postRoomImage(@PathVariable("hotelId") Integer hotelId,
                        @PathVariable("roomId") Integer roomId,
                        @RequestParam("image") MultipartFile file) throws IOException{
        hotelService.postRoomImage(hotelId, roomId, file);
    }

    // Get
    @GetMapping(path = "/country")
    List<CountryItem> getCountries(){
        return hotelService.getAllCountries();
    }

    @GetMapping(path = "/state")
    List<StateItem> getStates(@RequestBody IdItem idItem){
        return hotelService.getStates(idItem.id());
    }

    @GetMapping(path = "/city")
    List<CityItem> getCity(@RequestBody IdItem idItem){
        return hotelService.getCities(idItem.id());
    }

    @GetMapping(path = "/facility")
    List<FacilityItem> getFacilities(){
        return hotelService.getFacilities();
    }

    @GetMapping(path = "/{hotelId}")
    public HotelItem getHotel(
            @PathVariable("hotelId") Integer hotelId
    ){
        return hotelService.getHotel(hotelId);
    }

    @GetMapping(path = "/{hotelId}/image/{imageType}")
    public List<HotelImageUrlItem> getHotelImageUrls(
            @PathVariable("hotelId") Integer hotelId,
            @PathVariable("imageType") String imageTypeString
    ){
        ImageType imageType = hotelService.getImageTypeFromString(imageTypeString);
        return hotelService.getHotelImageUrls(hotelId, imageType);
    }


    @GetMapping(path = " /{hotelId}/room/{roomId}/image/{imageType}")
    public List<RoomImageUrlItem> getRoomImageUrls(
            @PathVariable("hotelId") Integer hotelId,
            @PathVariable("roomId") Integer roomId,
            @PathVariable("imageType") String imageTypeString

    ){
        ImageType imageType = hotelService.getImageTypeFromString(imageTypeString);
        return hotelService.getRoomImageUrls(hotelId, roomId, imageType);
    }

    @GetMapping(path = "/{hotelId}/image/{imageType}/{imageId}")
    public ResponseEntity<?> getHotelImage(
            @PathVariable("hotelId") Integer hotelId,
            @PathVariable("imageType") String imageTypeString,
            @PathVariable("imageId") Integer imageId) throws IOException {
        log.info("Get hotel image requested. (hotel: {}, {}, image: {})", hotelId, imageTypeString, imageId);
        ImageType imageType = hotelService.getImageTypeFromString(imageTypeString);
        System.out.println(imageType);
        byte[] image = hotelService.getHotelImage(hotelId, imageId, imageType);
        return ResponseEntity.status(HttpStatus.OK)
                        .contentType(MediaType.valueOf("image/png"))
                                .body(image);
    }


    @GetMapping(path = "/{hotelId}/room/{roomId}/image/{imageType}/{imageId}")
    public ResponseEntity<?> getRoomImage(
            @PathVariable("hotelId") Integer hotelId,
            @PathVariable("roomId") Integer roomId,
            @PathVariable("imageType") String imageTypeString,
            @PathVariable("imageId") Integer imageId) throws IOException {
        log.info("Get room image requested. (hotel: {}, room: {}, {}, image: {})", hotelId, roomId, imageTypeString, imageId);
        ImageType imageType = hotelService.getImageTypeFromString(imageTypeString);
        byte[] image = hotelService.getRoomImage(hotelId, roomId, imageId, imageType);
        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.valueOf("image/png"))
                .body(image);
    }

    @GetMapping(path = "/{hotelId}/facility")
    public List<FacilityItem> getHotelFacilities(){
        //todo
        return hotelService.getHotelFacilities();
    }

    @GetMapping(path = "/{hotelId}/room/{roomId}/facility")
    public List<FacilityItem> getRoomFacilities(){
        //todo
        return hotelService.getRoomFacilities();
    }


    // Put

    @PutMapping(path = "/country")
    public void putCountry(@RequestBody CountryItem countryItem){
        hotelService.putCountry(countryItem);
    }

    @PutMapping(path = "/state")
    public void putState(@RequestBody StateItem stateItem){
        hotelService.putState(stateItem);
    }

    @PutMapping(path = "/city")
    public void putCity(@RequestBody CityItem cityItem){
        hotelService.putCity(cityItem);
    }

    @PutMapping(path = "/{hotelId}")
    public void putHotel(
            @PathVariable("hotelId") Integer hotelId,
            @RequestBody @Valid HotelItem hotelItem){
        hotelService.putHotel(hotelId, hotelItem);
    }

    @PutMapping(path = "/{hotelId}/room/{roomId}")
    public void putRoom(
            @PathVariable("hotelId") Integer hotelId,
            @PathVariable("roomId") Integer roomId,
            @RequestBody @Valid RoomItem roomItem){
        hotelService.putRoom(hotelId, roomId, roomItem);
    }

    @PutMapping(path = "/{hotelId}/facility")
    public void putHotelFacilities(
            @PathVariable("hotelId") Integer hotelId,
            @RequestBody List<IdItem> facilityItemList
    ){
        hotelService.putHotelFacilities(hotelId, facilityItemList);
    }

    @PutMapping(path = "/{hotelId}/room/{roomId}/facility")
    public void putRoomFacilities(
            @PathVariable("hotelId") Integer hotelId,
            @PathVariable("roomId") Integer roomId,
            @RequestBody List<IdItem> facilityItemList
    ){
        hotelService.putRoomFacilities(hotelId, roomId, facilityItemList);
    }

    // Delete
    @DeleteMapping(path = "/country")
    public void deleteCountries(List<DeleteRequest> deleteRequestList){
        hotelService.deleteCountries(deleteRequestList);
    }

    @DeleteMapping(path = "/facility")
    public void deleteFacilities(List<DeleteRequest> deleteRequestList){
        hotelService.deleteFacilities(deleteRequestList);
    }

    @DeleteMapping(path = "/{hotelId}")
    public void deleteHotel(
            @PathVariable("hotelId") Integer hotelId
    ){
        hotelService.deleteHotel(hotelId);
    }

    @DeleteMapping(path = "/{hotelId}/room/{roomId}")
    public void deleteRoom(
            @PathVariable("hotelId") Integer hotelId,
            @PathVariable("roomId") Integer roomId
    ){
        hotelService.deleteRoom(hotelId, roomId);
    }

    @DeleteMapping(path = "/{hotelId}/image/{imageType}/{imageId}")
    public void deleteHotelImage(
            @PathVariable("hotelId") Integer hotelId,
            @PathVariable("imageType") String imageTypeString,
            @PathVariable("imageId") Integer imageId){
        ImageType imageType = hotelService.getImageTypeFromString(imageTypeString);
        hotelService.deleteHotelImage(hotelId, imageType, imageId);
    }

    @DeleteMapping(path = "/{hotelId}/room/{roomId}/image/{imageType}/{imageId}")
    public void deleteRoomImage(
            @PathVariable("hotelId") Integer hotelId,
            @PathVariable("roomId") Integer roomId,
            @PathVariable("imageType") String imageTypeString,
            @PathVariable("imageId") Integer imageId){
        ImageType imageType = hotelService.getImageTypeFromString(imageTypeString);
        hotelService.deleteRoomImage(hotelId, roomId, imageType, imageId);
    }


}
