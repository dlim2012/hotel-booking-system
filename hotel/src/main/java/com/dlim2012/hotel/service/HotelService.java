package com.dlim2012.hotel.service;

import com.dlim2012.hotel.DeleteRequest;
import com.dlim2012.hotel.entity.Hotel;
import com.dlim2012.hotel.entity.Room;
import com.dlim2012.hotel.entity.facility.Facility;
import com.dlim2012.hotel.entity.facility.HotelFacility;
import com.dlim2012.hotel.entity.facility.RoomFacility;
import com.dlim2012.hotel.entity.file.HotelImage;
import com.dlim2012.hotel.entity.file.ImageType;
import com.dlim2012.hotel.entity.file.RoomImage;
import com.dlim2012.hotel.entity.locality.City;
import com.dlim2012.hotel.entity.locality.Country;
import com.dlim2012.hotel.entity.locality.Locality;
import com.dlim2012.hotel.entity.locality.State;
import com.dlim2012.hotel.exception.EntityAlreadyExistsException;
import com.dlim2012.hotel.exception.ImageSizeExceededException;
import com.dlim2012.hotel.exception.ResourceNotFoundException;
import com.dlim2012.hotel.repository.HotelRepository;
import com.dlim2012.hotel.repository.RoomRepository;
import com.dlim2012.hotel.repository.facility.FacilityRepository;
import com.dlim2012.hotel.repository.facility.HotelFacilityRepository;
import com.dlim2012.hotel.repository.facility.RoomFacilityRepository;
import com.dlim2012.hotel.repository.file.HotelImageRepository;
import com.dlim2012.hotel.repository.file.RoomImageRepository;
import com.dlim2012.hotel.repository.locality.CityRepository;
import com.dlim2012.hotel.repository.locality.CountryRepository;
import com.dlim2012.hotel.repository.locality.LocalityRepository;
import com.dlim2012.hotel.repository.locality.StateRepository;
import com.dlim2012.hotel.util.ImageUtils;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.lang.module.ResolutionException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class HotelService {
    private final HotelRepository hotelRepository;
    private final RoomRepository roomRepository;

    private final FacilityRepository facilityRepository;
    private final HotelFacilityRepository hotelFacilityRepository;
    private final RoomFacilityRepository roomFacilityRepository;

    private final CountryRepository countryRepository;
    private final StateRepository stateRepository;
    private final CityRepository cityRepository;
    private final LocalityRepository localityRepository;

    private final HotelImageRepository hotelImageRepository;
    private final RoomImageRepository roomImageRepository;

    private final String originalHotelImageFolder;
    private final String displayHotelImageFolder;
    private final String originalRoomImageFolder;
    private final String displayRoomImageFolder;


    private final KafkaTemplate<String, RoomItem> roomKafkaTemplate;
    private final KafkaTemplate<String, HotelFullAddressItem> hotelKafkaTemplate;

    @Autowired
    public HotelService(HotelRepository hotelRepository, RoomRepository roomRepository,
                        FacilityRepository facilityRepository, HotelFacilityRepository hotelFacilityRepository,
                        RoomFacilityRepository roomFacilityRepository, CountryRepository countryRepository,
                        StateRepository stateRepository, CityRepository cityRepository,
                        LocalityRepository localityRepository,
                        HotelImageRepository hotelImageRepository,
                        RoomImageRepository roomImageRepository,
                        @Value("${file.path}") String filePath, KafkaTemplate<String, RoomItem> roomKafkaTemplate,
                        KafkaTemplate<String, HotelFullAddressItem> hotelKafkaTemplate) throws IOException {
        this.hotelRepository = hotelRepository;
        this.roomRepository = roomRepository;
        this.facilityRepository = facilityRepository;
        this.hotelFacilityRepository = hotelFacilityRepository;
        this.roomFacilityRepository = roomFacilityRepository;
        this.countryRepository = countryRepository;
        this.stateRepository = stateRepository;
        this.cityRepository = cityRepository;
        this.localityRepository = localityRepository;
        this.hotelImageRepository = hotelImageRepository;
        this.roomImageRepository = roomImageRepository;
        this.roomKafkaTemplate = roomKafkaTemplate;
        this.hotelKafkaTemplate = hotelKafkaTemplate;

        assert(Files.exists(Paths.get(filePath)));
        this.originalHotelImageFolder = Paths.get(filePath, "image", "hotel", "original").toString();
        this.displayHotelImageFolder = Paths.get(filePath, "image", "hotel", "display").toString();
        this.originalRoomImageFolder = Paths.get(filePath, "image", "room", "original").toString();
        this.displayRoomImageFolder = Paths.get(filePath, "image", "room", "display").toString();
        Files.createDirectories(Paths.get(this.originalHotelImageFolder));
        Files.createDirectories(Paths.get(this.displayHotelImageFolder));
        Files.createDirectories(Paths.get(this.originalRoomImageFolder));
        Files.createDirectories(Paths.get(this.displayRoomImageFolder));
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

    // Post
    public void postCountry(CountryItem CountryItem) {
        if (countryRepository.existsByName(CountryItem.name())) {
            throw new EntityAlreadyExistsException("Country already exists.");
        }
        Country country = Country.builder().name(CountryItem.name()).initials(CountryItem.initials()).build();
        countryRepository.save(country);
    }


    public void postState(StateItem StateItem) {
        Country country = countryRepository.findById(StateItem.countryId())
                .orElseThrow(() -> new ResourceNotFoundException("Country not found."));
        State state = State.builder()
                .country(country)
                .name(StateItem.name())
                .initials(StateItem.initials())
                .areaCode(StateItem.areaCode())
                .build();
        stateRepository.save(state);
    }


    public void postCity(CityItem CityItem){
        State state = stateRepository.findById(CityItem.stateId())
                .orElseThrow(() -> new ResourceNotFoundException("State not found."));
        City city = City.builder()
                .name(CityItem.name())
                .state(state)
                .build();
        cityRepository.save(city);
    }

    public void postFacility(FacilityItem FacilityItem) {
        if (facilityRepository.existsByDisplayName(FacilityItem.displayName())) {
            throw new EntityAlreadyExistsException("Facility already exists");
        }
        Facility facility = Facility.builder().displayName(FacilityItem.displayName()).build();
        facilityRepository.save(facility);
    }

    public Hotel postHotel(HotelFullAddressItem hotelFullAddressItem) {
        /*
        * While countries are pre-populated beforehand, state, city, and localities can be added by a post hotel request
        * */

        Country country = countryRepository.findByName(hotelFullAddressItem.countryName())
                .orElseThrow(() -> new ResolutionException("Country not found."));

        // Get State
        State state;
        Optional<State> optionalState = stateRepository.findByNameAndCountryId(
                hotelFullAddressItem.stateName(),
                country.getId()
        );
        if (optionalState.isEmpty()) {
            state = State.builder().name(hotelFullAddressItem.stateName()).country(country).build();
            stateRepository.save(state);
        } else {
            state = optionalState.get();
        }

        // Get City
        City city;
        Optional<City> optionalCity = cityRepository.findByNameAndStateId(hotelFullAddressItem.cityName(), state.getId());
        if (optionalCity.isEmpty()) {
            city = City.builder().name(hotelFullAddressItem.cityName()).state(state).build();
            cityRepository.save(city);
        } else {
            city = optionalCity.get();
        }

        // Check duplicate
        Locality locality = null;
        if (optionalState.isPresent() && optionalCity.isPresent()) {
            Optional<Locality> optionalLocality = localityRepository.findByZipcode(
                    hotelFullAddressItem.zipCode()
            );
            if (optionalLocality.isPresent()) {
                if (hotelRepository.existsByNameAndDescriptionAndLocalityId(
                        hotelFullAddressItem.name(), hotelFullAddressItem.description(), optionalLocality.get().getId())
                ) {
                    throw new EntityAlreadyExistsException("Hotel already exists");
                }
                locality = optionalLocality.get();
            }
        }
        if (locality == null){
            locality = Locality.builder()
                    .zipcode(hotelFullAddressItem.zipCode())
                    .city(city)
                    .build();
            localityRepository.save(locality);
        }

        Hotel hotel = Hotel.builder()
                .name(hotelFullAddressItem.name())
                .description(hotelFullAddressItem.description())
                .isActive(hotelFullAddressItem.isActive())
                .locality(locality)
                .addressLine1(hotelFullAddressItem.addressLine1())
                .addressLine2(hotelFullAddressItem.addressLine2())
                .build();

        Hotel savedHotel = hotelRepository.save(hotel);
        hotelKafkaTemplate.send("hotel", hotelFullAddressItem.withId(savedHotel.getId()));

        return hotel;
    }

    public void postRoom(Integer hotelId, List<RoomItem> RoomItemList) {
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found"));

        for (RoomItem roomItem : RoomItemList) {
            Optional<Room> optionalRoom = roomRepository.findByHotelIdAndDisplayName(hotelId, roomItem.displayName());
            Room room;
            if (optionalRoom.isPresent()){
                room = optionalRoom.get();
                room.setActive(roomItem.isActive());
                room.setDescription(roomItem.description());
                room.setMaxAdult(roomItem.maxAdult());
                room.setMaxChild(roomItem.maxChild());
                room.setQuantity(roomItem.quantity());
                room.setPriceMin(roomItem.priceMin());
                room.setPriceMax(roomItem.priceMax());
            } else {
                room = Room.builder()
                        .hotel(hotel)
                        .displayName(roomItem.displayName())
                        .isActive(roomItem.isActive())
                        .maxAdult(roomItem.maxAdult())
                        .maxChild(roomItem.maxChild())
                        .quantity(roomItem.quantity())
                        .priceMin(roomItem.priceMin())
                        .priceMax(roomItem.priceMax())
                        .build();
            }
            Room savedRoom = roomRepository.save(room);
            roomKafkaTemplate.send("room", roomItem.withId(savedRoom.getId()));
        }
    }

    public void postHotelImage(Integer hotelId, MultipartFile file) throws IOException {
        if (file.getSize() > 1024 * 1024 * 10) {
            throw new ImageSizeExceededException("Image size is too big");
        }

        Optional<Hotel> optionalHotel = hotelRepository.findById(hotelId);
        if (optionalHotel.isEmpty()) {
            throw new ResourceNotFoundException("Hotel not found.");
        }
        Hotel hotel = optionalHotel.get();


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
        hotelImageRepository.saveAll(hotelImageList);

        originalImage.setFilePath(Paths.get(originalHotelImageFolder, originalImage.getId().toString()).toString());
        displayImage.setFilePath(Paths.get(displayHotelImageFolder, displayImage.getId().toString()).toString());
        hotelImageRepository.saveAll(hotelImageList);

        Files.write(Paths.get(originalImage.getFilePath()), compressedOriginalImageBytes);
        Files.write(Paths.get(displayImage.getFilePath()), compressedDisplayImageBytes);

    }

    public void postRoomImage(Integer hotelId, Integer roomId, MultipartFile file) throws IOException {
        if (file.getSize() > 1024 * 1024 * 10) {
            throw new ImageSizeExceededException("Image size is too big");
        }

        Optional<Room> optionalRoom = roomRepository.findByHotelIdAndId(hotelId, roomId);
        if (optionalRoom.isEmpty()) {
            throw new ResourceNotFoundException("Hotel not found.");
        }
        Room room = optionalRoom.get();

        byte[] compressedOriginalImageBytes = ImageUtils.compressImage(file.getBytes());
        byte[] compressedDisplayImageBytes = ImageUtils.compressImage(
                ImageUtils.createDisplayImage(file.getBytes()));

        RoomImage originalImage = RoomImage.builder()
                .room(room)
                .name(file.getOriginalFilename())
                .imageType(ImageType.ORIGINAL)
                .contentType(file.getContentType())
                .filePath("")
                .build();
        RoomImage displayImage = RoomImage.builder()
                .room(room)
                .name(file.getOriginalFilename())
                .imageType(ImageType.DISPLAY)
                .contentType(file.getContentType())
                .filePath("")
                .build();
        List<RoomImage> roomImageList = new ArrayList<>();
        roomImageList.add(originalImage);
        roomImageList.add(displayImage);
        roomImageRepository.saveAll(roomImageList);

        originalImage.setFilePath(Paths.get(originalRoomImageFolder, originalImage.getId().toString()).toString());
        displayImage.setFilePath(Paths.get(displayRoomImageFolder, displayImage.getId().toString()).toString());
        roomImageRepository.saveAll(roomImageList);

        Files.write(Paths.get(originalImage.getFilePath()), compressedOriginalImageBytes);
        Files.write(Paths.get(displayImage.getFilePath()), compressedDisplayImageBytes);
    }

    // Get
    public List<CountryItem> getAllCountries() {
        return countryRepository.findAll().stream().map(entity -> new CountryItem(
                entity.getId(), entity.getName(), entity.getInitials())).toList();
    }

    public List<StateItem> getStates(Integer countryId){
        List<State> stateList = stateRepository.findByCountryId(countryId);
        return stateList.stream().map(
                entity -> new StateItem(
                        entity.getId(), entity.getName(),
                        entity.getInitials(),
                        entity.getAreaCode(),
                        entity.getCountry().getId()
                )
        ).toList();
    }

    public List<CityItem> getCities(Integer stateId){
        List<City> cityList = cityRepository.findByStateId(stateId);
        return cityList.stream().map(
                entity -> new CityItem(
                        entity.getId(), entity.getName(),
                        entity.getState().getId()
                )
        ).toList();
    }

    public List<FacilityItem> getFacilities() {
        return facilityRepository.findAll().stream().map(entity ->
                new FacilityItem(entity.getId(), entity.getDisplayName())).toList();
    }

    public HotelItem getHotel(Integer hotelId){
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found."));

        return new HotelItem(
                hotel.getId(),
                hotel.getName(),
                hotel.getDescription(),
                hotel.isActive(),
                hotel.getAddressLine1(),
                hotel.getAddressLine2(),
                hotel.getLocality().getId()
        );
    }

    public List<HotelImageUrlItem> getHotelImageUrls(
            Integer hotelId, ImageType imageType
    ){
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found."));

        List<HotelImage> hotelImages = hotelImageRepository.findByHotelIdAndImageType(hotelId, imageType);

        List<HotelImageUrlItem> hotelImageUrlItemList = new ArrayList<>();
        for (HotelImage hotelImage: hotelImages){
            hotelImageUrlItemList.add(new HotelImageUrlItem(
                    hotelImage.getId(),
                    hotelImage.getUrl() == null ? "" : hotelImage.getUrl()
            ));
        }
        return hotelImageUrlItemList;
    }


    public List<RoomImageUrlItem> getRoomImageUrls(Integer hotelId, Integer roomId, ImageType imageType) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found."));

        if (!room.getHotel().getId().equals(hotelId)){
            throw new ResourceNotFoundException("Room not found.");
        }

        List<RoomImage> roomImages = roomImageRepository.findByRoomIdAndImageType(roomId, imageType);


        List<RoomImageUrlItem> hotelImageDtoList = new ArrayList<>();
        for (RoomImage roomImage: roomImages){
            hotelImageDtoList.add(new RoomImageUrlItem(
                    roomImage.getId(),
                    roomImage.getUrl() == null ? "" : roomImage.getUrl()
            ));
        }
        return hotelImageDtoList;
    }

    public byte[] getHotelImage(Integer hotelId, Integer imageId, ImageType imageType) throws IOException {
        HotelImage hotelImage = hotelImageRepository.findById(imageId)
                .orElseThrow(() -> new ResourceNotFoundException("Image not found."));
        if (!hotelImage.getImageType().equals(imageType)
            || !hotelImage.getHotel().getId().equals(hotelId)){
            throw new ResourceNotFoundException("Image not found.");
        }
        byte[] image;
        try {
            image = Files.readAllBytes(Paths.get(hotelImage.getFilePath()));
        } catch (IOException e){
            log.error("Image not found in the file system.");
            throw new IllegalStateException("Image not found in the file system");
        }
        return ImageUtils.decompressImage(image);
    }

    public byte[] getRoomImage(Integer hotelId, Integer roomId, Integer imageId, ImageType imageType) {
        RoomImage roomImage = roomImageRepository.findById(imageId)
                .orElseThrow(() -> new ResourceNotFoundException("Image not found."));
        if (!roomImage.getImageType().equals(imageType)
                || !roomImage.getRoom().getId().equals(roomId)
                || !roomImage.getRoom().getHotel().getId().equals(hotelId)){
            throw new ResourceNotFoundException("Image not found.");
        }
        byte[] image;
        try {
            image = Files.readAllBytes(Paths.get(roomImage.getFilePath()));
        } catch (IOException e){
            log.error("Image not found in the file system.");
            throw new IllegalStateException("Image not found in the file system");
        }
        return ImageUtils.decompressImage(image);
    }

    public List<FacilityItem> getHotelFacilities() {
        //todo
        List<FacilityItem> facilityItemList = new ArrayList<>();

        return facilityItemList;
    }

    public List<FacilityItem> getRoomFacilities() {
        //todo
        List<FacilityItem> facilityItemList = new ArrayList<>();
        return facilityItemList;
    }


    public void putCountry(CountryItem countryItem) {
        Country country = countryRepository.findById(countryItem.id())
                .orElseThrow(() -> new ResourceNotFoundException("Country not found."));
        country.setName(countryItem.name());
        country.setInitials(countryItem.initials());
        countryRepository.save(country);
    }

    public void putState(StateItem stateItem){
        State state = stateRepository.findById(stateItem.id())
                .orElseThrow(() -> new ResourceNotFoundException("State not found."));
        Country country = countryRepository.findById(stateItem.countryId())
                .orElseThrow(() -> new ResourceNotFoundException("Country not found."));
        state.setCountry(country);
        state.setName(stateItem.name());
        state.setInitials(stateItem.initials());
        state.setAreaCode(stateItem.areaCode());
        stateRepository.save(state);
    }

    public void putCity(CityItem cityItem){
        City city = cityRepository.findById(cityItem.id())
                .orElseThrow(() -> new ResourceNotFoundException("City not found."));
        State state = stateRepository.findById(cityItem.stateId())
                .orElseThrow(() -> new ResourceNotFoundException("State not found."));
        city.setState(state);
        city.setName(cityItem.name());
        cityRepository.save(city);
    }

    public void putHotel(Integer hotelId, HotelItem hotelItem){
        // todo: use same template as postHotel
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found."));
        Locality locality = localityRepository.findById(hotelItem.localityId())
                .orElseThrow(() -> new ResourceNotFoundException("Locality not found."));
        City city = locality.getCity();
        State state = city.getState();
        Country country = state.getCountry();

        hotel.setName(hotelItem.name());
        hotel.setDescription(hotelItem.description());
        hotel.setActive(hotelItem.isActive());
        hotel.setAddressLine1(hotelItem.addressLine1());
        hotel.setAddressLine2(hotelItem.addressLine2());
        hotelRepository.save(hotel);
        hotelKafkaTemplate.send("hotel",
                new HotelFullAddressItem(
                        hotelId, hotelItem.name(), hotelItem.description(), hotelItem.isActive(), hotelItem.addressLine1(),
                        hotelItem.addressLine2(), locality.getZipcode(), city.getName(), state.getName(), country.getName()
                ));
    }

    public void putRoom(Integer hotelId, Integer roomId, RoomItem roomItem){

        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found."));
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found."));
        room.setDisplayName(roomItem.displayName());
        room.setActive(roomItem.isActive());
        room.setQuantity(roomItem.quantity());
        room.setPriceMin(roomItem.priceMin());
        room.setPriceMax(roomItem.priceMax());
        roomRepository.save(room);
        roomKafkaTemplate.send("room", roomItem.id().equals(roomId) ? roomItem : roomItem.withId(roomId));
    }

    public void putHotelFacilities(Integer hotelId, List<IdItem> facilityItemList) {
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found."));
        List<HotelFacility> toDelete = new ArrayList<>();
        List<HotelFacility> newList = new ArrayList<>();
        List<HotelFacility> prevList = hotelFacilityRepository.findByHotelId(hotelId);

        HashSet<Integer> idSet = new HashSet<>(facilityItemList
                .stream().map(IdItem::id).toList());
        for (HotelFacility hotelFacility: prevList){
            Integer id = hotelFacility.getFacility().getId();
            if (!idSet.contains(id)){
                toDelete.add(hotelFacility);
            } else {
                newList.add(hotelFacility);
                idSet.remove(id);
            }
        }
        hotelFacilityRepository.deleteAll(toDelete);

        idSet = new HashSet<Integer>(prevList
                .stream().map(HotelFacility::getId).toList());
        for (IdItem idItem: facilityItemList){
            if (!idSet.contains(idItem.id())){
                newList.add(HotelFacility.builder().hotel(hotel).facility(
                        facilityRepository.getReferenceById(idItem.id())
                ).build());
                idSet.add(idItem.id());
            }
        }
        hotel.setHotelFacilities(newList);
        hotelRepository.save(hotel);
    }

    public void putRoomFacilities(Integer hotelId, Integer roomId, List<IdItem> facilityItemList) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found."));List<RoomFacility> roomFacilityList = room.getRoomFacilities();
        if (!room.getHotel().getId().equals(hotelId)){
            throw new ResourceNotFoundException("Room not found");
        }

        List<RoomFacility> toDelete = new ArrayList<>();
        List<RoomFacility> newList = new ArrayList<>();
        List<RoomFacility> prevList = roomFacilityRepository.findByRoomId(roomId);

        HashSet<Integer> idSet = new HashSet<>(facilityItemList
                .stream().map(IdItem::id).toList());
        for (RoomFacility roomFacility: prevList){
            Integer id = roomFacility.getFacility().getId();
            if (!idSet.contains(id)){
                toDelete.add(roomFacility);
            } else {
                newList.add(roomFacility);
                idSet.remove(id);
            }
        }
        roomFacilityRepository.deleteAll(toDelete);

        idSet = new HashSet<Integer>(prevList
                .stream().map(RoomFacility::getId).toList());
        for (IdItem idItem: facilityItemList){
            if (!idSet.contains(idItem.id())){
                newList.add(RoomFacility.builder().room(room).facility(
                        facilityRepository.getReferenceById(idItem.id())
                ).build());
                idSet.add(idItem.id());
            }
        }
        room.setRoomFacilities(newList);
        roomRepository.save(room);
    }


    // Delete
    public void deleteCountries(List<DeleteRequest> deleteRequestList) {
        List<Integer> toDelete = new ArrayList<>();
        for (DeleteRequest deleteRequest : deleteRequestList) {
            if (countryRepository.existsByIdAndName(deleteRequest.id(), deleteRequest.name())){
                toDelete.add(deleteRequest.id());
            }
        }
        countryRepository.deleteAllById(toDelete);
    }

    public void deleteFacilities(List<DeleteRequest> deleteRequestList) {
        List<Integer> toDelete = new ArrayList<>();
        for (DeleteRequest deleteRequest : deleteRequestList) {
            if (facilityRepository.existsByIdAndDisplayName(deleteRequest.id(), deleteRequest.name())){
                toDelete.add(deleteRequest.id());
            }
        }
        facilityRepository.deleteAllById(toDelete);
    }

    public void deleteHotel(Integer hotelId){
        hotelRepository.deleteById(hotelId);
        hotelKafkaTemplate.send("hotel", HotelFullAddressItem.onlyId(hotelId));
    }

    public void deleteRoom(Integer hotelId, Integer roomId) {
        if (roomRepository.existsByHotelIdAndId(hotelId, roomId)){
            roomRepository.deleteById(roomId);
            roomKafkaTemplate.send("room", RoomItem.onlyId(roomId));
        }
    }

    public void deleteHotelImage(Integer hotelId, ImageType imageType, Integer imageId) {
        if (hotelImageRepository.existsByHotelIdAndImageTypeAndId(hotelId, imageType, imageId)){
            hotelImageRepository.deleteById(imageId);
        }
    }

    public void deleteRoomImage(Integer hotelId, Integer roomId, ImageType imageType, Integer imageId) {
        if (roomImageRepository.existsByRoomIdAndImageTypeAndId(roomId, imageType, imageId)){
            if (roomRepository.existsByHotelIdAndId(hotelId, roomId)){
                roomImageRepository.deleteById(imageId);
            }
        }
    }
}
