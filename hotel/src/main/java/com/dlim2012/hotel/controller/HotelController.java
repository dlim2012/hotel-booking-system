package com.dlim2012.hotel.controller;

import com.dlim2012.clients.dto.IdItem;
import com.dlim2012.clients.dto.hotel.facility.HotelFacilityItem;
import com.dlim2012.clients.dto.hotel.facility.RoomFacilityItem;
import com.dlim2012.clients.security.service.JwtService;
import com.dlim2012.clients.dto.hotel.HotelItem;
import com.dlim2012.clients.dto.hotel.RoomItem;
import com.dlim2012.clients.dto.hotel.facility.FacilityItem;
import com.dlim2012.hotel.dto.file.HotelImageUrlItem;
import com.dlim2012.hotel.dto.file.RoomImageUrlItem;
import com.dlim2012.hotel.dto.locality.CityItem;
import com.dlim2012.hotel.dto.locality.CountryItem;
import com.dlim2012.hotel.dto.locality.LocalityItem;
import com.dlim2012.hotel.dto.locality.StateItem;
import com.dlim2012.hotel.entity.file.ImageType;
import com.dlim2012.hotel.service.FacilityService;
import com.dlim2012.hotel.service.HotelService;
import com.dlim2012.hotel.service.ImageService;
import com.dlim2012.hotel.service.LocalityService;
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
    private final LocalityService localityService;
    private final FacilityService facilityService;
    private ImageService imageService;
    private final JwtService jwtService;

    // Country (internal), Facility, Hotel, Room, Hotel Image, Room Image, Hotel Facility, Room Facility

    // Post
    @PostMapping(path = "/country")
    void postCountry(
            @RequestBody @Validated({CountryItem.Post.class}) CountryItem CountryItem) {
        localityService.postCountry(CountryItem);
    }

    @PutMapping(path = "/country")
    public void putCountry(
            @RequestBody @Validated({CountryItem.Put.class}) CountryItem countryItem){
        localityService.putCountry(countryItem);
    }

    @PostMapping(path = "/state")
    void postState(
            @RequestBody @Validated({StateItem.Post.class}) StateItem postStateRequest){
        localityService.postState(postStateRequest);
    }

    @PutMapping(path = "/state")
    public void putState(@RequestBody @Validated({StateItem.Put.class}) StateItem stateItem){
        localityService.putState(stateItem);
    }

    @PostMapping(path = "/city")
    void postCity(
            @RequestBody @Validated({CityItem.Post.class}) CityItem CityItem){
        localityService.postCity(CityItem);
    }

    @PutMapping(path = "/city")
    public void putCity(@RequestBody @Validated({CityItem.Put.class}) CityItem cityItem){
        localityService.putCity(cityItem);
    }

    @PostMapping(path = "/locality")
    void postLocality(
            @RequestBody @Validated({LocalityItem.Post.class}) LocalityItem localityItem
    ){
        localityService.postLocality(localityItem); }

    @PutMapping(path = "/locality")
    public void putLocality(@RequestBody @Validated({LocalityItem.Put.class}) LocalityItem localityItem
    ){
        localityService.putLocality(localityItem); }

    @PostMapping(path = "/facility")
    void postFacility(@RequestBody @Validated({FacilityItem.Post.class}) FacilityItem FacilityItem) {
        facilityService.postFacility(FacilityItem);
    }

    @PutMapping(path = "/facility")
    void putFacility(@RequestBody @Validated({FacilityItem.Post.class}) FacilityItem FacilityItem) {
        facilityService.putFacility(FacilityItem);
    }

    @PostMapping(path = "")
    void postHotel(@RequestBody @Validated({HotelItem.Post.class}) HotelItem hotelItem) {
        Integer userId = jwtService.getId();
        String managerEmail = jwtService.getEmail();
        hotelService.postHotel(userId, hotelItem, managerEmail);
    }

    @PutMapping(path = "/{hotelId}")
    public void putHotel(
            @PathVariable("hotelId") Integer hotelId,
            @RequestBody @Validated({LocalityItem.Put.class}) HotelItem hotelItem){
        String userEmail = jwtService.getEmail();
        String managerEmail = jwtService.getEmail();
        hotelService.putHotel(hotelId, hotelItem, managerEmail);
    }

    @PostMapping(path = "/{hotelId}/rooms")
    void postRooms(@PathVariable("hotelId") Integer hotelId, @RequestBody @Validated({RoomItem.Post.class}) RoomItem roomItem){
        String managerEmail = jwtService.getEmail();
        hotelService.postRoom(hotelId, roomItem, managerEmail);
    }

    @PutMapping(path = "/{hotelId}/room/{roomId}")
    public void putRoom(
            @PathVariable("hotelId") Integer hotelId,
            @PathVariable("roomId") Integer roomId,
            @RequestBody @Validated({RoomItem.Put.class}) RoomItem roomItem){
        String managerEmail = jwtService.getEmail();
        hotelService.putRoom(hotelId, roomId, roomItem, managerEmail);
    }

    @PostMapping(path = "/{hotelId}/image")
    void postHotelImage(@PathVariable("hotelId") Integer hotelId, @RequestParam("image")MultipartFile file) throws IOException {
        imageService.postHotelImage(hotelId, file);
    }

    @PostMapping(path = "/{hotelId}/room/{roomId}/image")
    void postRoomImage(@PathVariable("hotelId") Integer hotelId,
                        @PathVariable("roomId") Integer roomId,
                        @RequestParam("image") MultipartFile file) throws IOException{
        imageService.postRoomImage(hotelId, roomId, file);
    }

    @PutMapping(path = "/{hotelId}/facility")
    public void putHotelFacilities(
            @PathVariable("hotelId") Integer hotelId,
            @RequestBody @Validated List<HotelFacilityItem> hotelFacilityItemList
    ){
        facilityService.setHotelFacilities(hotelId, hotelFacilityItemList);
    }

    @PutMapping(path = "/{hotelId}/room/{roomId}/facility")
    public void putRoomFacilities(
            @PathVariable("hotelId") Integer hotelId,
            @PathVariable("roomId") Integer roomId,
            @RequestBody @Validated List<RoomFacilityItem> roomFacilityItemList
    ){
        facilityService.setRoomFacilities(hotelId, roomId, roomFacilityItemList);
    }

    // Get
    @GetMapping(path = "/country")
    List<CountryItem> getCountries(){
        return localityService.getAllCountries();
    }

    @GetMapping(path = "/state")
    List<StateItem> getStates(@RequestBody @Validated IdItem countryIdItem){
        return localityService.getStates(countryIdItem.id());
    }

    @GetMapping(path = "/city")
    List<CityItem> getCities(@RequestBody @Validated IdItem stateIdItem){
        return localityService.getCities(stateIdItem.id());
    }

    @GetMapping(path = "/locality")
    List<LocalityItem> getLocalities(@RequestBody @Validated IdItem cityIdItem) {
        return localityService.getLocalities(cityIdItem.id());}

    @GetMapping(path = "/facility")
    List<FacilityItem> getFacilities(){
        return facilityService.getFacilities();
    }

    @GetMapping(path = "/{hotelId}")
    public HotelItem getHotel(
            @PathVariable("hotelId") Integer hotelId
    ){
        return hotelService.getHotel(hotelId);
    }

    @GetMapping(path = "/{hotelId}/room/{roomId}")
    public RoomItem getRoom(
            @PathVariable("hotelId") Integer hotelId,
            @PathVariable("roomId") Integer roomId
    ){
        return hotelService.getRoom(hotelId, roomId);
    }

    @GetMapping(path = "/{hotelId}/image/{imageType}")
    public List<HotelImageUrlItem> getHotelImageUrls(
            @PathVariable("hotelId") Integer hotelId,
            @PathVariable("imageType") String imageTypeString
    ){
        ImageType imageType = imageService.getImageTypeFromString(imageTypeString);
        return imageService.getHotelImageUrls(hotelId, imageType);
    }


    @GetMapping(path = " /{hotelId}/room/{roomId}/image/{imageType}")
    public List<RoomImageUrlItem> getRoomImageUrls(
            @PathVariable("hotelId") Integer hotelId,
            @PathVariable("roomId") Integer roomId,
            @PathVariable("imageType") String imageTypeString

    ){
        ImageType imageType = imageService.getImageTypeFromString(imageTypeString);
        return imageService.getRoomImageUrls(hotelId, roomId, imageType);
    }

    @GetMapping(path = "/{hotelId}/image/{imageType}/{imageId}")
    public ResponseEntity<?> getHotelImage(
            @PathVariable("hotelId") Integer hotelId,
            @PathVariable("imageType") String imageTypeString,
            @PathVariable("imageId") Integer imageId) throws IOException {
        log.info("Get hotel image requested. (hotel: {}, {}, image: {})", hotelId, imageTypeString, imageId);
        ImageType imageType = imageService.getImageTypeFromString(imageTypeString);
        System.out.println(imageType);
        byte[] image = imageService.getHotelImage(hotelId, imageId, imageType);
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
        ImageType imageType = imageService.getImageTypeFromString(imageTypeString);
        byte[] image = imageService.getRoomImage(hotelId, roomId, imageId, imageType);
        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.valueOf("image/png"))
                .body(image);
    }

    @GetMapping(path = "/{hotelId}/facility")
    public List<FacilityItem> getHotelFacilities(@PathVariable("hotelId") Integer hotelId){
        return facilityService.getHotelFacilities(hotelId);
    }

    @GetMapping(path = "/{hotelId}/room/{roomId}/facility")
    public List<FacilityItem> getRoomFacilities(
            @PathVariable("hotelId") Integer hotelId,
            @PathVariable("roomId") Integer roomId){
        return facilityService.getRoomFacilities(hotelId, roomId);
    }



    // Delete
    @DeleteMapping(path = "/country")
    public void deleteCountries(@Validated List<IdItem> idItemList){
        localityService.deleteCountries(idItemList);
    }

    @DeleteMapping(path = "/state")
    public void deleteStates(@Validated List<IdItem> idItemList){
        localityService.deleteStates(idItemList);
    }


    @DeleteMapping(path = "/city")
    public void deleteCities(@Validated List<IdItem> idItemList){
        localityService.deleteCities(idItemList);
    }

    @DeleteMapping(path = "/locality")
    public void deleteLocalities(@Validated List<IdItem> idItemList){
        localityService.deleteLocalities(idItemList);
    }

    @DeleteMapping(path = "/facility")
    public void deleteFacilities(List<IdItem> idItemList){
        facilityService.deleteFacilities(idItemList);
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

    @DeleteMapping(path = "/{hotelId}/image")
    public void deleteHotelImages(
            @PathVariable("hotelId") Integer hotelId,
            @RequestBody @Validated List<IdItem> imageIdList){
        imageService.deleteHotelImages(hotelId, imageIdList);
    }

    @DeleteMapping(path = "/{hotelId}/room/{roomId}/image")
    public void deleteRoomImages(
            @PathVariable("hotelId") Integer hotelId,
            @PathVariable("roomId") Integer roomId,
            @RequestBody @Validated List<IdItem> imageIdList){
        imageService.deleteRoomImages(hotelId, roomId, imageIdList);
    }
}
