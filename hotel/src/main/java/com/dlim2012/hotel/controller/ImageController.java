package com.dlim2012.hotel.controller;

import com.dlim2012.hotel.dto.file.HotelImageUrlItem;
import com.dlim2012.hotel.entity.file.ImageType;
import com.dlim2012.hotel.service.ImageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/api/v1/hotel")
@RequiredArgsConstructor
@CrossOrigin
public class ImageController {
    private final ImageService imageService;

    @PostMapping(path = "/hotel/{hotelId}/image")
    void postHotelImage(@PathVariable("hotelId") Integer hotelId, @RequestParam("image") MultipartFile file) throws IOException {
        imageService.postHotelImage(hotelId, file);
    }

    @GetMapping(path = "/image/{imageId}")
    HttpEntity<byte[]> getHotelDisplayImage(@PathVariable("imageId") Integer imageId) throws IOException {
        log.info("Image with image id {} requested", imageId);
        byte[] image = imageService.getHotelImage(imageId, ImageType.DISPLAY);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_PNG);
        headers.setContentLength(image.length);
        return new HttpEntity<byte[]>(image, headers);
    }

    @GetMapping(path = "/hotel/{hotelId}/images")
    List<HotelImageUrlItem> getHotelDisplayImages(@PathVariable("hotelId") Integer hotelId) {

        return imageService.getHotelDisplayImages(hotelId);
    }

//    @PostMapping(path = "/{hotelId}/room/{roomId}/image")
//    void postRoomImage(@PathVariable("hotelId") Integer hotelId,
//                        @PathVariable("roomId") Integer roomId,
//                        @RequestParam("image") MultipartFile file) throws IOException{
//        imageService.postRoomImage(hotelId, roomId, file);
//    }

}
