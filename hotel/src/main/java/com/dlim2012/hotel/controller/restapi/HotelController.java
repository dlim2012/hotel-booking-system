package com.dlim2012.hotel.controller.restapi;

import com.dlim2012.clients.dto.IdItem;
import com.dlim2012.hotel.dto.hotel.list.HotelRowItem;
import com.dlim2012.hotel.dto.hotel.profile.HotelAddressItem;
import com.dlim2012.hotel.dto.hotel.profile.HotelFacilityItem;
import com.dlim2012.hotel.dto.hotel.profile.HotelGeneralInfoItem;
import com.dlim2012.hotel.dto.hotel.profile.HotelIsActiveItem;
import com.dlim2012.hotel.dto.hotel.registration.HotelRegisterRequest;
import com.dlim2012.hotel.service.FacilityService;
import com.dlim2012.hotel.service.HotelService;
import com.dlim2012.hotel.service.LocalityService;
import com.dlim2012.hotel.service.RoomsService;
import com.dlim2012.security.service.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/api/v1/hotel")
@RequiredArgsConstructor
@CrossOrigin
public class HotelController {

    private final HotelService hotelService;
    private final RoomsService roomsService;
    private final LocalityService localityService;
    private final FacilityService facilityService;
    private final JwtService jwtService;

    /*
    Registration: Add and delete
     */
    @PostMapping(path = "/hotel/register")
    IdItem postHotel(@RequestBody HotelRegisterRequest request) {
        Integer userId = jwtService.getId();
        log.info("Hotel register requested from user {}.", userId);
        return IdItem.builder()
                .id(hotelService.postHotel(userId, request, false).getId())
                .build();
    }



    /*
    Get Info
     */
    @GetMapping(path = "/manage")
    public List<HotelRowItem> getHotels(){
        Integer userId = jwtService.getId();
        log.info("Get hotels requested from user {}.", userId);
        return hotelService.getUserHotels(userId);
    }

    /*
    Profile - General Info
     */
    @GetMapping(path= "/hotel/{hotelId}/info")
    public HotelGeneralInfoItem getHotelGeneralInfo(
            @PathVariable("hotelId") Integer hotelId
    ){
        Integer userId = jwtService.getId();
        log.info("Get hotel {} requested: general info", hotelId);
        return hotelService.getGeneralInfo(hotelId, userId);
    }

    @PutMapping(path = "/hotel/{hotelId}/info")
    public void putHotelGeneralInfo(
            @PathVariable("hotelId") Integer hotelId,
            @RequestBody HotelGeneralInfoItem hotelGeneralInfoItem
    ){
        Integer userId = jwtService.getId();
        log.info("Put hotel {} requested: general info", hotelId);
        hotelService.putHotelGeneralInfo(hotelId, userId, hotelGeneralInfoItem);
    }

    /*
    Profile - Facility
     */
    @GetMapping(path= "/hotel/{hotelId}/facility")
    public HotelFacilityItem getHotelFacilities(
            @PathVariable("hotelId") Integer hotelId
    ){
        Integer userId = jwtService.getId();
        log.info("Get hotel {} requested: facility", hotelId);
        return hotelService.getHotelFacilities(hotelId, userId);
    }

    @PutMapping(path= "/hotel/{hotelId}/facility")
    public void PutHotelFacilities(
            @PathVariable("hotelId") Integer hotelId,
            @RequestBody HotelFacilityItem hotelFacilityItem
    ){
        Integer userId = jwtService.getId();
        log.info("Put hotel {} requested: facility", hotelId);
        hotelService.putHotelFacilities(hotelId, userId, hotelFacilityItem);
    }

    /*
    Profile - Address
     */
    @GetMapping(path= "/hotel/{hotelId}/address")
    public HotelAddressItem getHotelAddress(
            @PathVariable("hotelId") Integer hotelId
    ){
        Integer userId = jwtService.getId();
        log.info("Get hotel {} requested: address", hotelId);
        return hotelService.getHotelAddress(hotelId, userId);
    }

    @PutMapping(path= "/hotel/{hotelId}/address")
    public void putHotelAddress(
            @PathVariable("hotelId") Integer hotelId,
            @RequestBody HotelAddressItem hotelAddressItem
    ){
        Integer userId = jwtService.getId();
        log.info("Put hotel {} requested: address", hotelId);
        hotelService.putHotelAddress(hotelId, userId, hotelAddressItem);
    }

    @GetMapping(path = "/hotel/{hotelId}/is-active")
    public HotelIsActiveItem getHotelIsActive(
            @PathVariable("hotelId") Integer hotelId
    ){
        Integer userId = jwtService.getId();
        log.info("Get hotel {} is active requested.", hotelId);
        return hotelService.getHotelIsActive(hotelId, userId);
    }

    @PutMapping(path = "/hotel/{hotelId}/activate")
    public void getHotelActivate(
            @PathVariable("hotelId") Integer hotelId
            ){
        Integer userId = jwtService.getId();
        log.info("Hotel {} activate requested.", hotelId);
        hotelService.activate(hotelId, userId);
    }

    @PutMapping(path = "/hotel/{hotelId}/inactivate")
    public void getHotelInActivate(
            @PathVariable("hotelId") Integer hotelId
            ){
        Integer userId = jwtService.getId();
        log.info("Get hotel {} inactivate requested.", hotelId);
        hotelService.inActivate(hotelId, userId);
    }

    @DeleteMapping(path = "/hotel/{hotelId}")
    void deleteHotel(
            @PathVariable("hotelId") Integer hotelId
    ){
        Integer userId = jwtService.getId();
        log.info("Hotel delete requested from user {}.", userId);
        hotelService.deleteHotel(hotelId, userId);
    }

//    @GetMapping(path = "/{hotelId}/room/{roomId}")
//    public RoomItem getRoom(
//            @PathVariable("hotelId") Integer hotelId,
//            @PathVariable("roomId") Integer roomId
//    ){
//        return hotelService.getRoom(hotelId, roomId);
//    }

//    @GetMapping(path = "/{hotelId}/image/{imageType}")
//    public List<HotelImageUrlItem> getHotelImageUrls(
//            @PathVariable("hotelId") Integer hotelId,
//            @PathVariable("imageType") String imageTypeString
//    ){
//        ImageType imageType = imageService.getImageTypeFromString(imageTypeString);
//        return imageService.getHotelImageUrls(hotelId, imageType);
//    }


//    @GetMapping(path = " /{hotelId}/room/{roomId}/image/{imageType}")
//    public List<RoomImageUrlItem> getRoomImageUrls(
//            @PathVariable("hotelId") Integer hotelId,
//            @PathVariable("roomId") Integer roomId,
//            @PathVariable("imageType") String imageTypeString
//    ){
//        ImageType imageType = imageService.getImageTypeFromString(imageTypeString);
//        return imageService.getRoomImageUrls(hotelId, roomId, imageType);
//    }

//    @GetMapping(path = "/{hotelId}/image/{imageType}/{imageId}")
//    public ResponseEntity<?> getHotelImage(
//            @PathVariable("hotelId") Integer hotelId,
//            @PathVariable("imageType") String imageTypeString,
//            @PathVariable("imageId") Integer imageId) throws IOException {
//        log.info("Get hotel image requested. (hotel: {}, {}, image: {})", hotelId, imageTypeString, imageId);
//        ImageType imageType = imageService.getImageTypeFromString(imageTypeString);
//        System.out.println(imageType);
//        byte[] image = imageService.getHotelImage(hotelId, imageId, imageType);
//        return ResponseEntity.status(HttpStatus.OK)
//                        .contentType(MediaType.valueOf("image/png"))
//                                .body(image);
//    }


//    @GetMapping(path = "/{hotelId}/room/{roomId}/image/{imageType}/{imageId}")
//    public ResponseEntity<?> getRoomImage(
//            @PathVariable("hotelId") Integer hotelId,
//            @PathVariable("roomId") Integer roomId,
//            @PathVariable("imageType") String imageTypeString,
//            @PathVariable("imageId") Integer imageId) throws IOException {
//        log.info("Get room image requested. (hotel: {}, room: {}, {}, image: {})", hotelId, roomId, imageTypeString, imageId);
//        ImageType imageType = imageService.getImageTypeFromString(imageTypeString);
//        byte[] image = imageService.getRoomImage(hotelId, roomId, imageId, imageType);
//        return ResponseEntity.status(HttpStatus.OK)
//                .contentType(MediaType.valueOf("image/png"))
//                .body(image);
//    }

//    @GetMapping(path = "/{hotelId}/facility")
//    public List<FacilityItem> getHotelFacilities(@PathVariable("hotelId") Integer hotelId){
//        return facilityService.getHotelFacilities(hotelId);
//    }

//    @GetMapping(path = "/{hotelId}/room/{roomId}/facility")
//    public List<FacilityItem> getRoomFacilities(
//            @PathVariable("hotelId") Integer hotelId,
//            @PathVariable("roomId") Integer roomId){
//        return facilityService.getRoomFacilities(hotelId, roomId);
//    }



    // Delete
//    @DeleteMapping(path = "/country")
//    public void deleteCountries(@Validated List<IdItem> idItemList){
//        localityService.deleteCountries(idItemList);
//    }

//    @DeleteMapping(path = "/state")
//    public void deleteStates(@Validated List<IdItem> idItemList){
//        localityService.deleteStates(idItemList);
//    }


//    @DeleteMapping(path = "/city")
//    public void deleteCities(@Validated List<IdItem> idItemList){
//        localityService.deleteCities(idItemList);
//    }

//    @DeleteMapping(path = "/locality")
//    public void deleteLocalities(@Validated List<IdItem> idItemList){
//        localityService.deleteLocalities(idItemList);
//    }

//    @DeleteMapping(path = "/facility")
//    public void deleteFacilities(List<IdItem> idItemList){
//        facilityService.deleteFacilities(idItemList);
//    }

//    @DeleteMapping(path = "/{hotelId}")
//    public void deleteHotel(
//            @PathVariable("hotelId") Integer hotelId
//    ){
//        hotelService.deleteHotel(hotelId);
//    }

//    @DeleteMapping(path = "/{hotelId}/room/{roomId}")
//    public void deleteRoom(
//            @PathVariable("hotelId") Integer hotelId,
//            @PathVariable("roomId") Integer roomId
//    ){
//        hotelService.deleteRoom(hotelId, roomId);
//    }

//    @DeleteMapping(path = "/{hotelId}/image")
//    public void deleteHotelImages(
//            @PathVariable("hotelId") Integer hotelId,
//            @RequestBody @Validated List<IdItem> imageIdList){
//        imageService.deleteHotelImages(hotelId, imageIdList);
//    }

//    @DeleteMapping(path = "/{hotelId}/room/{roomId}/image")
//    public void deleteRoomImages(
//            @PathVariable("hotelId") Integer hotelId,
//            @PathVariable("roomId") Integer roomId,
//            @RequestBody @Validated List<IdItem> imageIdList){
//        imageService.deleteRoomImages(hotelId, roomId, imageIdList);
//    }
}
