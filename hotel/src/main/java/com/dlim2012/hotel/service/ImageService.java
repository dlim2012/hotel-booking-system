package com.dlim2012.hotel.service;

import com.dlim2012.clients.dto.IdItem;
import com.dlim2012.clients.exception.ImageAlreadyExistsException;
import com.dlim2012.clients.exception.ImageSizeExceededException;
import com.dlim2012.clients.exception.ResourceNotFoundException;
import com.dlim2012.hotel.dto.file.HotelImageUrlItem;
import com.dlim2012.hotel.dto.file.RoomImageUrlItem;
import com.dlim2012.hotel.entity.Hotel;
import com.dlim2012.hotel.entity.Rooms;
import com.dlim2012.hotel.entity.file.HotelImage;
import com.dlim2012.hotel.entity.file.ImageType;
import com.dlim2012.hotel.entity.file.RoomsImage;
import com.dlim2012.hotel.repository.HotelRepository;
import com.dlim2012.hotel.repository.RoomsRepository;
import com.dlim2012.hotel.repository.file.HotelImageRepository;
import com.dlim2012.hotel.repository.file.RoomImageRepository;
import com.dlim2012.hotel.util.ImageUtils;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class ImageService {

    private final HotelRepository hotelRepository;
    private final RoomsRepository roomsRepository;
    private final HotelImageRepository hotelImageRepository;
    private final RoomImageRepository roomImageRepository;

    private final String originalHotelImageFolder;
    private final String displayHotelImageFolder;
    private final String originalRoomImageFolder;
    private final String displayRoomImageFolder;

    private final String imageUrlPrefix;

    private final ModelMapper modelMapper = new ModelMapper();

    public ImageService(HotelRepository hotelRepository,
                        RoomsRepository roomsRepository,
                        HotelImageRepository hotelImageRepository,
                        RoomImageRepository roomImageRepository,
                        @Value("${custom.file.path}") String filePath,
                        @Value("${custom.file.imageUrlPrefix}") String imageUrlPrefix
    ) throws IOException {
        this.hotelRepository = hotelRepository;
        this.roomsRepository = roomsRepository;
        this.hotelImageRepository = hotelImageRepository;
        this.roomImageRepository = roomImageRepository;

        assert(Files.exists(Paths.get(filePath)));
        this.originalHotelImageFolder = Paths.get(filePath, "image", "hotel", "original").toString();
        this.displayHotelImageFolder = Paths.get(filePath, "image", "hotel", "display").toString();
        this.originalRoomImageFolder = Paths.get(filePath, "image", "room", "original").toString();
        this.displayRoomImageFolder = Paths.get(filePath, "image", "room", "display").toString();
        Files.createDirectories(Paths.get(this.originalHotelImageFolder));
        Files.createDirectories(Paths.get(this.displayHotelImageFolder));
        Files.createDirectories(Paths.get(this.originalRoomImageFolder));
        Files.createDirectories(Paths.get(this.displayRoomImageFolder));

        this.imageUrlPrefix = imageUrlPrefix;
    }

    public ImageType getImageTypeFromString(String imageTypeString){
        if (imageTypeString.equals("original")){
            return ImageType.ORIGINAL;
        } else if (imageTypeString.equals("display")){
            return ImageType.DISPLAY;
        } else {
            throw new IllegalStateException("Invalid image type.");
        }
    }

    public void postHotelImage(Integer hotelId, MultipartFile file) throws IOException {
        if (file.getSize() > 1024 * 1024 * 10) {
            throw new ImageSizeExceededException("Image size is too big");
        }

        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found."));

        if (hotelImageRepository.existsByHotelIdAndName(hotelId, ImageType.ORIGINAL.name())){
            throw new ImageAlreadyExistsException("Image with the same name already exists.");
        }

        byte[] compressedOriginalImageBytes = ImageUtils.compressImage(file.getBytes());
        byte[] compressedDisplayImageBytes = ImageUtils.compressImage(
                ImageUtils.createDisplayImage(file.getBytes()));


        HotelImage originalImage = HotelImage.builder()
                .hotel(hotel)
                .name(file.getOriginalFilename())
                .imageType(ImageType.ORIGINAL)
                .contentType(file.getContentType())
                .filePath("")
                .build();
        HotelImage displayImage = HotelImage.builder()
                .hotel(hotel)
                .name(file.getOriginalFilename())
                .imageType(ImageType.DISPLAY)
                .contentType(file.getContentType())
                .filePath("")
                .build();
        List<HotelImage> hotelImageList = new ArrayList<>();
        hotelImageList.add(originalImage);
        hotelImageList.add(displayImage);
        hotelImageList = hotelImageRepository.saveAll(hotelImageList);

        originalImage.setFilePath(Paths.get(originalHotelImageFolder, originalImage.getId().toString()).toString());
        displayImage.setFilePath(Paths.get(displayHotelImageFolder, displayImage.getId().toString()).toString());
        hotelImageList = hotelImageRepository.saveAll(hotelImageList);

        Files.write(Paths.get(originalImage.getFilePath()), compressedOriginalImageBytes);
        Files.write(Paths.get(displayImage.getFilePath()), compressedDisplayImageBytes);

        if (hotel.getMainImageId() == null){
            for (HotelImage hotelImage: hotelImageList){
                if (hotelImage.getImageType().equals(ImageType.DISPLAY)){
                    hotel.setMainImageId(hotelImage.getId());
                    hotelRepository.save(hotel);
                    break;
                }
            }


        }

    }

    public List<HotelImageUrlItem> getHotelImageUrls(
            Integer hotelId, ImageType imageType
    ){
        if (!hotelRepository.existsById(hotelId))
            throw new ResourceNotFoundException("Hotel not found.");
        return hotelImageRepository.findByHotelIdAndImageType(hotelId, imageType)
                .stream().map(entity -> modelMapper.map(entity, HotelImageUrlItem.class)).toList();
    }

    public byte[] getHotelImage(Integer imageId, ImageType imageType) throws IOException {
        HotelImage hotelImage = hotelImageRepository.findByIdAndImageType(imageId, imageType)
                .orElseThrow(() -> new ResourceNotFoundException("Image not found."));
        try {
            byte[] image = Files.readAllBytes(Paths.get(hotelImage.getFilePath()));
            return ImageUtils.decompressImage(image);
        } catch (IOException e){
            log.error("Image not found in the file system.");
            throw new IllegalStateException("Image not found in the file system");
        }
    }

    public void deleteHotelImages(List<HotelImage> hotelImageList){
        for (HotelImage hotelImage: hotelImageList){
            try {
                if (Files.deleteIfExists(Paths.get(hotelImage.getFilePath()))) {
                    log.info("Hotel image {} deleted from file system.", hotelImage.getId());
                }
            } catch (Exception e){
                log.info("Exception while deleting hotel image {}: " + e.getMessage(), hotelImage.getId());
            }
        }
        hotelImageRepository.deleteAll(hotelImageList);
    }

    public void deleteHotelImages(Integer hotelId, List<IdItem> idItemList){
        List<HotelImage> hotelImageList = new ArrayList<>();
        for (IdItem idItem: idItemList){
            hotelImageRepository.findByHotelIdAndId(hotelId, idItem.getId())
                    .ifPresent(hotelImageList::add);
        }
        deleteHotelImages(hotelImageList);
    }

    public void deleteHotelImages(Integer hotelId){
        List<HotelImage> hotelImageList = hotelImageRepository.findByHotelId(hotelId);
        deleteHotelImages(hotelImageList);
    }

    public void postRoomImage(Integer hotelId, Integer roomId, MultipartFile file) throws IOException {
        if (file.getSize() > 1024 * 1024 * 10) {
            throw new ImageSizeExceededException("Image size is too big");
        }

        Optional<Rooms> optionalRoom = roomsRepository.findByHotelIdAndId(hotelId, roomId);
        if (optionalRoom.isEmpty()) {
            throw new ResourceNotFoundException("Hotel not found.");
        }
        Rooms rooms = optionalRoom.get();

        byte[] compressedOriginalImageBytes = ImageUtils.compressImage(file.getBytes());
        byte[] compressedDisplayImageBytes = ImageUtils.compressImage(
                ImageUtils.createDisplayImage(file.getBytes()));

        RoomsImage originalImage = RoomsImage.builder()
                .rooms(rooms)
                .name(file.getOriginalFilename())
                .imageType(ImageType.ORIGINAL)
                .contentType(file.getContentType())
                .filePath("")
                .build();
        RoomsImage displayImage = RoomsImage.builder()
                .rooms(rooms)
                .name(file.getOriginalFilename())
                .imageType(ImageType.DISPLAY)
                .contentType(file.getContentType())
                .filePath("")
                .build();
        List<RoomsImage> roomsImageList = new ArrayList<>();
        roomsImageList.add(originalImage);
        roomsImageList.add(displayImage);
        roomImageRepository.saveAll(roomsImageList);

        originalImage.setFilePath(Paths.get(originalRoomImageFolder, originalImage.getId().toString()).toString());
        displayImage.setFilePath(Paths.get(displayRoomImageFolder, displayImage.getId().toString()).toString());
        roomImageRepository.saveAll(roomsImageList);

        Files.write(Paths.get(originalImage.getFilePath()), compressedOriginalImageBytes);
        Files.write(Paths.get(displayImage.getFilePath()), compressedDisplayImageBytes);
    }


    public List<RoomImageUrlItem> getRoomImageUrls(Integer hotelId, Integer roomId, ImageType imageType) {
        if (!roomsRepository.existsByHotelIdAndId(hotelId, roomId))
            throw new ResourceNotFoundException("Room not found.");
        return roomImageRepository.findByRoomsIdAndImageType(roomId, imageType)
                .stream().map(entity -> modelMapper.map(entity, RoomImageUrlItem.class)).toList();
    }

    public byte[] getRoomImage(Integer hotelId, Integer roomId, Integer imageId, ImageType imageType) {
        if (!roomsRepository.existsByHotelIdAndId(hotelId, roomId))
            throw new ResourceNotFoundException("Room not found.");
        RoomsImage roomsImage = roomImageRepository.findByRoomsIdAndIdAndImageType(roomId, imageId, imageType)
                .orElseThrow(() -> new ResourceNotFoundException("Image not found."));
        try {
            byte[] image = Files.readAllBytes(Paths.get(roomsImage.getFilePath()));
            return ImageUtils.decompressImage(image);
        } catch (IOException e){
            log.error("Image not found in the file system.");
            throw new IllegalStateException("Image not found in the file system");
        }
    }

    public void deleteRoomImages(List<RoomsImage> roomsImageList){
        for (RoomsImage roomsImage : roomsImageList){
            try {
                if (Files.deleteIfExists(Paths.get(roomsImage.getFilePath()))) {
                    log.info("Room image {} deleted from file system.", roomsImage.getId());
                }
            } catch (Exception e){
                log.info("Exception while deleting room image {}:" + e.getMessage(), roomsImage.getId());
            }
        }
    }

    public void deleteRoomImages(Integer hotelId, Integer roomId, List<IdItem> idItemList){
        if (!roomsRepository.existsByHotelIdAndId(hotelId, roomId)){
            throw new ResourceNotFoundException("Room not found.");
        }
        List<RoomsImage> roomsImageList = new ArrayList<>();
        for (IdItem idItem: idItemList){
            roomImageRepository.findByRoomsIdAndId(roomId, idItem.getId())
                    .ifPresent(roomsImageList::add);
        }
        deleteRoomImages(roomsImageList);
    }

    public void deleteRoomImages(Integer roomId){
        List<RoomsImage> roomsImageList = roomImageRepository.findByRoomsId(roomId);
        deleteRoomImages(roomsImageList);
    }

    public List<HotelImageUrlItem> getHotelDisplayImages(Integer hotelId) {
        return hotelImageRepository.findByHotelIdAndImageType(hotelId, ImageType.DISPLAY).stream()
                .map(hotelImage -> HotelImageUrlItem.builder().id(hotelImage.getId())
                        .url(this.imageUrlPrefix + '/' + hotelImage.getId().toString())
                        .build())
                .toList();

    }
}
